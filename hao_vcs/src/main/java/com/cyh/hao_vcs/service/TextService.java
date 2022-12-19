package com.cyh.hao_vcs.service;

import com.cyh.hao_vcs.common.R;
import org.springframework.web.multipart.MultipartFile;

public interface TextService {
    String getDoc(String fileName);
    String getDocx(String fileName);
    String getTxt(String fileName);
    boolean saveDoc(String fileName, String content);
}
