package com.itheima.service.mongo;

import com.itheima.domain.mongo.Movement;
import com.itheima.vo.PageBeanVo;

public interface MovementService {

    //保存动态
    void saveMovement(Movement movement);

    //查询我的动态
    PageBeanVo findMyMovement(Long userId, Integer pageNum, Integer pageSize);

    //查询我的好友的动态
    PageBeanVo findMyFriendMovement(Long userId, Integer pageNum, Integer pageSize);

    //查询推荐给我的动态
    PageBeanVo findRecommendMovement(Long userId, Integer pageNum, Integer pageSize);

    //根据id查询动态信息
    Movement findMovementById(String movementId);

    //获取动态列表
    PageBeanVo findMovementList(Long uid, Integer stateInt, Integer pageNum, Integer pageSize);

    //更新动态的审核状态
    void updateMovementState(Movement movement);
}
