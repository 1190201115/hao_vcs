package com.cyh.hao_vcs.config;

import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class FileConfig {

    public static HashMap<Integer, String> pathMap = new HashMap<>();
    public static final String DOC_PATH = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\resources\\static\\text\\doc\\";
    public static final String PDF_PATH = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\resources\\static\\text\\pdf\\";
    public static final String TXT_PATH = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\resources\\static\\text\\txt\\";
    public static final String DOCX_PATH = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\resources\\static\\text\\docx\\";
    public static final String IMAGE_PATH = "D:\\ADeskTop\\project\\bigWork\\hao_vcs\\src\\main\\resources\\static\\image\\";
    public static final String IMAGE_RELATIVE_PATH = "../../image/";
    public static final Integer UNKNOWN_FILE = -1;
    public static final Integer DOC_FILE = 1;
    public static final Integer DOCX_FILE = 2;
    public static final Integer TXT_FILE = 3;
    public static final Integer PDF_FILE = 4;

    static {
        pathMap.put(FileConfig.DOC_FILE, DOC_PATH);
        pathMap.put(FileConfig.DOCX_FILE, DOCX_PATH);
        pathMap.put(FileConfig.TXT_FILE, TXT_PATH);
        pathMap.put(FileConfig.PDF_FILE, PDF_PATH);
    }
}
