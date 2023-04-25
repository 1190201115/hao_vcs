package com.cyh.hao_vcs.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AudioImf {
    private String path;
    private Double time;
    private Integer version;
    private List<String> logList;
}
