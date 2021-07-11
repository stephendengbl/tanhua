package com.itheima.service.db;

import com.itheima.domain.db.Notification;

public interface NotificationService {
    //根据用户id查询
    Notification findByUserId(Long userId);

    //保存
    void save(Notification notification);

    //更新
    void update(Notification notification);
}
