package com.itheima.app.manager;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.domain.db.Notification;
import com.itheima.domain.db.Question;
import com.itheima.domain.db.User;
import com.itheima.service.db.BlackListService;
import com.itheima.service.db.NotificationService;
import com.itheima.service.db.QuestionService;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.SettingVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

@Service
public class SettingManager {

    @Reference
    private QuestionService questionService;

    @Reference
    private NotificationService notificationService;

    @Reference
    private BlackListService blackListService;

    //查询通用设置
    public SettingVo findUserSetting() {
        //1. 从th获取用户信息
        User user = UserHolder.get();

        //2. 根据userId查询陌生人问题
        Question question = questionService.findByUserId(user.getId());

        //3. 根据userId查询推送设置
        Notification notification = notificationService.findByUserId(user.getId());

        //4.组装返回结果
        SettingVo settingVo = new SettingVo();
        //4-1 设置用户信息
        settingVo.setId(user.getId());
        settingVo.setPhone(user.getPhone());
        //4-2 设置陌生人问题
        if (question == null) {
            settingVo.setStrangerQuestion("你是喜欢天空的广阔,还是喜欢大海的波澜~~~");
        } else {
            settingVo.setStrangerQuestion(question.getStrangerQuestion());
        }
        //4-3 设置推送
        if (notification != null) {
            BeanUtil.copyProperties(notification, settingVo);

        }

        return settingVo;
    }

    //保存或者更新陌生人问题
    public void saveOrUpdateQuestions(String content) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 根据登录用户id查询其陌生人问题
        Question question = questionService.findByUserId(userId);

        //3. 处理
        if (question == null) {
            //3-1  查不到,添加
            question = new Question();
            question.setUserId(userId);
            question.setStrangerQuestion(content);

            questionService.save(question);

        } else {
            //3-2  查到,更新
            question.setStrangerQuestion(content);

            questionService.update(question);
        }
    }

    //保存或者更新推送设置
    public void saveOrUpdateNotifications(Notification notificationParam) {
        //1.获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 根据userId查询
        Notification notification = notificationService.findByUserId(userId);

        //3. 处理
        if (notification == null) {
            //3-1 不存在,新增
            //设置userId
            notificationParam.setUserId(userId);
            //保存
            notificationService.save(notificationParam);
        } else {
            //3-2 存在,更新
            //设置id
            notificationParam.setId(notification.getId());
            //更新
            notificationService.update(notificationParam);
        }
    }

    //黑名单列表查询
    public PageBeanVo findBlackList(Integer pageNum, Integer pageSize) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 根据id分页查询
        return blackListService.findBlackUserByUserId(userId, pageNum, pageSize);
    }

    //删除黑名单用户
    public void deleteBlackUser(Integer blackUserId) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用service删除
        blackListService.deleteBlackUser(userId, blackUserId);

    }
}