package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDetail {

    private Long id;

    private String head;

    private String signature;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime signUpTime;

    private LocalDateTime latestLoginTime;
}
