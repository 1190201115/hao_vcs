package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.ProjectBaseImf;
import com.cyh.hao_vcs.entity.ProjectChangeBaseImf;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.service.ProjectBaseService;
import com.cyh.hao_vcs.service.ProjectChangeBaseImfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private static String PUBLIC_STATUS = "public";

    @Autowired
    ProjectBaseService projectBaseService;

    @Autowired
    ProjectChangeBaseImfService projectChangeBaseImfService;

    private static Integer OWN_PROJECT_STATUS = 1;

    @PostMapping
    public R createProject(@RequestBody Map<String, String> map, HttpSession session) {
        String projectName = map.get("projectName");
        String textarea = map.get("textarea");
        int radio = PUBLIC_STATUS.equals(map.get("radio")) ? 1 : 0;
        ProjectBaseImf baseImf = new ProjectBaseImf(projectName,textarea,radio);
        Long userID = (Long)session.getAttribute("user");
        return projectBaseService.insertBaseProject(baseImf, userID, OWN_PROJECT_STATUS);
    }

    @GetMapping("/all")
    public R getAllProject(HttpSession session) {
        Long userID = (Long)session.getAttribute("user");
        List<Long> idList = projectBaseService.getAllProjectID(userID);
        if(Objects.isNull(idList) || idList.isEmpty()) {
            return R.warn("还没有任何项目~");
        }
        List<ProjectBaseImf> projectList = projectBaseService.getProjects(idList);
        return R.success(projectList, "查询成功");
    }

    @GetMapping("/changeImf")
    public R getChangeImf(Long projectId){
        ProjectChangeBaseImf projectChangeBaseImf = projectChangeBaseImfService.getProjectChangeBaseImfByID(projectId);
        if( Objects.isNull(projectChangeBaseImf)){
            return R.error("项目信息异常");
        }
        return R.success(projectChangeBaseImf, "项目更新基础信息");
    }
}
