package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.entity.ProjectChangeBaseImf;
import com.cyh.hao_vcs.entity.UserProject;
import com.cyh.hao_vcs.mapper.ProjectBaseMapper;
import com.cyh.hao_vcs.mapper.ProjectChangeBaseImfMapper;
import com.cyh.hao_vcs.mapper.User2ProjectMapper;
import com.cyh.hao_vcs.mapper.UserMapper;
import com.cyh.hao_vcs.service.ProjectChangeBaseImfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@Service
public class ProjectChangeBaseImfServiceImpl implements ProjectChangeBaseImfService {

    @Autowired
    ProjectChangeBaseImfMapper projectChangeBaseImfMapper;

    @Autowired
    User2ProjectMapper user2ProjectMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    ProjectBaseMapper projectBaseMapper;

    @Override
    public boolean insertProjectChangeBaseImf(Long userID, Long projectID, LocalDateTime latestUpdateTime, String latestAction) {
        String userName = userMapper.selectById(userID).getUsername();
        return projectChangeBaseImfMapper.insert(new ProjectChangeBaseImf(projectID, latestUpdateTime, latestAction, userName)) == 1;
    }

    @Override
    public ProjectChangeBaseImf getProjectChangeBaseImfByID(Long projectId) {
        System.out.println(projectId);
        return projectChangeBaseImfMapper.selectById(projectId);
    }

    @Override
    public boolean deleteProjectByID(Long projectID) {
        return projectChangeBaseImfMapper.deleteById(projectID) == 1 &&
                user2ProjectMapper.deleteById(projectID) == 1 &&
                projectBaseMapper.deleteById(projectID) == 1;
    }

    @Override
    public boolean checkAuthority(Long userID, Long projectID) {
        QueryWrapper<UserProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userID);
        queryWrapper.eq("project_id", projectID);
        UserProject userProject = user2ProjectMapper.selectOne(queryWrapper);
        if (!Objects.isNull(userProject)) {
            int relation = userProject.getRelation();
            if (StatusEnum.CREATE_PROJECT.equals(relation)){
                return  true;
            }
        }
        return false;
    }
}
