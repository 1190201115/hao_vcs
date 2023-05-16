package com.cyh.hao_vcs.log;

import lombok.Data;

import java.util.List;


@Data
public class PicLog {
    // 标记图片的宽和高
    Double pic_width;
    Double pic_height;

    // 标记图片的偏移量
    Double startX;
    Double startY;

    List<String> waterMarks;
}
