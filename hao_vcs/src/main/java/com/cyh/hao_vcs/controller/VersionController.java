package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.ProjectBaseImf;
import com.cyh.hao_vcs.utils.FileUtil;
import com.qiniu.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/version")
public class VersionController {

    @PostMapping("/updateText")
    public R updateText(@RequestParam("projectId")Long projectId, @RequestParam("morePath")String morePath,
                        @RequestParam("content") String content) {
        return R.success("查询成功");
    }

}
