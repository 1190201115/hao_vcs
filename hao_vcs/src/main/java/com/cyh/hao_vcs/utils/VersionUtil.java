package com.cyh.hao_vcs.utils;

public class VersionUtil {

    private static final String LOGO = "-v";
    public static String getVersionSuffix(String version){
        return LOGO + version;
    }
}
