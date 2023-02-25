package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class FileVersionImf {
    @TableId(value = "file_id")
    private String fileId;
    private String version;
    private LocalDateTime saveTime;
    private String latestActor;
    private String latestAction;
}