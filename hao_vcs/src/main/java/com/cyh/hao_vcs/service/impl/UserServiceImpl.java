package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.entity.UserDetail;
import com.cyh.hao_vcs.mapper.UserDetailMapper;
import com.cyh.hao_vcs.mapper.UserMapper;
import com.cyh.hao_vcs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    final UserMapper userMapper;
    final UserDetailMapper userDetailMapper;

    public UserServiceImpl(UserMapper userMapper, UserDetailMapper userDetailMapper) {
        this.userMapper = userMapper;
        this.userDetailMapper = userDetailMapper;
    }

    @Transactional
    public R addUser(User user) {
        if (invalidForm(user)) {
            return R.error("邮箱或密码为空！");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", user.getEmail());
        if (userMapper.selectOne(queryWrapper) != null) {
            return R.error("邮箱已存在！可尝试找回密码");
        }
        userMapper.insert(user);
        UserDetail userDetail = new UserDetail();
        userDetail.setId(user.getId());
        userDetailMapper.insert(userDetail);
        return R.success("注册成功");
    }

    public R signInWithPassword(User user) {
        if (invalidForm(user)) {
            return R.error("邮箱或密码为空！");
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", user.getEmail());
        User findUser = userMapper.selectOne(queryWrapper);
        if (Objects.isNull(findUser)) {
            return R.error("账号不存在！请先注册");
        }
        if (!findUser.getPassword().equals(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()))) {
            return R.error("密码错误");
        }
        UserDetail tempUser = userDetailMapper.selectById(findUser.getId());
        tempUser.setLatestLoginTime(LocalDateTime.now());
        userDetailMapper.updateById(tempUser);
        return R.success(findUser, "登录成功");
    }

    /**
     * 修改密码。接收email和password即可，email用于定位user
     *
     * @param user
     * @return
     */
    public R changePassword(User user) {
        if (invalidForm(user)) {
            return R.error("邮箱或密码为空！");
        }
        String email = user.getEmail();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        User findUser = userMapper.selectOne(queryWrapper);
        if (Objects.isNull(findUser)) {
            return R.error("账号不存在！请先注册");
        }
        findUser.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        userMapper.updateById(findUser);
        return R.success("修改成功");
    }

    private boolean invalidForm(User user) {
        return StringUtils.isEmpty(user.getEmail()) || StringUtils.isEmpty(user.getPassword());
    }

}
