package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ProjectChangeBaseImf {

    @TableId(value = "project_id")
    private Long projectId;

    private LocalDateTime latestUpdateTime;

    private String latestAction;

    private String latestActor;
}
