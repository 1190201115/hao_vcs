package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyh.hao_vcs.entity.ProjectConfig;
import com.cyh.hao_vcs.entity.UserDetail;
import com.cyh.hao_vcs.mapper.ProjectConfigMapper;
import com.cyh.hao_vcs.mapper.UserDetailMapper;
import com.cyh.hao_vcs.service.ProjectConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectConfigServiceImpl extends ServiceImpl<ProjectConfigMapper, ProjectConfig> implements ProjectConfigService {

    @Autowired
    ProjectConfigMapper projectConfigMapper;


}