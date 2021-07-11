package com.itheima.web.listener;

import com.itheima.autoconfig.huawei.HuaWeiUGCTemplate;
import com.itheima.domain.mongo.Movement;
import com.itheima.service.mongo.MovementService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RocketMQMessageListener(topic = "tanhua-movement", consumerGroup = "consumerGroup")
public class MovementCheckListener implements RocketMQListener<String> {

    @Reference
    private MovementService movementService;

    @Autowired
    private HuaWeiUGCTemplate huaWeiUGCTemplate;

    @Override
    public void onMessage(String movementId) {

        //1. 根据动态id查询动态详情
        Movement movement = movementService.findMovementById(movementId);

        if (movement != null && movement.getState() != 1) {
            //2. 调用华为云分别审核图片和文字
            //2-1 审核文字
            String textContent = movement.getTextContent();
            boolean textContentCheckResult = huaWeiUGCTemplate.textContentCheck(textContent);

            //2-2 审核图片
            List<String> medias = movement.getMedias();
            boolean imageContentCheckResult = huaWeiUGCTemplate.imageContentCheck(medias.toArray(new String[]{}));

            //3. 设置审核结果
            if (textContentCheckResult && imageContentCheckResult) {
                //更新动态的审核状态为1
                movement.setState(1);
            } else {
                //更新动态的审核状态为2
                movement.setState(2);
            }

            System.out.println("动态状态审核完毕,结果为:" + movement.getState());

            movementService.updateMovementState(movement);
        }
    }
}
