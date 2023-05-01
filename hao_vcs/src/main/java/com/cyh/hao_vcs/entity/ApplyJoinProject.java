package com.cyh.hao_vcs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApplyJoinProject {
    private Long projectId;
    private Long userId;
    private Integer status;
    private String content;
}