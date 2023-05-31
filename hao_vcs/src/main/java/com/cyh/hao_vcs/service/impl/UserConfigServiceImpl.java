package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyh.hao_vcs.entity.UserConfig;
import com.cyh.hao_vcs.mapper.UserConfigMapper;
import com.cyh.hao_vcs.service.UserConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserConfigServiceImpl extends ServiceImpl<UserConfigMapper, UserConfig> implements UserConfigService{

    @Autowired
    UserConfigMapper userConfigMapper;

    @Override
    public boolean setMsgAutoReply(Long userId, Integer choice) {
        return false;
    }
}