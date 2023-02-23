package com.cyh.hao_vcs.service;

import org.springframework.web.bind.annotation.RequestParam;

public interface FileVersionImfService {
    String updateText(Long projectId, String morePath, String content, Integer updateKind);
}