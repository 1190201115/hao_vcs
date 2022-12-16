package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.service.UserService;
import com.cyh.hao_vcs.utils.InviteCodeGenerator;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/doSignUp")
public class SignUpController {

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;
    private final UserService userService;
    private final RedisTemplate redisTemplate;

    private static final String emailContentPrefix = "欢迎注册Hao_VCS,您的验证码为";
    private static final String emailContentSuffix = ",请不要提供给其他人。五分钟内有效";
    private static final String subject = "Hao_VCS注册验证码";
    private static final String redisKey = "inviteCode";

    public SignUpController(JavaMailSender mailSender, MailProperties mailProperties, UserService signUpService,
                            RedisTemplate redisTemplate) {
        this.mailSender = mailSender;
        this.mailProperties = mailProperties;
        this.userService = signUpService;
        this.redisTemplate = redisTemplate;
    }

    @GetMapping
    public R getSignUpCode(String email) {
        String inviteCode = InviteCodeGenerator.generate();
        System.out.println(inviteCode);
        redisTemplate.opsForValue().set(redisKey, inviteCode, 5, TimeUnit.MINUTES);
        String content = emailContentPrefix + inviteCode + emailContentSuffix;
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(email);
        message.setSubject(subject);
        message.setText(content);
        mailSender.send(message);
        return R.success("验证码发送成功");
    }

    @PostMapping
    public R trySignUp(@RequestBody Map<String, String> map) {
        String code = map.get("code");
        Object codeSaved = redisTemplate.opsForValue().get(redisKey);
        if (codeSaved != null && codeSaved.equals(code)) {
            User user = new User();
            user.setPassword(DigestUtils.md5DigestAsHex(map.get("password").getBytes()));
            user.setEmail(map.get("email"));
            user.setUsername(map.get("username"));
            redisTemplate.delete(redisKey);
            if (StringUtils.isEmpty(user.getUsername())) {
                user.setUsername(user.getEmail());
            }
            return userService.addUser(user);
        }
        return R.error("注册失败");
    }
}
