package com.cyh.hao_vcs.common;

public class StatusEnum {
    public static Integer SUCCESS = 20000;
    public static Integer WARN = 30000;
    public static Integer ERROR = 40000;

    public static String INIT_VERSION = "1.0.0";
    public static Integer DELETE_SAFE = 1;

    public static Integer LIGHT_UPDATE = 0;
    public static Integer MIDDLE_UPDATE = 1;
    public static Integer HEAVY_UPDATE = 2;

    public static Integer JOIN_PROJECT = 0;
    public static Integer CREATE_PROJECT = 1;

    public static Integer WAIT = 0;
    public static Integer IGNORE = 1;
    public static Integer APPROVE = 2;

}
