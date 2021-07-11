package com.itheima.web.manager;


import cn.hutool.core.collection.CollectionUtil;
import com.itheima.domain.mongo.Movement;
import com.itheima.service.mongo.MovementService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovementReviewManager {

    @Reference
    private MovementService movementService;

    //消息通过
    public String messageVia(List<String> item) {

        //判断
        if (CollectionUtil.isNotEmpty(item)) {
            for (String id : item) {
                //根据id查询对应的动态
                Movement movement = movementService.findMovementById(id);

                //更新动态
                movement.setState(1);
                movementService.updateMovementState(movement);
            }
        }

        return "审核通过";
    }

    public String messageRefuse(List<String> item) {

        //判断
        if (CollectionUtil.isNotEmpty(item)) {
            for (String id : item) {
                //根据id查询对应的动态
                Movement movement = movementService.findMovementById(id);

                //更新动态
                movement.setState(2);
                movementService.updateMovementState(movement);
            }
        }

        return "网络繁忙";
    }
}
