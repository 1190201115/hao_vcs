package com.cyh.hao_vcs.config;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.HashMap;

@Component
public class FileConfig {

    public static HashMap<Integer, String> pathMap = new HashMap<>();
    public static final String DOC_PATH = "D:\\ADeskTop\\project\\bigWork\\html\\doc\\";
    public static final String PDF_PATH = "D:\\ADeskTop\\project\\bigWork\\html\\pdf\\";
    public static final String TXT_PATH = "D:\\ADeskTop\\project\\bigWork\\html\\txt\\";
    public static final String DOCX_PATH = "D:\\ADeskTop\\project\\bigWork\\html\\docx\\";
    public static final String IMAGE_PATH = "D:\\ADeskTop\\project\\bigWork\\image\\";
    public static final String PROJECT_PATH = "D:\\ADeskTop\\project\\bigWork\\repository\\";
    public static final Integer UNKNOWN_FILE = -1;
    public static final Integer EMPTY_FILE = 0;
    public static final Integer DOC_FILE = 1;
    public static final Integer DOCX_FILE = 2;
    public static final Integer TXT_FILE = 3;
    public static final Integer PDF_FILE = 4;

    static {
        pathMap.put(FileConfig.DOC_FILE, DOC_PATH);
        pathMap.put(FileConfig.DOCX_FILE, DOCX_PATH);
        pathMap.put(FileConfig.TXT_FILE, TXT_PATH);
        pathMap.put(FileConfig.PDF_FILE, PDF_PATH);
        pathMap.put(FileConfig.UNKNOWN_FILE, "");
    }
}
