package com.cyh.hao_vcs.service.impl;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.service.TextService;
import com.cyh.hao_vcs.utils.Converter;
import com.cyh.hao_vcs.utils.FileUtil;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
public class TextServiceImpl implements TextService {
    @Override
    public String getDoc(String fileName) {
        String resFileName = fileName.substring(0, fileName.indexOf(".")) + ".html";
        if(Converter.fileExists(resFileName)){
            return Converter.getFilePath(resFileName);
        }
        return Converter.wordToHtml(fileName);
    }

    @Override
    public String getDocx(String fileName) {
        return null;
    }

    @Override
    public String getTxt(String fileName) {
        return null;
    }

    @Override
    public boolean saveDoc(String fileName, String content) {
        if(Objects.equals(Converter.textClassifier(fileName), FileConfig.UNKNOWN_FILE))
            return false;
        return FileUtil.saveFile(Converter.getFilePath(fileName),content);
    }
}
