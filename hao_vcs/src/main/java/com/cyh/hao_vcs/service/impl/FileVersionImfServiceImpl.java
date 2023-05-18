package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.config.VersionConfig;
import com.cyh.hao_vcs.entity.FileBaseImf;
import com.cyh.hao_vcs.entity.FileVersionImf;
import com.cyh.hao_vcs.entity.ProjectConfig;
import com.cyh.hao_vcs.mapper.FileBaseImfMapper;
import com.cyh.hao_vcs.mapper.FileVersionImfMapper;
import com.cyh.hao_vcs.mapper.ProjectBaseMapper;
import com.cyh.hao_vcs.service.*;
import com.cyh.hao_vcs.utils.*;
import com.qiniu.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

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
    FileBaseImfMapper fileBaseImfMapper;

    @Autowired
    ProjectBaseService projectBaseService;

    @Autowired
    UserService userService;

    @Autowired
    ProjectConfigService projectConfigService;

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
    public String updatePic(Long projectId, String morePath, MultipartFile file, String log, Long actorId) {
        String path = projectBaseService.getProjectPath(projectId);
        if (StringUtils.isNullOrEmpty(log)) {
            log = UPDATE_FILE;
        }
        String version = fileBaseImfService.updateFileLatestVersion(path + morePath, StatusEnum.HEAVY_UPDATE);
        String fileId = fileBaseImfService.getFileOriginId(path + morePath);
        fileVersionImfMapper.insert(new FileVersionImf(fileId, version, LocalDateTime.now(),
                userService.getById(actorId).getUsername(), log));
        if(!StringUtils.isNullOrEmpty(fileId)){
            String suffix = '.' + FileUtil.getSuffix(morePath);
            ProjectConfig projectConfig = projectConfigService.getById(projectId);
            if(!Objects.isNull(projectConfig) && StatusEnum.AUTO_DELETE_CACHE_ON.equals(projectConfig.getAutoDeleteCache())){
                try {
                    String preVersion = VersionUtil.getPreviousVersionWithHeavyUpdate(version);
                    if(!INIT_VERSION.equals(preVersion)){
                        Files.delete(new File(path+ File.separator+fileId+ VersionUtil.LOGO
                                + preVersion + suffix).toPath() );
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            String resPath = path+ File.separator+fileId+ VersionUtil.LOGO + version;
            FileUtil.updatePicAndChangeVersion(resPath, suffix,file);
            return resPath + morePath.substring(morePath.lastIndexOf("."));
        }
        return null;
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
        //更新数据库中当前版本
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
    public String addWatermark(String waterMark, String path, Integer nowVersion) {
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

    @Override
    public R updateAudioByDel(String path, Integer nowVersion, double startTime, double endTime) {
        String inputPath = prefix + path;
        String outputPath = getOutputPath(inputPath, nowVersion);
        try {
            return R.success(AudioProcessor.audioClipByDel(inputPath, outputPath, startTime, endTime),
                    outputPath.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.error("音频裁剪失败");
    }

    @Override
    public R updateAudioBySave(String path, Integer nowVersion, double startTime, double endTime) {
        String inputPath = prefix + path;
        String outputPath = getOutputPath(inputPath, nowVersion);
        try {
            return R.success(AudioProcessor.audioClip(inputPath, outputPath, startTime, endTime),
                    outputPath.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.error("音频裁剪失败");
    }

    @Override
    public R changeAudioSpeed(String path, Integer nowVersion, double times) {
        String inputPath = prefix + path;
        String outputPath = getOutputPath(inputPath, nowVersion);
        try {
            return R.success(AudioProcessor.audioChangeSpeed(inputPath, outputPath, times),
                    outputPath.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.error("变速异常");
    }

    @Override
    public R changeAudioTone(String path, Integer nowVersion, double times) {
        String inputPath = prefix + path;
        String outputPath = getOutputPath(inputPath, nowVersion);
        try {
            AudioProcessor.audioChangeTone(inputPath, outputPath, times);
            return R.success(outputPath.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return R.error("变调异常");
    }

    @Override
    public R updateVideo(Long actorId, String path, int version, List<String> log) {
        // 拼接文件夹前缀 D:\ADeskTop\project\bigWork\repository\3- 音视频测试\temp\2-441a614b-f0f4-4f6d-a1e1-2645e414a4e2-v1.0.0.mp4
        String tempPath = path.replace(FileConfig.RELATIVE_PROJECT_PATH, FileConfig.PROJECT_PATH);
        //temp文件名， 2-441a614b-f0f4-4f6d-a1e1-2645e414a4e2-v1.0.0.mp4
        String fileName = tempPath.substring(tempPath.lastIndexOf("\\")+1);
        //去除前缀 441a614b-f0f4-4f6d-a1e1-2645e414a4e2-v1.0.0.mp4
        fileName = fileName.substring(fileName.indexOf("-") + 1);
        path = tempPath.substring(0,tempPath.lastIndexOf("\\"));
        //D:\ADeskTop\project\bigWork\repository\3- 音视频测试\
        path = path.substring(0,path.lastIndexOf("\\") + 1);
        String fileId = fileName.substring(0, fileName.lastIndexOf("-"));
        //2.0.0
        String newVersion = fileBaseImfService.updateFileLatestVersion(
                path + fileBaseImfMapper.selectById(fileId).getFileName(), StatusEnum.HEAVY_UPDATE);
        String newFileName = fileId + "-v" + newVersion + "." + FileUtil.getSuffix(fileName);
        String newFilePath = path + newFileName;
        new File(tempPath).renameTo(new File(newFilePath));
        try (Stream<Path> walk = Files.walk(Paths.get(tempPath.substring(0, tempPath.lastIndexOf("\\"))))) {
            walk.sorted(Comparator.reverseOrder())
                    .forEach(pathIn -> {
                        try {
                            Files.delete(pathIn);
                        } catch (IOException e) {
                            System.err.printf("无法删除的路径 %s%n%s", pathIn, e);
                        }
                    });
        }catch (Exception e){
            System.out.println(e.toString());
            return  R.error("文件保存异常");
        }
        fileVersionImfMapper.insert(new FileVersionImf(fileName.substring(0, fileName.lastIndexOf("-")), newVersion,
                LocalDateTime.now(), userService.getById(actorId).getUsername(), log.toString()));
        return R.success("保存成功");
    }


    private FileVersionImf getFileVersionImf(Long projectId, String morePath, String version) {
        String fileId = fileBaseImfService.getFileOriginId(projectBaseService.getProjectPath(projectId) + morePath);
        QueryWrapper<FileVersionImf> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", fileId);
        queryWrapper.eq("version", version);
        return fileVersionImfMapper.selectOne(queryWrapper);
    }
}