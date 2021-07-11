package com.itheima.web.interceptor;

import com.itheima.domain.db.Admin;

// 线程绑定工具类
public class AdminHolder {

    private static final ThreadLocal<Admin> adminTL = new ThreadLocal<>();

    // 向线程绑定admin
    public static void set(Admin admin) {
        adminTL.set(admin);
    }

    // 从线程获取admin
    public static Admin getAdmin() {
        return adminTL.get();
    }

    // 移出线程绑定
    public static void remove() {
        adminTL.remove();
    }
}