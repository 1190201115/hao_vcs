package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.config.QiNiuConfig;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.cyh.hao_vcs.common.KeyEnum.DIR_KEY;
import static com.cyh.hao_vcs.common.KeyEnum.FILE_KEY;

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

    public static String saveFile(String dirPath, MultipartFile file){
        String originalFilename = file.getOriginalFilename();
        String fileFormat = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().trim();
        File localFile = new File(dirPath + File.separator + uuid + fileFormat);
        createDir(dirPath);
        InputStream ins = null;
        OutputStream os = null;
        try {
            ins = file.getInputStream();
            os = new FileOutputStream(localFile);
            IOUtils.copy(ins, os);
            os.flush();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (ins != null) {
                    ins.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return uuid;
    }

    public static String getSuffix(MultipartFile file) {
        int dotPos = file.getOriginalFilename().lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        return file.getOriginalFilename().substring(dotPos + 1).toLowerCase();
    }

    public static String getProjectPath(Long projectID,String projectName){
        return FileConfig.PROJECT_PATH + projectID.toString() +"-" + projectName;
    }

    public static void createDir(String dirPath){
        File directory = new File(dirPath);
        if(!directory.exists()){
            directory.mkdir();
        }
    }

    public static void createDir(File directory){
        if(!directory.exists()){
            directory.mkdir();
        }
    }

    public static Map<String, List<String>> getPageFile(String path){
        File page = new File(path);
        File[] allFileList = page.listFiles();
        Map<String, List<String>> map = new HashMap<>();
        List<String> fileList = new ArrayList<>();
        List<String> dirList = new ArrayList<>();
        for(File file:allFileList){
            if(file.isDirectory()){
                dirList.add(file.getName());
            }else{
                fileList.add(file.getName());
            }
        }
        map.put(FILE_KEY,fileList);
        map.put(DIR_KEY,dirList);
        return map;
    }

    public static List<String> removeSuffix(List<String> nameList){
        List<String> preNameList = nameList.stream().map(
                name->{
                    int dotPos = name.lastIndexOf(".");
                    return name.substring(0,dotPos);
                }
        ).collect(Collectors.toList());
        return preNameList;
    }
}
