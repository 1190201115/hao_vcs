package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.ApplyJoinProject;
import com.cyh.hao_vcs.entity.ProjectBaseImf;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.entity.UserProject;
import com.cyh.hao_vcs.mapper.ApplyJoinProjectMapper;
import com.cyh.hao_vcs.mapper.ProjectBaseMapper;
import com.cyh.hao_vcs.mapper.User2ProjectMapper;
import com.cyh.hao_vcs.mapper.UserMapper;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.ProjectUtil;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
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
        if(!StringUtils.isNullOrEmpty(projectName)){
            return FileConfig.PROJECT_PATH + ProjectUtil.getProjectName(projectName,projectId);
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
        return userMapper.selectById(user2ProjectMapper.selectById(projectId).getUserId());
    }

    @Override
    public boolean applyJoin(Long projectId, Long userID, String content) {
        return applyJoinProjectMapper.insert(new ApplyJoinProject(projectId, userID, StatusEnum.WAIT, content)) == 1;
    }


}
