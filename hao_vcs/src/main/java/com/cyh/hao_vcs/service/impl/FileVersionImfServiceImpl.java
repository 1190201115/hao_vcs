package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.FileBaseImf;
import com.cyh.hao_vcs.entity.FileVersionImf;
import com.cyh.hao_vcs.mapper.FileVersionImfMapper;
import com.cyh.hao_vcs.mapper.ProjectBaseMapper;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.service.FileVersionImfService;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.service.UserService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.VersionUtil;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.sql.Wrapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cyh.hao_vcs.config.VersionConfig.CHECK_FILE;
import static com.cyh.hao_vcs.config.VersionConfig.UPDATE_FILE;

@Service
public class FileVersionImfServiceImpl implements FileVersionImfService {

    @Autowired
    ProjectBaseMapper projectBaseMapper;

    @Autowired
    FileVersionImfMapper fileVersionImfMapper;

    @Autowired
    FileBaseImfService fileBaseImfService;

    @Autowired
    ProjectBaseService projectBaseService;

    @Autowired
    UserService userService;



    @Override
    public String updateText(Long projectId, String morePath, String content, Integer updateKind, Long actorId) {
        String path = projectBaseService.getProjectPath(projectId);
        String version = fileBaseImfService.updateFileLatestVersion(path + morePath, updateKind);
        String fileId = fileBaseImfService.getFileOriginId(path + morePath);
        fileVersionImfMapper.insert(new FileVersionImf(fileId,version, LocalDateTime.now(),
                userService.getById(actorId).getUsername(),UPDATE_FILE));
        return FileUtil.saveTextAsHtml(morePath.substring(morePath.lastIndexOf("\\")+1),
                fileId+ VersionUtil.LOGO+version,content);

    }

    @Override
    public List<FileVersionImf> getVersionList(Long projectId, String morePath) {
        String fileId = fileBaseImfService.getFileOriginId(projectBaseService.getProjectPath(projectId) + morePath);
        QueryWrapper<FileVersionImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        return fileVersionImfMapper.selectList(queryWrapper);
    }

    @Override
    public boolean checkFileVersion(Long projectId, String morePath, String newVersion, Long actorId) {
        boolean checkCurrentVersion = fileBaseImfService.checkCurrentVersion
                (projectBaseService.getProjectPath(projectId) + morePath, newVersion);
        FileVersionImf fileVersionImf = getFileVersionImf(projectId, morePath, newVersion);
        if(Objects.isNull( fileVersionImf )) return false;
        fileVersionImf.setSaveTime(LocalDateTime.now());
        fileVersionImf.setLatestAction(CHECK_FILE);
        fileVersionImf.setLatestActor(userService.getById(actorId).getUsername());
        String fileId = fileBaseImfService.getFileOriginId(projectBaseService.getProjectPath(projectId) + morePath);
        QueryWrapper<FileVersionImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        queryWrapper.eq("version", newVersion);
        return fileVersionImfMapper.update(fileVersionImf,queryWrapper) == 1 && checkCurrentVersion;
    }

    private FileVersionImf getFileVersionImf(Long projectId, String morePath, String version){
        String fileId = fileBaseImfService.getFileOriginId(projectBaseService.getProjectPath(projectId) + morePath);
        QueryWrapper<FileVersionImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        queryWrapper.eq("version", version);
        return fileVersionImfMapper.selectOne(queryWrapper);
    }
}