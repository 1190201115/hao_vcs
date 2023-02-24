package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.service.FileVersionImfService;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.utils.FileUtil;
import com.cyh.hao_vcs.utils.VersionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.File;


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
                        @RequestParam("content") String content, HttpSession session) {
        fileVersionImfService.updateText(projectId,morePath,content,0,(Long)session.getAttribute("user"));
        return R.success("更新成功");
    }

}
