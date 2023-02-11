package com.cyh.hao_vcs.common;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * mp的自动填充策略
 */

@Component
public class AutoFillData implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("signUpTime", LocalDateTime.now());
//        metaObject.setValue("createTime", LocalDateTime.now());
//        metaObject.setValue("updateTime", LocalDateTime.now());
//            metaObject.setValue("createUser",BaseContext.getCurrentId());
//            metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
//            metaObject.setValue("updateTime",LocalDateTime.now());
//            metaObject.setValue("updateUser",BaseContext.getCurrentId());
    }
}

