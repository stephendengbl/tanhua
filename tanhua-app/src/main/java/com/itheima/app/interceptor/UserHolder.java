package com.itheima.app.interceptor;

import com.itheima.domain.db.User;

//操作threadLocal的工具类
public class UserHolder {

    //存储user
    private static ThreadLocal<User> th = new ThreadLocal<User>();

    //向th放入user
    public static void set(User user) {
        th.set(user);
    }

    //从th获取user
    public static User get() {
        return th.get();
    }

    //删除th中user
    public static void remove() {
        th.remove();
    }
}
