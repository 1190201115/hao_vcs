package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.entity.FileBaseImf;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileBaseImfService {

    boolean insertFileIntoProject(Long projectId, String projectName, MultipartFile file, String path, Long userId);

    List<String> getFileRealName(List<String> fileNameList);

    String getFileOriginId(String filePath);

    String getFileIdWithCurrentVersion(String filePath);

    String getFileIdWithLatestVersion(String filePath);

    String getFileLatestVersion(String filePath);

    String updateFileLatestVersion(String filePath, Integer updateKind);

    boolean checkCurrentVersion(String filePath, String newVersion);

    FileBaseImf getFileByFilePath(String filePath);
}