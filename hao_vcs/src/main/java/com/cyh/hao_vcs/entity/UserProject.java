package com.cyh.hao_vcs.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserProject {
    Long userId;

    Long projectId;

    //为0表示user参与该project，为1表示拥有
    int relation;
}
