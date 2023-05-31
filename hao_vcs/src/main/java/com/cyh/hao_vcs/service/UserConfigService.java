package com.cyh.hao_vcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyh.hao_vcs.entity.UserConfig;

public interface UserConfigService  extends IService<UserConfig> {

    boolean setMsgAutoReply(Long userId, Integer choice);
}
