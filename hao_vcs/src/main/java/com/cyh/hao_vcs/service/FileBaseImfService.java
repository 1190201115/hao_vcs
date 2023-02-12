package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.entity.FileBaseImf;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileBaseImfService {

    boolean insertFileIntoProject(Long projectId, String projectName, MultipartFile file);

    List<String> getFileRealName(List<String> fileNameList);


}