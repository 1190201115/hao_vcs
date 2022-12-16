package com.cyh.hao_vcs.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.User;
import com.cyh.hao_vcs.entity.UserDetail;
import com.cyh.hao_vcs.mapper.UserDetailMapper;
import com.cyh.hao_vcs.mapper.UserMapper;
import com.cyh.hao_vcs.service.FileService;
import com.cyh.hao_vcs.service.UserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Service
public class UserDetailServiceImpl extends ServiceImpl<UserDetailMapper, UserDetail> implements UserDetailService {

    @Autowired
    UserDetailMapper userDetailMapper;

    @Autowired
    FileService fileService;


    public boolean setHead(Long id, String head) {
        UserDetail userDetail = userDetailMapper.selectById(id);
        String oldHead = userDetail.getHead();
        if (!StringUtils.isEmpty(oldHead)) {
            if (!fileService.deleteFile(oldHead)) {
                System.out.println("删除头像失败");
            }
        }
        userDetail.setHead(head);
        return Objects.equals(userDetailMapper.updateById(userDetail), 1);
    }

    public String getHead(Long id) {
        UserDetail userDetail = userDetailMapper.selectById(id);
        if (Objects.isNull(userDetail)) return null;
        return userDetail.getHead();
    }
}
