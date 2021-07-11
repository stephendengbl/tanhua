package com.itheima.web.controller;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.bean.BeanUtil;
import com.itheima.domain.db.Admin;
import com.itheima.util.JwtUtil;
import com.itheima.web.interceptor.AdminHolder;
import com.itheima.web.manager.AdminManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
public class AdminController {

    @Autowired
    private AdminManager adminManager;

    //生成验证码
    @GetMapping("/system/users/verification")
    public void genVerification(String uuid, HttpServletResponse response) throws IOException {
        //1. 调用manager生成验证码
        LineCaptcha lineCaptcha = adminManager.genVerification(uuid);

        //2. 返回验证码
        response.setContentType("image/jpeg");//声明返回的数据的Content-Type
        lineCaptcha.write(response.getOutputStream());
    }


    //用户登录
    @PostMapping("/system/users/login")
    public Map<String, String> login(@RequestBody Map<String, String> map) {
        //1. 接收参数
        String username = map.get("username");
        String password = map.get("password");
        String verificationCode = map.get("verificationCode");
        String uuid = map.get("uuid");

        //2. 调用manager登录,返回token
        String token = adminManager.login(username, password, verificationCode, uuid);

        //3. 组装返回map
        Map<String, String> tokenMap = new HashMap<>();
        tokenMap.put("token", token);
        return tokenMap;
    }


    //获取用户基本信息
    @PostMapping("/system/users/profile")
    public Map<String, String> findUserInfo(@RequestHeader("Authorization") String token) {
        //1. 从token中获取用户Admin
        Admin admin = AdminHolder.getAdmin();
        //2. 查询头像
        admin = adminManager.findByUsername(admin.getUsername());
        //3. 根据admin封装返回map
        HashMap<String, String> map = new HashMap<>();
        map.put("uid", admin.getId() + "");
        map.put("username", admin.getUsername());
        map.put("avatar", admin.getAvatar());
        return map;
    }

    //用户退出
    @PostMapping("/system/users/logout")
    public void logout(@RequestHeader("Authorization") String token){
        //获取到了token
        token = token.replaceAll("Bearer ", "");

        //调用manager退出
        adminManager.logout(token);
    }

}
