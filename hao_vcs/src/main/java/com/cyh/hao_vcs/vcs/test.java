package com.cyh.hao_vcs.vcs;

public class test {
    public static void main(String[] args) {
        int a = 1;
        assert countNum(a) : "aaa";
        System.out.println(a);
    }

    public static boolean countNum(int num){
        return false;
    }
}
