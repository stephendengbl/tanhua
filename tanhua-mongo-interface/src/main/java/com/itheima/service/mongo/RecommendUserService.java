package com.itheima.service.mongo;

import com.itheima.domain.mongo.RecommendUser;
import com.itheima.vo.PageBeanVo;

public interface RecommendUserService {
    //根据登录用户id查询缘分值最高一个用户
    RecommendUser findMaxScore(Long userId);

    //根据登录用户id分页查询推荐用户列表(去掉缘分值最高的一个)
    PageBeanVo findRecommendUser(Long userId, Integer pageNum, Integer pageSize);

    //根据推荐人和被推荐人id查询
    RecommendUser findRecommendUser(Long userId, Long recommendUserId);
}
