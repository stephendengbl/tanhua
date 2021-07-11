package com.itheima.job;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.itheima.domain.mongo.Movement;
import com.itheima.domain.mongo.RecommendMovement;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

//任务: 将redis中推荐数据转移到mongo中
@Component
public class MovementJob {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MongoTemplate mongoTemplate;


    //@Scheduled(cron = "0 0/30 * * * ?")
    @Scheduled(cron = "0 0/1 * * * ?")
    public void recommend(){
        System.out.println("推荐系统定时任务开始执行");

        //1. 找到所有的推荐动态的键
        Set<String> keys = stringRedisTemplate.keys("QUANZI_PUBLISH_RECOMMEND_*");

        //自己选择是否要删除redis和mongo中的数据

        //2. 遍历集合,得到推荐的键
        if (CollectionUtil.isNotEmpty(keys)){
            for (String key : keys) {
                Long userId = Long.parseLong(key.replaceAll("QUANZI_PUBLISH_RECOMMEND_", ""));//推荐给谁
                String s = stringRedisTemplate.opsForValue().get(key);//10092,1,2,6,5,3
                String[] pidArr = s.split(",");
                for (String pid : pidArr) {
                    //3 根据pid获取动态id
                    ObjectId movementId = mongoTemplate.findOne(new Query(Criteria.where("pid").is(Long.parseLong(pid))), Movement.class).getId();
                    double score = RandomUtil.randomDouble(80, 100);//模拟分数


                    //4 组装一个recomment_movement对象
                    RecommendMovement recommendMovement = new RecommendMovement();
                    recommendMovement.setPid(Long.parseLong(pid));
                    recommendMovement.setPublishId(movementId);
                    recommendMovement.setScore(score);
                    recommendMovement.setUserId(userId);
                    recommendMovement.setCreated(System.currentTimeMillis());

                    mongoTemplate.save(recommendMovement);
                }
            }
        }


    }
}
