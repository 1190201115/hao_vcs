package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.config.FileConfig;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Objects;

public class Classifier {

    public static R getHtmlPath(String path){
        File file = new File(path);
        String fileName = file.getName();
        if (StringUtils.isEmpty(fileName)) {
            return R.warn("文件名为空");
        }
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String result = textClassifier(suffix, fileName, path);
        if(!Objects.isNull(result)){
            return R.success(result,FileConfig.TEXT_FILE);
        }
        return R.error(null);
    }

    public static String textClassifier(String suffix, String fileName, String path) {
        if (Objects.equals(suffix, "doc")) {
            return Converter.doc2Html(fileName, path);
        }
        if (Objects.equals(suffix, "docx")) {
            return Converter.docx2Html(fileName, path);
        }
        if (Objects.equals(suffix, "txt")) {
            return Converter.txt2Html(fileName, path);
        }
        if (Objects.equals(suffix, "pdf")) {
            return Converter.pdf2Html(fileName, path);
        }
        return null;
    }

}
