package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.entity.FileBaseImf;
import com.cyh.hao_vcs.entity.FileVersionImf;
import com.cyh.hao_vcs.mapper.FileBaseImfMapper;
import com.cyh.hao_vcs.mapper.FileVersionImfMapper;
import com.cyh.hao_vcs.mapper.UserMapper;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.VersionUtil;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.cyh.hao_vcs.common.StatusEnum.*;
import static com.cyh.hao_vcs.config.VersionConfig.CREATE_FILE;
import static com.cyh.hao_vcs.utils.FileUtil.*;

@Service
public class FileBaseImfServiceImpl implements FileBaseImfService {

    @Autowired
    FileBaseImfMapper fileBaseImfMapper;

    @Autowired
    UserMapper userMapper;

    @Autowired
    FileVersionImfMapper fileVersionImfMapper;


    /**
     *
     * @param projectId
     * @param projectName
     * @param file
     * @param path 带/
     * @return
     */
    @Override
    public boolean insertFileIntoProject(Long projectId, String projectName, MultipartFile file, String path, Long userId) {
        String projectPath = FileUtil.getProjectPath(projectId, projectName);
        if(!StringUtils.isNullOrEmpty(path)){
            projectPath += path;
        }
        String fileId = FileUtil.saveFile(projectPath,INIT_VERSION,file);
        if(!StringUtils.isNullOrEmpty(fileId)){
            FileBaseImf fileBaseImf = new FileBaseImf(fileId, projectPath,file.getOriginalFilename(), INIT_VERSION, DELETE_SAFE);
            FileVersionImf fileVersionImf = new FileVersionImf(fileId, INIT_VERSION, LocalDateTime.now(),
                    userMapper.selectById(userId).getUsername(),CREATE_FILE);
            return fileBaseImfMapper.insert(fileBaseImf) == 1 && fileVersionImfMapper.insert(fileVersionImf) == 1;
        }
        return false;
    }

    @Override
    public List<String> getFileRealName(List<String> fileNameList) {
        QueryWrapper<FileBaseImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("file_id", removeSuffixContainVersion(fileNameList));
        return fileBaseImfMapper.selectList(queryWrapper).stream().map(
                fileBaseImf -> fileBaseImf.getFileName()
        ).collect(Collectors.toList());
    }

    @Override
    public String getFileOriginId(String filePath) {
        QueryWrapper<FileBaseImf> queryWrapper = new QueryWrapper<>();
        String fileName = filePath.substring(filePath.lastIndexOf('\\')+1);
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("path", filePath.substring(0,filePath.lastIndexOf('\\')));
        FileBaseImf fileBaseImf = fileBaseImfMapper.selectOne(queryWrapper);
        return fileBaseImf.getFileId();
    }

    @Override
    public String getFileIdWithVersion(String filePath) {
        QueryWrapper<FileBaseImf> queryWrapper = new QueryWrapper<>();
        String fileName = filePath.substring(filePath.lastIndexOf('\\')+1);
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("path", filePath.substring(0,filePath.lastIndexOf('\\')));
        FileBaseImf fileBaseImf = fileBaseImfMapper.selectOne(queryWrapper);
        return fileBaseImf.getFileId()+ VersionUtil.getVersionSuffix(fileBaseImf.getLatestVersion())+fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public String getFileLatestVersion(String filePath) {
        String fileName = filePath.substring(filePath.lastIndexOf('\\')+1);
        QueryWrapper<FileBaseImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("path", filePath.substring(0,filePath.lastIndexOf('\\')));
        return fileBaseImfMapper.selectOne(queryWrapper).getLatestVersion();
    }

    @Override
    public String updateFileLatestVersion(String filePath, Integer updateKind) {
        String fileName = filePath.substring(filePath.lastIndexOf('\\')+1);
        QueryWrapper<FileBaseImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", fileName);
        queryWrapper.eq("path", filePath.substring(0,filePath.lastIndexOf('\\')));
        FileBaseImf fileBaseImf = fileBaseImfMapper.selectOne(queryWrapper);
        String version = VersionUtil.getNextVersion(fileBaseImf.getLatestVersion(),updateKind);
        fileBaseImf.setLatestVersion(version);
        fileBaseImfMapper.updateById(fileBaseImf);
        return version;
    }


}