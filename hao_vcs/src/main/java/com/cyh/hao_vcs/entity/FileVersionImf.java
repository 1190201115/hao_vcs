package com.cyh.hao_vcs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileVersionImf {
    private String fileId;
    private String version;
    private LocalDateTime saveTime;
    private String latestActor;
    private String latestAction;
}