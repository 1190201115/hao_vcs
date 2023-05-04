package com.cyh.hao_vcs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ApplyJoinProject {
    private Long projectId;
    private Long userId;
    private Integer status;
    private String content;
    private Integer checked;
    private LocalDateTime applyTime;
}