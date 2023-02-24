package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.entity.FileVersionImf;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface FileVersionImfService {
    String updateText(Long projectId, String morePath, String content, Integer updateKind, Long actorId);

    List<FileVersionImf> getVersionList(Long projectId, String morePath);
}