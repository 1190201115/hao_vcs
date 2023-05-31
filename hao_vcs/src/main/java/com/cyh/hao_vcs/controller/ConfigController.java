package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.entity.ProjectConfig;
import com.cyh.hao_vcs.entity.UserConfig;
import com.cyh.hao_vcs.service.ProjectChangeBaseImfService;
import com.cyh.hao_vcs.service.ProjectConfigService;
import com.cyh.hao_vcs.service.UserConfigService;
import com.cyh.hao_vcs.service.impl.ProjectConfigServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@RestController
@RequestMapping("/config")
public class ConfigController {

    @Autowired
    ProjectConfigService projectConfigService;

    @Autowired
    ProjectChangeBaseImfService projectChangeBaseImfService;

    @Autowired
    UserConfigService userConfigService;

    @GetMapping("/changeCache")
    public R changeCache(@RequestParam("projectId") Long projectId, @RequestParam("switches") Boolean switches,
                         HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        if (projectChangeBaseImfService.checkAuthority(userId, projectId)) {
            int choice = switches ? StatusEnum.AUTO_DELETE_CACHE_ON : StatusEnum.AUTO_DELETE_CACHE_OFF;
            projectConfigService.saveOrUpdate(new ProjectConfig(projectId, choice));
//            if(!projectConfigService.updateById(new ProjectConfig(projectId, choice))){
//                projectConfigService.save(new ProjectConfig(projectId, choice));
//            }
            return R.success("更新完成");
        }
        return R.warn("没有更新权限");
    }

    @GetMapping("/getCacheStrategy")
    public boolean getCacheStrategy(@RequestParam("projectId") Long projectId) {
        ProjectConfig projectConfig = projectConfigService.getById(projectId);
        if (!Objects.isNull(projectConfig)) {
            return projectConfig.getAutoDeleteCache() == 1;
        }
        return false;
    }

    @GetMapping("/msgAutoReply")
    public R msgAutoReply(@RequestParam("reply") Boolean config, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        int choice = config ? StatusEnum.AUTO_ACCEPT_MSG : StatusEnum.AUTO_REFUSE_MSG;
        userConfigService.saveOrUpdate(new UserConfig(userId, choice, StatusEnum.AUTO_REPLY_MSG_EFFECTIVE));
        return R.success("更新完成");
    }

    @GetMapping("/changeMsgAutoEffect")
    public R changeMsgAutoEffect(@RequestParam("config") Boolean config, @RequestParam("reply") Boolean reply,
                                 HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        int choice = reply ? StatusEnum.AUTO_ACCEPT_MSG : StatusEnum.AUTO_REFUSE_MSG;
        int effect = config ? StatusEnum.AUTO_REPLY_MSG_EFFECTIVE : StatusEnum.AUTO_REPLY_MSG_INEFFECTIVE;
        userConfigService.saveOrUpdate(new UserConfig(userId, choice, effect));
        return R.success("更新完成");
    }

    @GetMapping("/getMsgStrategy")
    public R getMsgStrategy(HttpSession session) {
        UserConfig userConfig = userConfigService.getById((Long) session.getAttribute("user"));
        if (!Objects.isNull(userConfig) && StatusEnum.AUTO_REPLY_MSG_EFFECTIVE.equals(userConfig.getEffective())) {
            Integer setting = userConfig.getMessageAuto();
            if (StatusEnum.AUTO_ACCEPT_MSG.equals(setting)) {
                return R.success("accept");
            } else if (StatusEnum.AUTO_REFUSE_MSG.equals(setting)) {
                return R.success("refuse");
            }
        }
        return R.success("closed");
    }

}
