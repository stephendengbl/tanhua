package com.itheima;

import com.itheima.domain.mongo.TopicQuestionnaire;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class TopicQueAdd {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void test() {

        TopicQuestionnaire topicQuestionnaire2 = new TopicQuestionnaire();
        topicQuestionnaire2.setId(new ObjectId("60933edd7568a234f494706f"));
        topicQuestionnaire2.setLevel("3");
        topicQuestionnaire2.setCover(":)");
        topicQuestionnaire2.setStar(3);
        topicQuestionnaire2.setName("高级灵魂题");
        mongoTemplate.save(topicQuestionnaire2);
        TopicQuestionnaire topicQuestionnaire3 = new TopicQuestionnaire();
        topicQuestionnaire3.setId(new ObjectId("60933edd7568a234f4947070"));
        topicQuestionnaire3.setLevel("3");
        topicQuestionnaire3.setCover(":)");
        topicQuestionnaire3.setStar(3);
        topicQuestionnaire3.setName("高级灵魂题");
        mongoTemplate.save(topicQuestionnaire3);
        TopicQuestionnaire topicQuestionnaire = new TopicQuestionnaire();
        topicQuestionnaire.setId(new ObjectId("60933edd7568a234f4947071"));
        topicQuestionnaire.setLevel("3");
        topicQuestionnaire.setCover(":)");
        topicQuestionnaire.setStar(3);
        topicQuestionnaire.setName("高级灵魂题");
        mongoTemplate.save(topicQuestionnaire);




    }

}
