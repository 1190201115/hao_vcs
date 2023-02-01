package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.config.QiNiuConfig;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class FileUtil {

    // 图片允许的后缀扩展名
    public static String[] IMAGE_FILE_SUFFIX = new String[] { "png", "bmp", "jpg", "jpeg","pdf"};

    public static boolean isPicAllowed(String fileName) {
        for (String ext : IMAGE_FILE_SUFFIX) {
            if (ext.equals(fileName)) {
                return true;
            }
        }
        return false;
    }

    //七牛云文件路径
    public static String getFileUrl(String fileName) {
        if(StringUtils.isNullOrEmpty(fileName)) return null;
        String encodedFileName = null;
        try {
            encodedFileName = URLEncoder.encode(fileName, "utf-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
        String publicUrl = String.format("%s/%s", QiNiuConfig.cdns, encodedFileName);
        Auth auth = Auth.create(QiNiuConfig.accessKey, QiNiuConfig.secretKey);
        publicUrl = auth.privateDownloadUrl(publicUrl, QiNiuConfig.time);
        //下载
        //HttpUtil.downloadFile(finalUrl, FileUtil.file("D:\\sdk\\七牛云"));
        return publicUrl;
    }

    public static boolean downloadFile(String fileName, String savePath){
        return false;
    }

    public static boolean fileIsNotExists(MultipartFile file) {
        return Objects.isNull(file) || file.isEmpty();
    }

    public static boolean saveFile(String path, String content){
        try {
            FileOutputStream fos = new FileOutputStream(path);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(content);
            writer.flush();
            fos.close();
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
        return true;
    }
}
