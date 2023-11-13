package com.bitdf.txing.txcodesandbox.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Lizhiwei
 * @date 2023/8/22 11:10:49
 * @description 用于封装http响应体
 */
public class R extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    public R() {
        put("code", 200);
        put("msg", "success");
    }

    public static R error() {
        return error(500, "未知异常，请联系管理员");
    }

    public static R error(String msg) {
        return error(500, msg);
    }

    public static R error(Enum enums)  {
        int code = 500;
        String msg = "未知异常，请联系管理员";
        try {
            Field codeField = enums.getClass().getDeclaredField("code");
            Field msgField = enums.getClass().getDeclaredField("msg");
            codeField.setAccessible(true);
            msgField.setAccessible(true);
            code = codeField.getInt(enums);
            msg = (String) msgField.get(enums);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return R.error(code, msg);
    }


    public static R error(int code, String msg) {
        R r = new R();
        r.put("code", code);
        r.put("msg", msg);
        return r;
    }

    public static R ok(Object value){
        R r=new R();
        r.put("data", value);
        return r;
    }



    public static R ok(int code,String msg){
        R r = new R();
        r.put("code",code).put("msg",msg);
        return r;
    }

    public static R ok(String msg) {
        R r = new R();
        r.put("msg", msg);
        return r;
    }

    public static R ok(Map<String, Object> map) {
        R r = new R();
        r.putAll(map);
        return r;
    }

    public static R ok() {
        return new R();
    }

    @Override
    public R put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
