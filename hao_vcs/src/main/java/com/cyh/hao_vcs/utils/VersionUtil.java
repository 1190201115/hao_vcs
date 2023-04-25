package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.StatusEnum;

public class VersionUtil {

    public static final String LOGO = "-v";
    public static String getVersionSuffix(String version){
        return LOGO + version;
    }

    private static final String INIT_VERSION_PARSER = ".0";

    public static String getNextVersion(String version, Integer updateKind){
        if(StatusEnum.LIGHT_UPDATE.equals(updateKind)){
            return getNextVersionWithLightUpdate(version);
        }else if(StatusEnum.MIDDLE_UPDATE.equals(updateKind)){
            return getNextVersionWithMiddleUpdate(version);
        }
        return getNextVersionWithHeavyUpdate(version);

    }

    private static String getNextVersionWithLightUpdate(String version){
        int lightNum = Integer.parseInt(version.substring(version.lastIndexOf(".")+1));
        lightNum++;
        return version.substring(0,version.lastIndexOf(".")+1)+lightNum;
    }

    private static String getNextVersionWithMiddleUpdate(String version){
        int midNum = Integer.parseInt(version.substring(version.indexOf(".")+1, version.lastIndexOf(".")));
        midNum++;
        return version.substring(0,version.indexOf(".")+1)+midNum+INIT_VERSION_PARSER;
    }

    private static String getNextVersionWithHeavyUpdate(String version){
        int heavyNum = Integer.parseInt(version.substring(0,version.indexOf(".")));
        heavyNum++;
        return heavyNum + INIT_VERSION_PARSER + INIT_VERSION_PARSER;
    }

    private static String getNextVersion(String version){
        return String.valueOf(Integer.parseInt(version) + 1);
    }

}
