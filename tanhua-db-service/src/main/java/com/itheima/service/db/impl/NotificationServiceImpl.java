package com.itheima.service.db.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.domain.db.Notification;
import com.itheima.mapper.NotificationMapper;
import com.itheima.service.db.NotificationService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationMapper notificationMapper;

    @Override
    public Notification findByUserId(Long userId) {
        QueryWrapper<Notification> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        return notificationMapper.selectOne(wrapper);
    }

    @Override
    public void save(Notification notification) {
        notificationMapper.insert(notification);
    }

    @Override
    public void update(Notification notification) {
        notificationMapper.updateById(notification);
    }
}
