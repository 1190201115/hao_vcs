package com.cyh.hao_vcs.utils;

public class ProjectUtil {

    public static String getProjectName(String projectName, Long projectId){
        return projectId.toString() + "-" + projectName;
    }
}
