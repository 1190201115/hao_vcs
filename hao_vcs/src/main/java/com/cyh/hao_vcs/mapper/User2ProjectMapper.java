package com.cyh.hao_vcs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cyh.hao_vcs.entity.ProjectBaseImf;
import com.cyh.hao_vcs.entity.UserProject;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface User2ProjectMapper extends BaseMapper<UserProject> {
}
