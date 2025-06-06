package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.*;
import com.cyh.hao_vcs.mapper.*;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.service.UserConfigService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.ProjectUtil;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.cyh.hao_vcs.utils.FileUtil.createDir;

@Service
public class ProjectBaseServiceImpl implements ProjectBaseService {

    @Autowired
    ProjectBaseMapper projectBaseMapper;

    @Autowired
    User2ProjectMapper user2ProjectMapper;

    @Autowired
    ProjectChangeBaseImfServiceImpl projectChangeBaseImfService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ApplyJoinProjectMapper applyJoinProjectMapper;

    @Autowired
    UserLikeProjectMapper userLikeProjectMapper;

    @Autowired
    ProjectBaseService projectBaseService;

    @Autowired
    UserConfigService userConfigService;


    private static final String DEFAULT_ACTION = "创建工程";


    @Override
    public R insertBaseProject(ProjectBaseImf baseImf, Long userID, int status) {
        if (StringUtils.isNullOrEmpty(baseImf.getProjectName())) {
            return R.error("工程名称不能为空");
        }
        if (Objects.isNull(userID)) {
            return R.error("用户状态异常！");
        }
        try {
            LocalDateTime time = LocalDateTime.now();
            baseImf.setCreateTime(time);
            projectBaseMapper.insert(baseImf);
            //先插入project,后续才能获得id
            user2ProjectMapper.insert(new UserProject(userID, baseImf.getProjectId(), status));
            projectChangeBaseImfService.insertProjectChangeBaseImf(userID, baseImf.getProjectId(), time, DEFAULT_ACTION);
            String projectPath = FileUtil.getProjectPath(baseImf.getProjectId(), baseImf.getProjectName());
            createDir(projectPath);
        } catch (Exception e) {
            return R.error("创建工程异常");
        }
        return R.success("创建成功");
    }

    @Override
    public List<Long> getAllProjectID(Long userID) {
        QueryWrapper<UserProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userID);
        return collectList(queryWrapper);
    }

    @Override
    public List<Long> getSelfProjectID(Long userID) {
        QueryWrapper<UserProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userID).eq("relation", StatusEnum.CREATE_PROJECT);
        return collectList(queryWrapper);
    }

    @Override
    public List<Long> getJoinProjectID(Long userID) {
        QueryWrapper<UserProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userID).eq("relation", StatusEnum.JOIN_PROJECT);
        return collectList(queryWrapper);
    }

    @Override
    public List<ProjectBaseImf> getProjects(List<Long> idList) {
        return projectBaseMapper.selectBatchIds(idList);
    }


    private List<Long> collectList(QueryWrapper<UserProject> queryWrapper) {
        List<Long> idList = user2ProjectMapper.selectList(queryWrapper)
                .stream()
                .map(userProject -> userProject.getProjectId())
                .collect(Collectors.toList());
        return idList;
    }

    @Override
    public String getProjectPath(Long projectId) {
        String projectName = projectBaseMapper.selectById(projectId).getProjectName();
        if (!StringUtils.isNullOrEmpty(projectName)) {
            return FileConfig.PROJECT_PATH + ProjectUtil.getProjectName(projectName, projectId);
        }
        return null;
    }

    @Override
    public List<ProjectBaseImf> getProjectByKey(String key, long userId) {
        QueryWrapper<ProjectBaseImf> wrapper = new QueryWrapper<>();
        wrapper.like("project_name", key);
        List<ProjectBaseImf> projectBaseImfList = projectBaseMapper.selectList(wrapper);
        QueryWrapper<UserProject> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("user_id", userId);
        List<Long> userOwnProjects = user2ProjectMapper.selectList(wrapper2)
                .stream().map(userProject -> userProject.getProjectId()).collect(Collectors.toList());
        projectBaseImfList = projectBaseImfList.stream().filter(project -> Objects.equals(project.getPrivacy(), FileConfig.PUBLIC_STATUS) &&
                        !userOwnProjects.contains(project.getProjectId()))
                .collect(Collectors.toList());
        return projectBaseImfList;
    }

    @Override
    public User getOwner(Long projectId) {
        QueryWrapper<UserProject> wrapper = new QueryWrapper<>();
        wrapper.eq("project_id", projectId);
        wrapper.eq("relation", StatusEnum.CREATE_PROJECT);
        return userMapper.selectById(user2ProjectMapper.selectOne(wrapper).getUserId());
    }

    @Override
    public boolean applyJoin(Long projectId, Long userID, String content) {
        Long ownerId = getOwner(projectId).getId();
        UserConfig userConfig = userConfigService.getById(ownerId);
        if (!Objects.isNull(userConfig)) {
            if (StatusEnum.AUTO_REPLY_MSG_EFFECTIVE.equals(userConfig.getEffective())) {
                if (StatusEnum.AUTO_REFUSE_MSG.equals(userConfig.getMessageAuto())) {
                    return applyJoinProjectMapper.insert(
                            new ApplyJoinProject(projectId, userID, StatusEnum.REFUSE, content, StatusEnum.UNCHECKED,
                                    LocalDateTime.now())) == 1;
                } else {
                    return applyJoinProjectMapper.insert(
                            new ApplyJoinProject(projectId, userID, StatusEnum.APPROVE, content, StatusEnum.UNCHECKED,
                                    LocalDateTime.now())) == 1 && user2ProjectMapper.insert(new UserProject(userID, projectId,
                            StatusEnum.JOIN_PROJECT)) == 1;
                }
            }
        }
        return applyJoinProjectMapper.insert(
                new ApplyJoinProject(projectId, userID, StatusEnum.WAIT, content, StatusEnum.UNCHECKED,
                        LocalDateTime.now())) == 1;
    }

    @Override
    public int checkReceiveApplyNum(long userId) {
        return getUncheckedReceiveApplyNum(userId).size();
    }

    @Override
    public int checkApplyNum(long userId) {
        return getUncheckedApplyNum(userId).size();
    }


    @Override
    public Map<String, List<Message>> checkAllApply(long userId) {
        HashMap<String, List<Message>> map = new HashMap<>();
        List<ApplyJoinProject> receiveList = getReceiveApply(userId);
        List<ApplyJoinProject> applyList = getApply(userId);
        List<Message> receiveMessages = new ArrayList<>();
        List<Message> applyMessages = new ArrayList<>();
        for (ApplyJoinProject receiveApply : receiveList) {
            ProjectBaseImf projectBaseImf = projectBaseMapper.selectById(receiveApply.getProjectId());
            Message receiveMessage = new Message(userMapper.selectById(receiveApply.getUserId()).getUsername(),
                    projectBaseImf.getProjectName(),
                    projectBaseImf.getDescription(),
                    receiveApply);
            receiveMessages.add(receiveMessage);
        }
        for (ApplyJoinProject apply : applyList) {
            Long projectId = apply.getProjectId();
            ProjectBaseImf projectBaseImf = projectBaseMapper.selectById(projectId);
            Message applyMessage = new Message(projectBaseService.getOwner(projectId).getUsername(),
                    projectBaseImf.getProjectName(),
                    projectBaseImf.getDescription(),
                    apply);
            applyMessages.add(applyMessage);
        }
        map.put("receive", receiveMessages);
        map.put("apply", applyMessages);
        return map;
    }

    @Override
    public List<Integer> getLikeList(long userId, List<ProjectBaseImf> projectBaseImfList) {
        QueryWrapper<UserLikeProject> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Long> collect = userLikeProjectMapper.selectList(wrapper).stream().map(index -> index.getProjectId()).collect(Collectors.toList());
        List<Integer> res = new ArrayList<>(projectBaseImfList.size());
        for (ProjectBaseImf baseImf : projectBaseImfList) {
            if (collect.contains(baseImf.getProjectId())) {
                res.add(StatusEnum.LIKED);
            } else {
                res.add(StatusEnum.UNLIKED);
            }
        }
        return res;
    }

    @Override
    public boolean changeLikedStatus(long userId, long projectId, Integer newLikedStatus) {
        if (StatusEnum.LIKED.equals(newLikedStatus)) {
            return userLikeProjectMapper.insert(new UserLikeProject(userId, projectId)) == 1;
        }
        QueryWrapper<UserLikeProject> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("project_id", projectId);
        return userLikeProjectMapper.delete(wrapper) == 1;
    }

    @Override
    public List<ProjectBaseImf> getLikeProject(long userId) {
        QueryWrapper<UserLikeProject> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        List<Long> projectIds = userLikeProjectMapper.selectList(wrapper)
                .stream().map(index -> index.getProjectId()).collect(Collectors.toList());
        return getProjects(projectIds);
    }

    @Override
    public boolean setReply(ApplyJoinProject applyJoinProject) {
        UpdateWrapper<ApplyJoinProject> wrapper = new UpdateWrapper<>();
        long projectId = applyJoinProject.getProjectId();
        long userId = applyJoinProject.getUserId();
        wrapper.eq("project_id", projectId);
        wrapper.eq("user_id", userId);
        wrapper.eq("apply_time", applyJoinProject.getApplyTime());
        boolean res = true;
        if (StatusEnum.APPROVE.equals(applyJoinProject.getStatus())) {
            res = user2ProjectMapper.insert(new UserProject(userId, applyJoinProject.getProjectId(), StatusEnum.JOIN_PROJECT)) == 1;
        }
        return res && applyJoinProjectMapper.update(applyJoinProject, wrapper) == 1;
    }

    private List<ApplyJoinProject> getUncheckedReceiveApplyNum(long userId) {
        List<Long> selfProjectID = getSelfProjectID(userId);
        List<ApplyJoinProject> applyJoinProjects = new ArrayList<>();
        if(!Objects.isNull(selfProjectID) && !selfProjectID.isEmpty()) {
            QueryWrapper<ApplyJoinProject> wrapper = new QueryWrapper<>();
            wrapper.eq("checked", StatusEnum.UNCHECKED);
            wrapper.in("project_id", selfProjectID);
            applyJoinProjects = applyJoinProjectMapper.selectList(wrapper);
        }
        return applyJoinProjects;
    }

    private List<ApplyJoinProject> getUncheckedApplyNum(long userId) {
        QueryWrapper<ApplyJoinProject> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("checked", StatusEnum.UNCHECKED);
        return applyJoinProjectMapper.selectList(wrapper);
    }

    private List<ApplyJoinProject> getReceiveApply(long userId) {
        List<Long> selfProjectID = getSelfProjectID(userId);
        List<ApplyJoinProject> res = new ArrayList<>();
        if(!Objects.isNull(selfProjectID)&& !selfProjectID.isEmpty()){
            QueryWrapper<ApplyJoinProject> wrapper = new QueryWrapper<>();
            wrapper.in("project_id", selfProjectID);
            res = applyJoinProjectMapper.selectList(wrapper);
            checkApply(selfProjectID);
        }
        return res;
    }

    private List<ApplyJoinProject> getApply(long userId) {
        QueryWrapper<ApplyJoinProject> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return applyJoinProjectMapper.selectList(wrapper);
    }

    private void checkApply(List<Long> selfProjectID) {
        UpdateWrapper<ApplyJoinProject> wrapper = new UpdateWrapper<>();
        wrapper.in("project_id", selfProjectID);
        wrapper.set("checked", 1);
        applyJoinProjectMapper.update(null, wrapper);
    }


}
