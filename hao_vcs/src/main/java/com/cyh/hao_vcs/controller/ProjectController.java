package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.ProjectBaseImf;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.service.ProjectBaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/createProject")
public class ProjectController {

    private static String PUBLIC_STATUS = "public";

    @Autowired
    ProjectBaseService projectBaseService;

    @PostMapping
    public R createProject(@RequestBody Map<String, String> map) {
        String projectName = map.get("projectName");
        String textarea = map.get("textarea");
        int radio = PUBLIC_STATUS.equals(map.get("radio")) ? 1 : 0;
        ProjectBaseImf baseImf = new ProjectBaseImf(projectName,textarea,radio);
        return projectBaseService.insertBaseProject(baseImf);
    }
}
