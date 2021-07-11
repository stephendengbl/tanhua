package com.itheima.service.db;

import com.itheima.domain.db.User;

public interface UserService {

    //保存用户,返回主键
    Long save(User user);

    //根据手机号查询用户
    User findByPhone(String phone);

    User findUserById(Long userId);
    void updatePhone(String phone,Long id);
}
