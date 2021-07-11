package com.itheima.app.controller;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.app.manager.UserManager;
import com.itheima.domain.db.User;
import com.itheima.domain.db.UserInfo;
import com.itheima.util.JwtUtil;
import com.itheima.vo.UserInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserManager userManager;

    //保存用户,返回主键
    @PostMapping("/save")
    public Long save(@RequestBody User user) {
        return userManager.save(user);
    }

    //根据手机号查询用户
    @GetMapping("/findByPhone")
    public User findByPhone(String phone) {
        return userManager.findByPhone(phone);
    }

    //发送手机验证码
    @PostMapping("/user/login")
    public void sendSmsCode(@RequestBody Map<String, String> map) {
        //1. 接收参数
        String phone = map.get("phone");

        //2. 调用manager
        userManager.sendSmsCode(phone);
    }

    //登录注册
    @PostMapping("/user/loginVerification")
    public Map<String, Object> regAndLogin(@RequestBody Map<String, String> map) {
        //1. 接收参数

        String phone = map.get("phone");
        String verificationCode = map.get("verificationCode");

        //2. 调用manager
        return userManager.regAndLogin(phone, verificationCode);
    }

    //完善个人基本信息
    @PostMapping("/user/loginReginfo")
    public void saveUserBaseInfo(@RequestBody UserInfo userInfo) {
        //1. 从token中获取到一个user对象
        User user = UserHolder.get();

        //2.设置userid到userinfo对象中
        userInfo.setId(user.getId());

        //3. 调用manager
        userManager.saveUserBaseInfo(userInfo);
    }

    //完善个人头像信息
    @PostMapping({"/user/loginReginfo/head", "/users/header"})
    public void saveUserHeadInfo(MultipartFile headPhoto) throws IOException {
        //1. 从token中获取到一个user对象
        User user = UserHolder.get();

        //2. 调用manager
        userManager.saveUserHeadInfo(user.getId(), headPhoto);
    }

    //查询个人信息
    @GetMapping("/users")
    public UserInfoVo findUserInfo(Long userID, Long huanxinID) {
        //1. 查询
        if (userID != null) {
            return userManager.findById(userID);
        } else if (huanxinID != null) {
            return userManager.findById(huanxinID);
        } else {
            User user = UserHolder.get();
            return userManager.findById(user.getId());
        }
    }

    //更新个人信息
    @PutMapping("/users")
    public void updateUserBaseInfo(@RequestBody UserInfo userInfo) {
        //调用manager更新
        userManager.updateUserBaseInfo(userInfo);
    }
    //发送验证码（修改手机号）
    @PostMapping("/users/phone/sendVerificationCode")
    public void SendSms() {
        Long userId = UserHolder.get().getId();
        User user=userManager.findUserById(userId);
        userManager.sendSmsCode(user.getPhone());
    }
    //修改电话校验验证码
    @PostMapping("/users/phone/checkVerificationCode")
    public Map<String, Boolean> checkCode(@RequestBody Map<String,String> map) {
        String code = map.get("verificationCode");
        return  userManager.checkCode(code);
    }

    //保存新的电话号
    @PostMapping("/users/phone")
    public void updatePhone(@RequestBody Map<String, String> map) {
        String phone = map.get("phone");
        userManager.updatePhone(phone);
    }
}
