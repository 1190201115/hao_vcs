package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Objects;


@RestController
@RequestMapping("/doLogin")
public class LoginController {

    private final UserService userService;

    public LoginController(UserService loginService) {
        this.userService = loginService;
    }
    @PostMapping
    public R tryLogin(@RequestBody User user, HttpSession session){
        R r = userService.signInWithPassword(user);
        User findUser = (User)r.getData();
        if(Objects.equals(r.getCode(), StatusEnum.SUCCESS)){
            session.setAttribute("user",findUser.getId());
        }
        return r;
    }

}
