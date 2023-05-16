package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.entity.ProjectConfig;
import com.cyh.hao_vcs.service.ProjectChangeBaseImfService;
import com.cyh.hao_vcs.service.ProjectConfigService;
import com.cyh.hao_vcs.service.impl.ProjectConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ProjectChangeBaseImfService projectChangeBaseImfService;

    @GetMapping("/changeCache")
    public R changeCache(@RequestParam("projectId") Long projectId, @RequestParam("switches") Boolean switches,
            HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        if(projectChangeBaseImfService.checkAuthority(userId, projectId)){
            int choice = switches ? StatusEnum.AUTO_DELETE_CACHE_OFF : StatusEnum.AUTO_DELETE_CACHE_ON;
            projectConfigService.saveOrUpdate(new ProjectConfig(projectId, choice));
//            if(!projectConfigService.updateById(new ProjectConfig(projectId, choice))){
//                projectConfigService.save(new ProjectConfig(projectId, choice));
//            }
            return R.success("更新完成");
        }
        return R.warn("没有更新权限");
    }

    @GetMapping("/getCacheStrategy")
    public boolean getCacheStrategy(@RequestParam("projectId") Long projectId){
        return projectConfigService.getById(projectId).getAutoDeleteCache() == 0;
    }

}
