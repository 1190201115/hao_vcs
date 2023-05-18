package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.FileVersionImf;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileVersionImfService {
    String updateText(Long projectId, String morePath, String content, Integer updateKind, String log, Long actorId);

    String updatePic(Long projectId, String morePath, MultipartFile file, String log, Long actorId);

    List<FileVersionImf> getVersionList(Long projectId, String morePath);

    boolean checkFileVersion(Long projectId, String morePath, String newVersion, Long userId);

    R compareText(Long projectId, String morePath, String version);

    String addWatermark(String waterMark, String path, Integer nowVersion);

    R updateVideoByDel(String path, Integer nowVersion, double startTime, double endTime);

    R updateVideoBySave(String path, Integer nowVersion, double startTime, double endTime);

    R updateAudioByDel(String path, Integer nowVersion, double startTime, double endTime);

    R updateAudioBySave(String path, Integer nowVersion, double startTime, double endTime);

    R changeAudioSpeed(String path, Integer nowVersion, double times);

    R changeAudioTone(String path, Integer nowVersion, double times);

    R updateVideo(Long userId, String path, int version, List<String> log);

}