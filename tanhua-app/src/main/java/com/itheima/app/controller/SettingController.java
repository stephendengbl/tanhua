package com.itheima.app.controller;

import com.itheima.app.manager.SettingManager;
import com.itheima.domain.db.Notification;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.SettingVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class SettingController {

    @Autowired
    private SettingManager settingManager;

    //查询通用设置
    @GetMapping("/users/settings")
    public SettingVo findUserSetting() {
        return settingManager.findUserSetting();
    }

    //保存或者更新陌生人问题
    @PostMapping("/users/questions")
    public void saveOrUpdateQuestions(@RequestBody Map<String, String> map) {
        //1. 接收参数
        String content = map.get("content");

        //2. 调用manager
        settingManager.saveOrUpdateQuestions(content);
    }

    //保存或者更新推送设置
    @PostMapping("/users/notifications/setting")
    public void saveOrUpdateNotifications(@RequestBody Notification notificationParam) {
        //1. 调用manager
        settingManager.saveOrUpdateNotifications(notificationParam);
    }

    //黑名单列表查询
    @GetMapping("/users/blacklist")
    public PageBeanVo findBlackList(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {

        return settingManager.findBlackList(pageNum, pageSize);
    }

    //删除黑名单用户
    @DeleteMapping("/users/blacklist/{uid}")
    public void deleteBlackUser(@PathVariable("uid") Integer blackUserId){
        settingManager.deleteBlackUser(blackUserId);
    }

}
