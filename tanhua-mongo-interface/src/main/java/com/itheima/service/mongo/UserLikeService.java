package com.itheima.service.mongo;

import com.itheima.vo.PageBeanVo;
import com.itheima.vo.UserLikeCountVo;

public interface UserLikeService {

    //保存喜欢关系
    void save(Long userId, Long likeUserId);

    //查询二人是否互相喜欢
    boolean isMutualLike(Long userId, Long likeUserId);

    //删除喜欢关系(我不在喜欢她)
    void delete(Long userId, Long likeUserId);

    //统计用户数量
    UserLikeCountVo findUserCount(Long userId);

    //分页查询用户列表
    PageBeanVo findUserList(Long userId, Integer type, Integer pageNum, Integer pageSize);
}
