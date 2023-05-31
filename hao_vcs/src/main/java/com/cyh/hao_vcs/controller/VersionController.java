package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.FileVersionImf;
import com.cyh.hao_vcs.entity.PicImf;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.service.FileVersionImfService;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.service.impl.ProjectChangeBaseImfServiceImpl;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.PicCombiner;
import com.cyh.hao_vcs.utils.VersionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;


@RestController
@RequestMapping("/version")
public class VersionController {

    @Autowired
    FileBaseImfService fileBaseImfService;

    @Autowired
    ProjectBaseService projectBaseService;

    @Autowired
    FileVersionImfService fileVersionImfService;

    @Autowired
    ProjectChangeBaseImfServiceImpl projectChangeBaseImfService;

    @PostMapping("/updateText")
    public R updateText(@RequestParam("projectId") Long projectId, @RequestParam("morePath") String morePath,
                        @RequestParam("content") String content, @RequestParam("updateKind") Integer updateKind,
                        @RequestParam("log") String log, HttpSession session) {
        fileVersionImfService.updateText(projectId, morePath, content, updateKind, log,
                (Long) session.getAttribute("user"));
        return R.success("更新成功");
    }

    @PostMapping("/updatePic")
    public R updatePic(@RequestParam("projectId") Long projectId, @RequestParam("morePath") String morePath,
                       @RequestBody MultipartFile file, @RequestParam("log") List<String> logList,
                       HttpSession session) {
        if (Objects.isNull(logList) || logList.isEmpty()) {
            return R.warn("没有更新");
        }
        String path = fileVersionImfService.updatePic(projectId, morePath, file, logList.toString(),
                (Long) session.getAttribute("user"));
        if (Objects.isNull(path)) {
            return R.error("更新失败");
        }
        PicImf picImf = PicCombiner.getPicImf(path);
        return R.success(PicCombiner.getPicImf(path), "更新成功");
    }

    @PostMapping("/deleteOldPic")
    public void deleteOldPic(@RequestBody Map<String, String> map){
        fileVersionImfService.deleteOldPic(Long.parseLong(map.get("projectId")), map.get("morePath"));
    }

    @PostMapping("/combinePic")
    public R updatePic(@RequestParam("projectId") Long projectId, @RequestParam("morePath") String morePath,
                       @RequestBody MultipartFile file, @RequestParam("log") String log,
                       @RequestParam("addPath") String addPath,
                       HttpSession session) {
        fileVersionImfService.updatePic(projectId, morePath, file, log, (Long) session.getAttribute("user"));
        return R.success("更新成功");
    }

    @PostMapping("/addVideoWatermark")
    public R addWatermarkVideo(@RequestBody Map<String, String> map) {
        String newPath = fileVersionImfService.addWatermark(map.get("content"), map.get("path"),
                Integer.parseInt(map.get("version")));
        return Objects.isNull(newPath) ? R.error("水印添加失败") : R.success(newPath);
    }

    @PostMapping("/cutVideo")
    public R cutVideoByDel(@RequestBody Map<String, String> map) {
        if ("1".equals(map.get("radio"))) {
            return fileVersionImfService.updateVideoByDel(map.get("path"), Integer.parseInt(map.get("version")),
                    Double.parseDouble(map.get("startTime")), Double.parseDouble(map.get("endTime")));
        } else {
            return fileVersionImfService.updateVideoBySave(map.get("path"), Integer.parseInt(map.get("version")),
                    Double.parseDouble(map.get("startTime")), Double.parseDouble(map.get("endTime")));
        }
    }

    @PostMapping("/cutAudio")
    public R cutAudioByDel(@RequestBody Map<String, String> map) {
        System.out.println(map.get("radio"));
        if ("1".equals(map.get("radio"))) {
            return fileVersionImfService.updateAudioByDel(map.get("path"), Integer.parseInt(map.get("version")),
                    Double.parseDouble(map.get("startTime")), Double.parseDouble(map.get("endTime")));
        } else {
            return fileVersionImfService.updateAudioBySave(map.get("path"), Integer.parseInt(map.get("version")),
                    Double.parseDouble(map.get("startTime")), Double.parseDouble(map.get("endTime")));
        }
    }

    @PostMapping("/changeAudioSpeed")
    public R changeAudioSpeed(@RequestBody Map<String, String> map) {
        return fileVersionImfService.changeAudioSpeed(map.get("path"), Integer.parseInt(map.get("version")),
                Double.parseDouble(map.get("times")));
    }

    @PostMapping("/changeAudioTone")
    public R changeAudioTone(@RequestBody Map<String, String> map) {
        return fileVersionImfService.changeAudioTone(map.get("path"), Integer.parseInt(map.get("version")),
                Double.parseDouble(map.get("times")));
    }

    @PostMapping("/updateVideo")
    public R saveVideo(@RequestBody Map<String, Object> map, HttpSession session) {
        int version = (int) map.get("version");
        if (Objects.equals(0, version)) {
            return R.warn("保存完成(其实没有更新)");
        }
        fileVersionImfService.updateVideo((Long) session.getAttribute("user"), (String) map.get("path"),
                version, (List<String>) map.get("log"));
        return R.success("保存完成");
    }

    @GetMapping("/getVersionList")
    public R getVersionList(@RequestParam("projectId") Long projectId, @RequestParam("morePath") String morePath) {
        List<FileVersionImf> resList = fileVersionImfService.getVersionList(projectId, morePath);
        if (Objects.isNull(resList) || resList.isEmpty()) {
            return R.warn("版本号查询异常");
        }
        return R.success(resList, "版本列表查询成功");
    }

    @GetMapping("/checkFileVersion")
    public R checkFileVersion(@RequestParam("projectId") Long projectId, @RequestParam("morePath") String morePath,
                              @RequestParam("version") String newVersion, HttpSession session) {
        if (fileVersionImfService.checkFileVersion(projectId, morePath, newVersion,
                (Long) session.getAttribute("user"))) {
            return R.success("版本切换成功");
        }
        return R.error("版本切换失败");
    }

    @GetMapping("/compareText")
    public R compareText(@RequestParam("projectId") Long projectId, @RequestParam("morePath") String morePath,
                         @RequestParam("version") String newVersion) {
        return fileVersionImfService.compareText(projectId, morePath, newVersion);
    }

//    @PostMapping("/deleteCache")
//    public R deleteCache(@RequestBody Map<String, Object> map, HttpSession session){
//        Long projectId = (Long)map.get("projectId");
//        Long userId = (Long) session.getAttribute("user");
//        //验证是否有权限
//        if(projectChangeBaseImfService.checkAuthority(userId, projectId)){
//            //删除除当前版本外的文件
//            projectChangeBaseImfService.deleteCache(projectId);
//        }
//
//        //返回结果
//    }

}
