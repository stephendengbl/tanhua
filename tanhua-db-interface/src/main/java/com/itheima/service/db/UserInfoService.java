package com.itheima.service.db;

import com.itheima.domain.db.UserInfo;
import com.itheima.vo.PageBeanVo;

public interface UserInfoService {

    //保存用户信息
    void save(UserInfo userInfo);

    //更新用户信息
    void update(UserInfo userInfo);

    //根据id查询
    UserInfo findById(Long id);

    //分页查询用户列表
    PageBeanVo findUserList(Integer pageNum, Integer pageSize);
}
