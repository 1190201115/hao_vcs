package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.FileVersionImf;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileVersionImfService {
    String updateText(Long projectId, String morePath, String content, Integer updateKind, String log, Long actorId);

    void updatePic(Long projectId, String morePath, MultipartFile file, String log, Long actorId);

    List<FileVersionImf> getVersionList(Long projectId, String morePath);

    boolean checkFileVersion(Long projectId, String morePath, String newVersion, Long userId);

    R compareText(Long projectId, String morePath, String version);
}