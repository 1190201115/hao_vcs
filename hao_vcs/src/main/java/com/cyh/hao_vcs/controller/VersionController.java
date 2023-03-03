package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.FileVersionImf;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.service.FileVersionImfService;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.VersionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.List;
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

    @PostMapping("/updateText")
    public R updateText(@RequestParam("projectId")Long projectId, @RequestParam("morePath")String morePath,
                        @RequestParam("content") String content, @RequestParam("updateKind") Integer updateKind,
                        @RequestParam("log") String log,HttpSession session) {
        fileVersionImfService.updateText(projectId,morePath,content,updateKind, log, (Long)session.getAttribute("user"));
        return R.success("更新成功");
    }

    @GetMapping("/getVersionList")
    public R getVersionList(@RequestParam("projectId")Long projectId, @RequestParam("morePath")String morePath) {
        List<FileVersionImf> resList = fileVersionImfService.getVersionList(projectId,morePath);
        if(Objects.isNull(resList) || resList.isEmpty()){
            return R.warn("版本号查询异常");
        }
        return R.success(resList,"版本列表查询成功");
    }

    @GetMapping("/checkFileVersion")
    public R checkFileVersion(@RequestParam("projectId")Long projectId, @RequestParam("morePath")String morePath,
                              @RequestParam("version") String newVersion, HttpSession session) {
        if(fileVersionImfService.checkFileVersion(projectId,morePath,newVersion, (Long)session.getAttribute("user"))){
            return R.success("版本切换成功");
        }
        return R.error("版本切换失败");
    }

    @GetMapping("/compareText")
    public R compareText(@RequestParam("projectId")Long projectId, @RequestParam("morePath")String morePath,
                         @RequestParam("version") String newVersion){
        return fileVersionImfService.compareText(projectId,morePath,newVersion);
    }

}
