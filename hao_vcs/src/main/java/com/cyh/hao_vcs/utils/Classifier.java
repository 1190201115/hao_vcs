package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.entity.PicImf;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Objects;


public class Classifier {

    /**
     * 图片限制格式为jpg\png\jpeg
     *
     * @param path
     * @return
     */
    public static R getHtmlPath(String path) {
        File file = new File(path);
        String fileName = file.getName();
        if (StringUtils.isEmpty(fileName)) {
            return R.warn("文件名为空");
        }
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String result = null;
        result = textConverter(suffix, fileName, path);
        if (!Objects.isNull(result)) {
            return R.success(result, FileConfig.TEXT_FILE);
        } else if (isPic(suffix)) {
            PicImf picImf = PicCombiner.getPicImf(path);
            if (Objects.isNull(picImf)) {
                String version = VersionUtil.getVersion(path);
                path = findLastExistPath(version, path);
                if (Objects.isNull(path)) {
                    return R.error("找不到文件");
                }
                return R.warn(path, FileConfig.PIC_FILE);
            }
            return R.success(picImf, FileConfig.PIC_FILE);
        } else if (isAudio(suffix)) {
            return R.success(AudioProcessor.getAudioImf(path), FileConfig.AUDIO_FILE);
        } else if (isMedio(suffix)) {
            return R.success(VideoProcessor.getVideoImf(path), FileConfig.VIDEO_FILE);
        }
        return R.error("获取文件内容失败");
    }

    private static String findLastExistPath(String version, String path) {
        if (StatusEnum.INIT_VERSION.equals(version)) {
            return null;
        }
        String previousVersion = VersionUtil.getPreviousVersionWithHeavyUpdate(version);
        path = path.replaceFirst(version, previousVersion);
        while (!new File(path).exists()) {
            version = previousVersion;
            if (StatusEnum.INIT_VERSION.equals(version)) {
                return null;
            }
            previousVersion = VersionUtil.getPreviousVersionWithHeavyUpdate(version);
            path = path.replaceFirst(version, previousVersion);
        }
        return path;
    }

    private static boolean isPic(String suffix) {
        return "jpg".equals(suffix) || "png".equals(suffix) || "jpeg".equals(suffix);
    }

    private static boolean isAudio(String suffix) {
        return "mp3".equals(suffix) || "wav".equals(suffix);
    }

    private static boolean isMedio(String suffix) {
        return "mp4".equals(suffix) || "avi".equals(suffix) || "wmv".equals(suffix) || "mpeg".equals(suffix);
    }

    public static String textConverter(String suffix, String fileName, String path) {
        String resPath = null;
        if (Objects.equals(suffix, "doc")) {
            resPath = checkHtmlExists(fileName, FileConfig.DOC_PATH);
            if (StringUtils.isEmpty(resPath)) {
                return Converter.doc2Html(fileName, path);
            }
            return resPath;
        }
        if (Objects.equals(suffix, "docx")) {
            resPath = checkHtmlExists(fileName, FileConfig.DOCX_PATH);
            if (StringUtils.isEmpty(resPath)) {
                return Converter.docx2Html(fileName, path);
            }
            return resPath;
        }
        if (Objects.equals(suffix, "txt")) {
            resPath = checkHtmlExists(fileName, FileConfig.TXT_PATH);
            if (StringUtils.isEmpty(resPath)) {
                return Converter.txt2Html(fileName, path);
            }
            return resPath;
        }
        if (Objects.equals(suffix, "pdf")) {
            resPath = checkHtmlExists(fileName, FileConfig.PDF_PATH);
            if (StringUtils.isEmpty(resPath)) {
                return Converter.pdf2Html(fileName, path);
            }
        }
        return null;
    }

    public static String checkHtmlExists(String fileName, String format) {
        String path = format + Converter.nameFromFile2Html(fileName);
        File file = new File(path);
        if (file.exists()) {
            return path;
        }
        return null;
    }

    public static void main(String[] args) {
    }

}
