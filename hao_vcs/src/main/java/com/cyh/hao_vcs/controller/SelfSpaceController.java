package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.entity.UserDetail;
import com.cyh.hao_vcs.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/selfspace")
public class SelfSpaceController {

    @Autowired
    UserDetailService userDetailService;

    @GetMapping
    private R getUserDetail(HttpSession session) {
        return userDetailService.getUserDetail((Long) session.getAttribute("user"));
    }

    @PostMapping
    public R tryReset(@RequestBody Map<String, String> map, HttpSession session) {
        if (userDetailService.updateImf(map.get("username"), map.get("signature"), (Long) session.getAttribute("user"))) {
            return R.success("更新信息成功");
        }
        return R.error("用户状态异常");
    }
}
