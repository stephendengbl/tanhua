package com.itheima;

import cn.hutool.core.util.RandomUtil;
import com.itheima.domain.mongo.Topic;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TopicAdd {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void add01() {

            Topic topic = new Topic();
            topic.setId(new ObjectId("60933edd7568a234f4947071"));
            topic.setCharacterType(RandomUtil.randomInt(1,4)+"");
            topic.setDifficultToType("3");
            topic.setOption("你养了"+RandomUtil.randomInt(1,66)+"条鱼？");
            mongoTemplate.save(topic);

    }
}
