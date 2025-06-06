package com.cyh.hao_vcs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cyh.hao_vcs.common.KeyEnum;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.*;

import com.cyh.hao_vcs.service.*;
import com.cyh.hao_vcs.service.impl.FileBaseImfServiceImpl;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.PicCombiner;
import com.cyh.hao_vcs.utils.VersionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private static String PUBLIC_STATUS = "public";

    @Autowired
    ProjectBaseService projectBaseService;

    @Autowired
    ProjectChangeBaseImfService projectChangeBaseImfService;

    @Autowired
    FileBaseImfService fileBaseImfService;

    @Autowired
    FileService fileService;

    @Autowired
    FileVersionImfService fileVersionImfService;

    private static Integer OWN_PROJECT_STATUS = 1;

    @PostMapping
    public R createProject(@RequestBody Map<String, String> map, HttpSession session) {
        String projectName = map.get("projectName");
        String textarea = map.get("textarea");
        int radio = PUBLIC_STATUS.equals(map.get("radio")) ? 1 : 0;
        ProjectBaseImf baseImf = new ProjectBaseImf(projectName, textarea, radio);
        Long userID = (Long) session.getAttribute("user");
        return projectBaseService.insertBaseProject(baseImf, userID, OWN_PROJECT_STATUS);
    }

    @GetMapping("/all")
    public R getAllProject(HttpSession session) {
        Long userID = (Long) session.getAttribute("user");
        List<Long> idList = projectBaseService.getAllProjectID(userID);
        if (Objects.isNull(idList) || idList.isEmpty()) {
            return R.warn("还没有任何项目~");
        }
        List<ProjectBaseImf> projectList = projectBaseService.getProjects(idList);
        return R.success(projectList, "查询成功");
    }

    @GetMapping("/own")
    public R getOwnProject(HttpSession session) {
        Long userID = (Long) session.getAttribute("user");
        List<Long> idList = projectBaseService.getSelfProjectID(userID);
        if (Objects.isNull(idList) || idList.isEmpty()) {
            return R.warn("还没有任何项目~");
        }
        List<ProjectBaseImf> projectList = projectBaseService.getProjects(idList);
        return R.success(projectList, "查询成功");
    }

    @GetMapping("/join")
    public R getJoinProject(HttpSession session) {
        Long userID = (Long) session.getAttribute("user");
        List<Long> idList = projectBaseService.getJoinProjectID(userID);
        if (Objects.isNull(idList) || idList.isEmpty()) {
            return R.warn("还没有任何项目~");
        }
        List<ProjectBaseImf> projectList = projectBaseService.getProjects(idList);
        return R.success(projectList, "查询成功");
    }

    @GetMapping("/changeImf")
    public R getChangeImf(Long projectId) {
        ProjectChangeBaseImf projectChangeBaseImf = projectChangeBaseImfService.getProjectChangeBaseImfByID(projectId);
        if (Objects.isNull(projectChangeBaseImf)) {
            return R.error("项目信息异常");
        }
        return R.success(projectChangeBaseImf, "项目更新基础信息");
    }

    @DeleteMapping("/deleteProject")
    public R deleteProject(Long projectId) {
        if (projectChangeBaseImfService.deleteProjectByID(projectId)) {
            return R.success("删除成功");
        }
        return R.error("删除失败");
    }

    @GetMapping("/getDirContent")
    public R getDirContent(Long projectId, String morePath) {
        if (Objects.isNull(projectId) || projectId <= 0) {
            return R.error("工程信息异常");
        }
        String path = projectBaseService.getProjectPath(projectId);
        if (StringUtils.isEmpty(path)) {
            return R.warn("空白工程");
        }
        if (!Objects.isNull(morePath)) {
            path += morePath;
        }
        if (FileUtil.isDirectory(path)) {
            Map<String, List<String>> pageFile = FileUtil.getPageFile(path);
            List<String> names = pageFile.get(KeyEnum.FILE_KEY);
            if(!Objects.isNull(names) && !names.isEmpty()){
                List<String> fileRealName = fileBaseImfService.getFileRealName(names);
                pageFile.put(KeyEnum.FILE_KEY, fileRealName);
            }
            return R.success(pageFile, "dir");
        } else {
            return R.error("不是文件夹");
        }
    }

    // morePath自带\
    @GetMapping("/getFileContent")
    public R getLatestFileContent(Long projectId, String morePath) {
        if (Objects.isNull(projectId) || projectId <= 0) {
            return R.error("工程信息异常");
        }
        String path = projectBaseService.getProjectPath(projectId);
        if (StringUtils.isEmpty(path)) {
            return R.error("工程信息异常");
        }
        if (!Objects.isNull(morePath)) {
            String fileId = fileBaseImfService.getFileIdWithCurrentVersion(
                    projectBaseService.getProjectPath(projectId) + morePath);
            path = path + morePath.substring(0, morePath.lastIndexOf("\\") + 1) + fileId;
        }
        R result = fileService.getFileContent(path);
        if (StatusEnum.WARN.equals(result.getCode())) {
            //修复文件
            String prePath = (String) result.getData();
            List<String> logList = fileVersionImfService.getVersionList(projectId, morePath)
                    .stream().map(FileVersionImf::getLatestAction).collect(Collectors.toList());
            String s1 = VersionUtil.getVersion(prePath);
            s1 = s1.substring(0, s1.indexOf('.'));
            int index = Integer.parseInt(s1);
            BufferedImage image = PicCombiner.createImage(prePath);
            while (!prePath.equals(path)) {
                String logStr = logList.get(index);
                // 解析日志字符串
                image = PicCombiner.mapStringToPicLog(logStr, path, image);
                prePath = VersionUtil.getNextVersionPath(prePath);
            }
            PicCombiner.savePic(path, image);
            return R.success(PicCombiner.getPicImf(path), FileConfig.PIC_FILE);
        }
        return result;
    }

    @GetMapping("/search")
    public R searchProjectByKey(String key, HttpSession session) {
        if (StringUtils.isEmpty(key)) {
            return R.warn("搜索关键字不能为空");
        }
        long userId = (Long) session.getAttribute("user");
        Map<String, Object> map = new HashMap<>();
        List<ProjectBaseImf> projectBaseImfList = projectBaseService.getProjectByKey(key, userId);
        map.put("baseImf", projectBaseImfList);
        List<Integer> likeList = projectBaseService.getLikeList(userId, projectBaseImfList);
        map.put("likeList", likeList);
        return R.success(map, "搜索成功");
    }

    @GetMapping("/getOwner")
    public R getOwner(Long projectId) {
        User user = projectBaseService.getOwner(projectId);
        if (Objects.isNull(user)) {
            return R.warn("查找项目拥有者失败");
        }
        return R.success(user, "搜索成功");
    }

    @GetMapping("/applyJoin")
    public R applyJoin(Long projectId, String content, HttpSession session) {
        Long userID = (Long) session.getAttribute("user");
        if (projectBaseService.applyJoin(projectId, userID, content)) {
            return R.success("发送成功");
        }
        return R.error("发送失败");
    }

    @GetMapping("/checkMessageNum")
    public int checkMessageNum(HttpSession session) {
        long userId = (Long) session.getAttribute("user");
        return projectBaseService.checkReceiveApplyNum(userId);
    }

    @GetMapping("/checkAllMessage")
    public R checkAllMessage(HttpSession session) {
        return R.success(projectBaseService.checkAllApply((Long) session.getAttribute("user")), "获取完成");
    }

    @GetMapping("/changeLikeStatus")
    public R changeLikeStatus(Long projectId, Integer newLikeStatus, HttpSession session) {
        if (projectBaseService.changeLikedStatus((Long) session.getAttribute("user"), projectId, newLikeStatus)) {
            if (StatusEnum.LIKED.equals(newLikeStatus)) {
                return R.success("收藏成功");
            }
            return R.success("取消收藏");
        } else {
            if (StatusEnum.LIKED.equals(newLikeStatus)) {
                return R.error("收藏失败");
            }
            return R.error("取消收藏失败");
        }

    }

    @GetMapping("/getLikeProject")
    public R getLikeProject(HttpSession session) {
        return R.success(projectBaseService.getLikeProject((Long) session.getAttribute("user")), "获取成功");

    }

    @PostMapping("/reply")
    public R getReply(@RequestBody ApplyJoinProject applyJoinProject) {
        if (projectBaseService.setReply(applyJoinProject)) {
            return R.success("回复成功");
        }
        return R.error("回复失败");
    }


}
