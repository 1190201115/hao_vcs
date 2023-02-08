package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProjectBaseImf {

    @TableId(type = IdType.AUTO)
    private Long projectId;

    private String projectName;
    private String description;
    private int privacy;

    private LocalDateTime createTime;

    //mp在封装对象时会使用到无参构造函数
    public ProjectBaseImf(){

    }

    public ProjectBaseImf(String projectName, String description, int privacy) {
        this.projectName = projectName;
        this.description = description;
        this.privacy = privacy;
    }
}
