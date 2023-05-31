package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.config.QiNiuConfig;
import com.qiniu.util.Auth;
import com.qiniu.util.StringUtils;
import org.apache.commons.io.IOUtils;
import org.bytedeco.javacv.FFmpegFrameGrabber;
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
    public static String[] IMAGE_FILE_SUFFIX = new String[]{"png", "bmp", "jpg", "jpeg", "pdf"};

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
        if (StringUtils.isNullOrEmpty(fileName)) return null;
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

    public static boolean fileIsNotExists(MultipartFile file) {
        return Objects.isNull(file) || file.isEmpty();
    }

    public static boolean saveFile(String path, String content) {
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

    public static String saveTextAsHtml(String fileName, String fileIdWithVersion, String content) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String path = null;
        if (Objects.equals(suffix, "doc")) {
            path = FileConfig.DOC_PATH + fileIdWithVersion + ".html";
        }
        if (Objects.equals(suffix, "docx")) {
            path = FileConfig.DOCX_PATH + fileIdWithVersion + ".html";
        }
        if (Objects.equals(suffix, "txt")) {
            path = FileConfig.TXT_PATH + fileIdWithVersion + ".html";
        }
        if (Objects.equals(suffix, "pdf")) {
            path = FileConfig.PDF_PATH + fileIdWithVersion + ".html";
        }
        try {
            FileOutputStream fos = new FileOutputStream(path);
            OutputStreamWriter writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8);
            writer.write(content);
            writer.flush();
            fos.close();
        } catch (Exception e) {
            System.out.println(e);
        }
        return path;
    }

    /**
     * @param
     * @param suffix .jpeg
     * @return
     */
    public static boolean updatePicAndChangeVersion(String fullPath, String suffix, MultipartFile file) {
        return !Objects.isNull(saveFile(fullPath + suffix, file));
    }

    public static String saveFile(String completePath, MultipartFile file) {
        File localFile = new File(completePath);
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
        return "success";
    }

    public static double getMediaTime(String path) throws FFmpegFrameGrabber.Exception {
        FFmpegFrameGrabber grabber = FFmpegFrameGrabber.createDefault(path);
        grabber.start();
        double durationInSec = grabber.getFormatContext().duration() / 1000000.0;
        grabber.stop();
        return durationInSec;
    }

    public static String saveFile(String dirPath, String version, MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        String fileFormat = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uuid = UUID.randomUUID().toString().trim();
        createDir(dirPath);
        saveFile(dirPath + File.separator + uuid + VersionUtil.getVersionSuffix(version) + fileFormat, file);
        return uuid;
    }

    //不包含.
    public static String getSuffix(String filePath) {
        int dotPos = filePath.lastIndexOf(".");
        if (dotPos < 0) {
            return null;
        }
        return filePath.substring(dotPos + 1);
    }

    public static String getProjectPath(Long projectID, String projectName) {
        return FileConfig.PROJECT_PATH + projectID.toString() + "-" + projectName;
    }

    public static void createDir(String dirPath) {
        File directory = new File(dirPath);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public static void createDir(File directory) {
        if (!directory.exists()) {
            directory.mkdir();
        }
    }

    public static Map<String, List<String>> getPageFile(String path) {
        File page = new File(path);
        File[] allFileList = page.listFiles();
        Map<String, List<String>> map = new HashMap<>();
        List<String> fileList = new ArrayList<>();
        List<String> dirList = new ArrayList<>();
        String tempDirName = "";
        if (allFileList != null) {
            for (File file : allFileList) {
                if (file.isDirectory()) {
                    File[] tempFileList = file.listFiles();
                    tempDirName = file.getName();
                    while (!Objects.isNull(tempFileList) &&
                            tempFileList.length == 1) {
                        File tempFile = tempFileList[0];
                        if (tempFileList[0].isDirectory()) {
                            tempDirName = tempDirName + "\\" + tempFile.getName();
                            tempFileList = tempFileList[0].listFiles();
                        } else {
                            break;
                        }
                    }
                    dirList.add(tempDirName);
                } else {
                    fileList.add(file.getName());
                }
            }
        }
        map.put(FILE_KEY, fileList);
        map.put(DIR_KEY, dirList);
        return map;
    }

    public static boolean isDirectory(String path) {
        return new File(path).isDirectory();
    }

    public static List<String> removeSuffix(List<String> nameList) {
        List<String> preNameList = nameList.stream().map(
                name -> {
                    int dotPos = name.lastIndexOf(".");
                    return name.substring(0, dotPos);
                }
        ).collect(Collectors.toList());
        return preNameList;
    }

    public static List<String> removeSuffixContainVersion(List<String> nameList) {
        List<String> preNameList = nameList.stream().map(
                name -> {
                    int dotPos = name.lastIndexOf("-");
                    if (dotPos < 0) return null;
                    return name.substring(0, dotPos);
                }
        ).collect(Collectors.toList());
        return preNameList;
    }

    public static String removeSuffix(String name) {
        return name.substring(0, name.lastIndexOf("."));
    }

    public static String getHtmlStorePath(String suffix) {
        if (Objects.equals(suffix, "doc")) {
            return FileConfig.DOC_PATH;
        }
        if (Objects.equals(suffix, "docx")) {
            return FileConfig.DOCX_PATH;
        }
        if (Objects.equals(suffix, "txt")) {
            return FileConfig.TXT_PATH;
        }
        if (Objects.equals(suffix, "pdf")) {
            return FileConfig.PDF_PATH;
        }
        return null;
    }

    public static String getDiffFileName(String fileName, String originVersion, String version) {
        return fileName + "-" + originVersion + "^" + version + ".html";
    }
}
