package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.service.UserService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@RequestMapping("/doReset")
public class ResetController {
    private final UserService userService;
    private final RedisTemplate redisTemplate;
    private static final String redisKey = "inviteCode";

    public ResetController(UserService userService, RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.userService = userService;
    }

    @PostMapping
    public R tryReset(@RequestBody Map<String, String> map){
        String code = map.get("code");
        Object codeSaved = redisTemplate.opsForValue().get(redisKey);
        if (codeSaved != null && codeSaved.equals(code)) {
            User user = new User();
            user.setPassword(map.get("password"));
            user.setEmail(map.get("email"));
            redisTemplate.delete(redisKey);
            return userService.changePassword(user);
        }
        return R.error("重置失败");
    }


}
