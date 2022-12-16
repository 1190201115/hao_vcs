package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.common.R;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    R saveImage(MultipartFile file);

    boolean renameFile(String oldName, String newName);

    boolean deleteFile(String fileName);
}
