package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.config.VersionConfig;
import com.cyh.hao_vcs.entity.FileBaseImf;
import com.cyh.hao_vcs.entity.FileVersionImf;
import com.cyh.hao_vcs.mapper.FileVersionImfMapper;
import com.cyh.hao_vcs.mapper.ProjectBaseMapper;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.service.FileVersionImfService;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.service.UserService;
import com.cyh.hao_vcs.utils.Converter;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.VersionUtil;
import com.cyh.hao_vcs.utils.VideoProcessor;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.cyh.hao_vcs.common.StatusEnum.DELETE_SAFE;
import static com.cyh.hao_vcs.common.StatusEnum.INIT_VERSION;
import static com.cyh.hao_vcs.config.VersionConfig.*;
import static com.cyh.hao_vcs.utils.FileUtil.getDiffFileName;

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

    private static String prefix = "D:\\ADeskTop\\project\\bigWork";

    @Override
    public String updateText(Long projectId, String morePath, String content, Integer updateKind, String log, Long actorId) {
        String path = projectBaseService.getProjectPath(projectId);
        if (StringUtils.isNullOrEmpty(log)) {
            log = UPDATE_FILE;
        }
        String version = fileBaseImfService.updateFileLatestVersion(path + morePath, updateKind);
        String fileId = fileBaseImfService.getFileOriginId(path + morePath);
        fileVersionImfMapper.insert(new FileVersionImf(fileId, version, LocalDateTime.now(),
                userService.getById(actorId).getUsername(), log));
        return FileUtil.saveTextAsHtml(morePath.substring(morePath.lastIndexOf("\\") + 1),
                fileId + VersionUtil.LOGO + version, content);

    }

    @Override
    public void updatePic(Long projectId, String morePath, MultipartFile file, String log, Long actorId) {
        String path = projectBaseService.getProjectPath(projectId);
        if (StringUtils.isNullOrEmpty(log)) {
            log = UPDATE_FILE;
        }
        String version = fileBaseImfService.updateFileLatestVersion(path + morePath, StatusEnum.LIGHT_UPDATE);
        String fileId = fileBaseImfService.getFileOriginId(path + morePath);
        fileVersionImfMapper.insert(new FileVersionImf(fileId, version, LocalDateTime.now(),
                userService.getById(actorId).getUsername(), log));
        if(!StringUtils.isNullOrEmpty(fileId)){
            FileUtil.updatePicAndChangeVersion(path+ File.separator+fileId+ VersionUtil.LOGO + version, morePath.substring(morePath.lastIndexOf(".")),file);
        }
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
        if (Objects.isNull(fileVersionImf)) return false;
        fileVersionImf.setSaveTime(LocalDateTime.now());
        fileVersionImf.setLatestAction(CHECK_FILE);
        fileVersionImf.setLatestActor(userService.getById(actorId).getUsername());
        String fileId = fileBaseImfService.getFileOriginId(projectBaseService.getProjectPath(projectId) + morePath);
        QueryWrapper<FileVersionImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        queryWrapper.eq("version", newVersion);
        return fileVersionImfMapper.update(fileVersionImf, queryWrapper) == 1 && checkCurrentVersion;
    }

    @Override
    public R compareText(Long projectId, String morePath, String version) {
        String filePath = projectBaseService.getProjectPath(projectId) + morePath;
        FileBaseImf fileBaseImf = fileBaseImfService.getFileByFilePath(filePath);
        String fileId = fileBaseImf.getFileId();
        String fileName = fileBaseImf.getFileName();
        fileName = fileName.substring(0, fileName.lastIndexOf("."));
        String currentVersion = fileBaseImf.getCurrentVersion();
        String htmlSuffix = ".html";
        String fileSuffix = FileUtil.getHtmlStorePath(morePath.substring(morePath.lastIndexOf(".") + 1));
        if (Objects.isNull(fileSuffix)) {
            return R.error("文件类型错误");
        }
        String pathPrefix = fileSuffix + fileId + VersionUtil.LOGO;
        String diffFilePath = Converter.showHtmlDiff(pathPrefix + version + htmlSuffix, pathPrefix + currentVersion + htmlSuffix,
                getDiffFileName(fileName, version, currentVersion));
        if (StringUtils.isNullOrEmpty(diffFilePath)) {
            return R.error("差异分析失败");
        }
        return R.success(diffFilePath, "对比成功");
    }

    private String getOutputPath(String path, Integer nowVersion){
        String fileName = path.substring(path.lastIndexOf("\\") + 1);
        String outputPath = path.substring(0, path.lastIndexOf("\\"));
        if(nowVersion  != 1){
            outputPath = outputPath + File.separator;
            outputPath = outputPath.replaceAll("/", "\\\\");
            fileName = fileName.substring(fileName.indexOf("-")+1);
        }else{
            outputPath = outputPath + FileConfig.TEMP_REPO;
            FileUtil.createDir(outputPath);
        }
        return outputPath + nowVersion + "-" + fileName;
    }

    @Override
    public String updateVideo(String waterMark, String path, Integer nowVersion) {
        String inputPath = prefix + path;
        String outputPath = getOutputPath(inputPath, nowVersion);
        try {
            VideoProcessor.videoAddText(inputPath,outputPath,waterMark);
            return outputPath.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public R updateVideoByDel(String path, Integer nowVersion, double startTime, double endTime) {
        String inputPath = prefix + path;
        String outputPath = getOutputPath(inputPath, nowVersion);
        try {
            return R.success(VideoProcessor.videoClipByDel(inputPath, outputPath, startTime, endTime),
                    outputPath.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.error("视频裁剪失败");
    }

    @Override
    public R updateVideoBySave(String path, Integer nowVersion, double startTime, double endTime) {
        String inputPath = prefix + path;
        String outputPath = getOutputPath(inputPath, nowVersion);
        try {
            return R.success(VideoProcessor.videoClip(inputPath, outputPath, startTime, endTime),
                    outputPath.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.error("视频裁剪失败");
    }

    private FileVersionImf getFileVersionImf(Long projectId, String morePath, String version) {
        String fileId = fileBaseImfService.getFileOriginId(projectBaseService.getProjectPath(projectId) + morePath);
        QueryWrapper<FileVersionImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        queryWrapper.eq("version", version);
        return fileVersionImfMapper.selectOne(queryWrapper);
    }
}