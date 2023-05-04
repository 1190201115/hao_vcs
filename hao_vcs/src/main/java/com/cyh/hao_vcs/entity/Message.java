package com.cyh.hao_vcs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Message {
    private String applyName;
    private String projectName;
    private String description;
    private ApplyJoinProject apply;

    public Message(){

    }
}
