package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.FileConfig;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Objects;

import static com.cyh.hao_vcs.utils.Converter.nameFromFile2Html;

public class Classifier {

    /**
     * 图片限制格式为jpg\png\jpeg
     * @param path
     * @return
     */
    public static R getHtmlPath(String path){
        File file = new File(path);
        String fileName = file.getName();
        if (StringUtils.isEmpty(fileName)) {
            return R.warn("文件名为空");
        }
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String result = null;
        result = textConverter(suffix, fileName, path);
        if(!Objects.isNull(result)){
            return R.success(result,FileConfig.TEXT_FILE);
        }else if(isPic(suffix)){
            return R.success(PicCombiner.getPicImf(path), FileConfig.PIC_FILE);
        }else if(isAudio(suffix)){
            return R.success(path.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH), FileConfig.AUDIO_FILE);
        }else if(isMedio(suffix)){
            return R.success(path.replace(FileConfig.PROJECT_PATH,FileConfig.RELATIVE_PROJECT_PATH), FileConfig.VIDEO_FILE);
        }
        return R.error(null);
    }

    private static boolean isPic(String suffix){
        return "jpg".equals(suffix) || "png".equals(suffix) || "jpeg".equals(suffix);
    }

    private static boolean isAudio(String suffix){
        return "mp3".equals(suffix) || "wav".equals(suffix);
    }

    private static boolean isMedio(String suffix){
        return "mp4".equals(suffix) || "avi".equals(suffix) || "wmv".equals(suffix) || "mpeg".equals(suffix);
    }

    public static String textConverter(String suffix, String fileName, String path) {
        String resPath = null;
        if (Objects.equals(suffix, "doc")) {
            resPath = checkHtmlExists(fileName, FileConfig.DOC_PATH);
            if(StringUtils.isEmpty(resPath)){
                return Converter.doc2Html(fileName, path);
            }
            return resPath;
        }
        if (Objects.equals(suffix, "docx")) {
            resPath = checkHtmlExists(fileName, FileConfig.DOCX_PATH);
            if(StringUtils.isEmpty(resPath)){
                return Converter.docx2Html(fileName, path);
            }
            return resPath;
        }
        if (Objects.equals(suffix, "txt")) {
            resPath = checkHtmlExists(fileName, FileConfig.TXT_PATH);
            if(StringUtils.isEmpty(resPath)){
                return Converter.txt2Html(fileName, path);
            }
            return resPath;
        }
        if (Objects.equals(suffix, "pdf")) {
            resPath = checkHtmlExists(fileName, FileConfig.PDF_PATH);
            if(StringUtils.isEmpty(resPath)){
                return Converter.pdf2Html(fileName, path);
            }
        }
        return null;
    }

    public static String checkHtmlExists(String fileName, String format){
        String path = format + nameFromFile2Html(fileName);
        File file = new File(path);
        if(file.exists()){
            return path;
        }
        return null;
    }

}
