package com.itheima.listenter;

import com.itheima.domain.mongo.MovementScore;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RocketMQMessageListener(topic = "recommend-movement",consumerGroup = "movementConsumer")
public class MovementScoreListener implements RocketMQListener<Map> {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void onMessage(Map map) {
        //1. 从map中获取参数
        Long userId = Long.parseLong((String)map.get("userId"));
        Long pid = Long.parseLong((String)map.get("pid"));
        Integer type = Integer.parseInt((String) map.get("type"));

        //2. 组装MovementScore对象
        MovementScore movementScore = new MovementScore();
        movementScore.setUserId(userId);
        movementScore.setPublishId(pid);
        movementScore.setDate(System.currentTimeMillis());
        switch (type) {
            case 1: {
                movementScore.setScore(20d);
                break;
            }
            case 2: {
                movementScore.setScore(1d);
                break;
            }
            case 3: {
                movementScore.setScore(5d);
                break;
            }
            case 4: {
                movementScore.setScore(8d);
                break;
            }
            case 5: {
                movementScore.setScore(10d);
                break;
            }
            case 6: {
                movementScore.setScore(-5d);
                break;
            }
            case 7: {
                movementScore.setScore(-8d);
                break;
            }
        }

        //3. 保存movementScore到mongo
        mongoTemplate.save(movementScore);

    }
}
