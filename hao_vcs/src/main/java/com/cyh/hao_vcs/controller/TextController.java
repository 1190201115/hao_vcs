package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.service.FileService;
import com.cyh.hao_vcs.service.TextService;
import com.cyh.hao_vcs.utils.Converter;
import com.cyh.hao_vcs.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.Objects;

@RestController
@RequestMapping("/text")
public class TextController {

    @Autowired
    private TextService textService;

    @GetMapping("/doc")
    public R getDoc(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return R.warn("文件名为空！");
        }
        String res = textService.getDoc(fileName);
        if(StringUtils.isEmpty(res)) {
            return R.error("读取Doc文件失败");
        }
        return R.success(Converter.htmlToString(res), "读取Doc文件成功");
    }
}
