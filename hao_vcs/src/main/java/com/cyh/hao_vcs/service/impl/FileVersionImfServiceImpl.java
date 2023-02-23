package com.cyh.hao_vcs.service.impl;

import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.mapper.ProjectBaseMapper;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.service.FileVersionImfService;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class FileVersionImfServiceImpl implements FileVersionImfService {

    @Autowired
    ProjectBaseMapper projectBaseMapper;

    @Autowired
    FileBaseImfService fileBaseImfService;

    @Autowired
    ProjectBaseService projectBaseService;



    @Override
    public String updateText(Long projectId, String morePath, String content, Integer updateKind) {
        String path = projectBaseService.getProjectPath(projectId);
        fileBaseImfService.updateFileLatestVersion(path + morePath, updateKind);
        String fileId = fileBaseImfService.getFileIdWithVersion(path + morePath);
        return FileUtil.saveTextAsHtml(morePath.substring(morePath.lastIndexOf("\\")+1),
                fileId.substring(0,fileId.lastIndexOf(".")),content);

    }
}