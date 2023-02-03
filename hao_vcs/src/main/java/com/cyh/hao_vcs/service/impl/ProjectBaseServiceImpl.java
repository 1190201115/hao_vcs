package com.cyh.hao_vcs.service.impl;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.ProjectBaseImf;
import com.cyh.hao_vcs.mapper.ProjectBaseMapper;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProjectBaseServiceImpl implements ProjectBaseService {

    @Autowired
    ProjectBaseMapper projectBaseMapper;

    @Override
    public R insertBaseProject(ProjectBaseImf baseImf) {
        if(StringUtils.isNullOrEmpty( baseImf.getProjectName() ))
        {
            return R.error("工程名称不能为空");
        }
        projectBaseMapper.insert(baseImf);
        return R.success("创建成功");
    }
}
