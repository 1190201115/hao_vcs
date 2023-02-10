package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserDetail {

    @TableId(value = "id")
    private Long id;

    private String head;

    private String signature;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime signUpTime;

    private LocalDateTime latestLoginTime;
}
