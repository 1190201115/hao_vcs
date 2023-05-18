package com.cyh.hao_vcs.common;

import lombok.Data;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

@Data
public class R<T> implements Serializable {

    private Integer code;

    private String msg;

    private T data;

    private Map map = new HashMap();

    public static <T> R<T> success(T object, String msg) {
        R<T> r = new R<T>();
        r.data = object;
        r.msg = msg;
        r.code = 20000;
        return r;
    }

    public static <T> R<T> success(String msg) {
        R<T> r = new R<T>();
        r.msg = msg;
        r.code = 20000;
        return r;
    }

    public static <T> R<T> warn(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 30000;
        return r;
    }
    public static <T> R<T> warn(T object, String msg) {
        R r = new R();
        r.data = object;
        r.msg = msg;
        r.code = 30000;
        return r;
    }


    public static <T> R<T> error(String msg) {
        R r = new R();
        r.msg = msg;
        r.code = 40000;
        return r;
    }

    public R<T> add(String key, Object value) {
        this.map.put(key, value);
        return this;
    }
}
