package com.cyh.hao_vcs.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.UserDetail;

public interface UserDetailService extends IService<UserDetail> {
    boolean setHead(Long id, String str);
    String getHead(Long id);
    R getUserDetail(Long id);
    boolean updateImf(String username, String signature, Long id);
}
