package com.linyi.takeout.common;


/**
 * 基于ThreadLocal封装的工具类，用于保存和获取当前登陆用户的id
 * 作用在同一个线程之内
 */
public class BaseContext {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    //保存用户ID
    public static void setCurrentId(Long id) {
        threadLocal.set(id);
    }

    //获取用户ID
    public static Long getCurrentId() {
        return threadLocal.get();
    }
}
