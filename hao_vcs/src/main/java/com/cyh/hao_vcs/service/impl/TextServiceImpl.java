package com.cyh.hao_vcs.service.impl;

import com.cyh.hao_vcs.common.R;
import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.config.FileConfig;
import com.cyh.hao_vcs.service.TextService;
import com.cyh.hao_vcs.utils.Converter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Objects;

@Service
public class TextServiceImpl implements TextService {
    @Override
    public String getDoc(String fileName) {
        Integer kind = Converter.textClassifier(fileName);
        String resFileName = fileName.substring(0, fileName.indexOf(".")) + ".html";
        if(Converter.fileExists(resFileName, kind)){
            return FileConfig.pathMap.get(kind) + resFileName;

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
}
