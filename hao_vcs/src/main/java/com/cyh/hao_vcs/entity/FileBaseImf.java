package com.cyh.hao_vcs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
@Data
@AllArgsConstructor
public class FileBaseImf {
private String fileId;
private String fileName;
private Integer latestVersion;
private Integer deleteSafe;
}