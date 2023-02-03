package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class ProjectBaseImf {

    @TableId(type = IdType.AUTO)
    private Long projectID;

    private String projectName;
    private String description;
    private int privacy;

    public ProjectBaseImf(String projectName, String description, int privacy) {
        this.projectName = projectName;
        this.description = description;
        this.privacy = privacy;
    }
}
