package com.cyh.hao_vcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.User;

public interface UserService extends IService<User> {
    R addUser(User user);
    R signInWithPassword(User user);
    R changePassword(User user);
}
