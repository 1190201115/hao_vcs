package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProjectConfig {

    @TableId(value = "project_id")
    private Long projectId;
    private Integer autoDeleteCache;

    ProjectConfig(){

    }
}