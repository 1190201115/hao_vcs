package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.ProjectBaseImf;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.entity.UserProject;
import com.cyh.hao_vcs.mapper.ProjectBaseMapper;
import com.cyh.hao_vcs.mapper.User2ProjectMapper;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProjectBaseServiceImpl implements ProjectBaseService {

    @Autowired
    ProjectBaseMapper projectBaseMapper;

    @Autowired
    User2ProjectMapper user2ProjectMapper;



    @Override
    public R insertBaseProject(ProjectBaseImf baseImf, Long userID, int status) {
        if(StringUtils.isNullOrEmpty(baseImf.getProjectName()))
        {
            return R.error("工程名称不能为空");
        }
        if(Objects.isNull(userID)){
            return R.error("用户状态异常！");
        }
        projectBaseMapper.insert(baseImf);
        user2ProjectMapper.insert(new UserProject(userID, baseImf.getProjectId(), status));
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
        queryWrapper.eq("user_id", userID).eq("relation", 1);
        return collectList(queryWrapper);
    }

    @Override
    public List<Long> getJoinProjectID(Long userID) {
        QueryWrapper<UserProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userID).eq("relation", 0);
        return collectList(queryWrapper);
    }

    @Override
    public List<ProjectBaseImf> getProjects(List<Long> idList) {
        return projectBaseMapper.selectBatchIds(idList);
    }


    private List<Long> collectList(QueryWrapper<UserProject> queryWrapper){
        Object ob = user2ProjectMapper.selectList(queryWrapper);
        List<Long> idList = user2ProjectMapper.selectList(queryWrapper)
                .stream()
                .map(userProject -> userProject.getProjectId())
                .collect(Collectors.toList());
        return idList;
    }
}
