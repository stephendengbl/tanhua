package com.itheima.service.db;

import com.itheima.vo.PageBeanVo;

public interface BlackListService {

    //根据登录用户id分页查询其黑名单用户列表
    PageBeanVo findBlackUserByUserId(Long userId, Integer pageNum, Integer pageSize);

    //根据登录用户id和黑名单用户id删除
    void deleteBlackUser(Long userId, Integer blackUserId);
}
