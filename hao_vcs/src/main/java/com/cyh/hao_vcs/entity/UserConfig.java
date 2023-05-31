package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserConfig {

    @TableId(value = "user_id")
    private Long userId;
    private Integer messageAuto;
    private Integer effective;

    UserConfig(){

    }
}
