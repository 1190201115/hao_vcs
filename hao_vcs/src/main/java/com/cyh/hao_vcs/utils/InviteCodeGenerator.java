package com.cyh.hao_vcs.utils;

public class InviteCodeGenerator {

    private static final int multiple = 1000000;
    private static final int len = 6;
    private static final String prefix = "0";

    public static String generate() {
        String code = String.valueOf((int) (Math.random() * multiple));
        while (code.length() < 6) {
            code = prefix + code;
        }
        return code;
    }

}
