package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileBaseImf {
    @TableId(value = "file_id")
    private String fileId;
    private String path;
    private String fileName;
    private String currentVersion;
    private String latestVersion;
    private Integer deleteSafe;
}