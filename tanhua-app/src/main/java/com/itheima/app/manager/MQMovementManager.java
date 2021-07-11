package com.itheima.app.manager;

import com.itheima.app.interceptor.UserHolder;
import com.itheima.service.mongo.MovementService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

//负责向mq发送消息
@Service
public class MQMovementManager {
    //针对动态的操作
    public static final Integer MOVEMENT_PUBLISH = 1;// 发动态
    public static final Integer MOVEMENT_BROWSE = 2;// 浏览动态
    public static final Integer MOVEMENT_LIKE = 3;// 点赞
    public static final Integer MOVEMENT_LOVE = 4;// 喜欢
    public static final Integer MOVEMENT_COMMENT = 5;// 评论
    public static final Integer MOVEMENT_DISLIKE = 6;// 取消点赞
    public static final Integer MOVEMENT_DISLOVE = 7;// 取消喜欢


    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Reference
    private MovementService movementService;


    //发送消息(某个用户对哪条动态进行了哪种操作)
    public void sendMsg(String movementId, Integer type) {
        Map<String, String> map = new HashMap<>();
        map.put("userId", UserHolder.get().getId() + "");//某个用户
        map.put("pid", movementService.findMovementById(movementId).getPid() + "");//对哪条动态
        map.put("type", type + "");//哪种操作

        rocketMQTemplate.convertAndSend("recommend-movement", map);
    }

}