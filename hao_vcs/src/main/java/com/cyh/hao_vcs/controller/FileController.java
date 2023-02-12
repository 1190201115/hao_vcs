package com.cyh.hao_vcs.controller;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.entity.FileBaseImf;
import com.cyh.hao_vcs.service.FileBaseImfService;
import com.cyh.hao_vcs.service.FileService;
import com.cyh.hao_vcs.service.UserDetailService;
import com.cyh.hao_vcs.utils.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserDetailService userDetailService;

    @Autowired
    private FileBaseImfService fileBaseImfService;


    @PostMapping("/head")
    public R uploadImg(MultipartFile file, HttpSession session) {
        if (FileUtil.fileIsNotExists(file)) {
            return R.warn("文件为空！");
        }
        R res = fileService.saveImage(file);
        if (!Objects.isNull(res)) {
            if (userDetailService.setHead((Long) session.getAttribute("user"), res.getMsg())) {
                String path = FileUtil.getFileUrl(res.getMsg());
                if (Objects.isNull(path)) {
                    return R.error("保存头像失败！");
                }
                return R.success(path, "保存头像成功");
            }
        }
        return R.error("保存头像失败！");
    }

    @GetMapping("/head")
    public R getHeadUrl(HttpSession session) {
        String fileName = userDetailService.getHead((Long) session.getAttribute("user"));
        if (!StringUtils.isEmpty(fileName)) {
            String headUrl = FileUtil.getFileUrl(fileName);
            if (!Objects.isNull(headUrl)) {
                return R.success(headUrl, "获取头像成功");
            }
        }
        return R.error(null);
    }

    @PostMapping("/uploadFile")
    public R uploadFile(@RequestParam("id") Long projectId, @RequestParam("file") MultipartFile file,
                             @RequestParam("name") String projectName) {
        if(fileBaseImfService.insertFileIntoProject(projectId, projectName, file)){
            return R.success("上传成功");
        }
        return R.error("上传失败");
    }



}
