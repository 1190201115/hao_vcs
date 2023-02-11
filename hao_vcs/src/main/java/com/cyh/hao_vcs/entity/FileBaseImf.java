package com.cyh.hao_vcs.entity;

import lombok.Data;
@Data
public class FileBaseImf {
private Long fileId;
private String fileName;
private Integer latestVersion;
private Integer deleteSafe;
}