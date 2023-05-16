package com.cyh.hao_vcs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserLikeProject {
    private Long userId;
    private Long projectId;
}