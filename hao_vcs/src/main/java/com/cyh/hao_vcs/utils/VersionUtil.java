package com.cyh.hao_vcs.utils;

import com.cyh.hao_vcs.common.StatusEnum;
import com.cyh.hao_vcs.entity.FileVersionImf;
import com.cyh.hao_vcs.log.PicLog;

import java.util.Arrays;
import java.util.List;

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

    public static String getNextVersionPath(String path){
        String version = VersionUtil.getVersion(path);
        String nextVersion = getNextVersionWithHeavyUpdate(version);
        return path.replaceFirst(version, nextVersion);
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

    public static String getPreviousVersionWithHeavyUpdate(String version){
        int heavyNum = Integer.parseInt(version.substring(0,version.indexOf(".")));
        heavyNum--;
        return heavyNum + INIT_VERSION_PARSER + INIT_VERSION_PARSER;
    }

    public static String getVersion(String path){
        return path.substring(path.indexOf(LOGO) + LOGO.length(), path.lastIndexOf('.'));
    }


    private static String getNextVersion(String version){
        return String.valueOf(Integer.parseInt(version) + 1);
    }

    public static PicLog getPicLogFromLogString(List<String> logList, PicLog originPic){
        return  null;
    }


//
//    public static void main(String[] args) {
//        mapStringToPicLog("cut##&100-100-100-100##&water##&陈宇豪yoyo##&");
//
//    }

}
