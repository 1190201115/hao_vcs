package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.FileBaseImf;
import com.cyh.hao_vcs.mapper.FileBaseImfMapper;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.cyh.hao_vcs.common.StatusEnum.*;
import static com.cyh.hao_vcs.utils.FileUtil.getSuffix;
import static com.cyh.hao_vcs.utils.FileUtil.removeSuffix;

@Service
public class FileBaseImfServiceImpl implements FileBaseImfService {

    @Autowired
    FileBaseImfMapper fileBaseImfMapper;


    @Override
    public boolean insertFileIntoProject(Long projectId, String projectName, MultipartFile file, String path) {
        String projectPath = FileUtil.getProjectPath(projectId, projectName);
        if(!Objects.isNull(path)){
            projectPath += path;
        }
        String fileId = FileUtil.saveFile(projectPath, file);
        if(!StringUtils.isNullOrEmpty(fileId)){
            FileBaseImf fileBaseImf = new FileBaseImf(fileId, file.getOriginalFilename(), INIT_VERSION, DELETE_SAFE);
            return fileBaseImfMapper.insert(fileBaseImf) == 1;
        }
        return false;
    }

    @Override
    public List<String> getFileRealName(List<String> fileNameList) {
        QueryWrapper<FileBaseImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("file_id", removeSuffix(fileNameList));
        return fileBaseImfMapper.selectList(queryWrapper).stream().map(
                fileBaseImf -> fileBaseImf.getFileName()
        ).collect(Collectors.toList());
    }

    @Override
    public String getFileRealName(String fileName) {
        QueryWrapper<FileBaseImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_name", fileName);
        return fileBaseImfMapper.selectOne(queryWrapper).getFileId()+fileName.substring(fileName.lastIndexOf("."));
    }
}