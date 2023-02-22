package com.cyh.hao_vcs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class FileBaseImf {
private String fileId;
private String path;
private String fileName;
private String latestVersion;
private Integer deleteSafe;
}