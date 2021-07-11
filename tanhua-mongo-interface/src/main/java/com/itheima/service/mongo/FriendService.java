package com.itheima.service.mongo;

import com.itheima.vo.PageBeanVo;

public interface FriendService {
    //保存好友
    void save(Long userId, Long friendId);

    //查询联系人
    PageBeanVo findByUserId(Long userId, Integer pageNum, Integer pageSize);

    //删除好友
    void delete(Long userId, Long friendId);
}
