package com.itheima;

import com.itheima.domain.mongo.TopicDetails;
import com.itheima.domain.mongo.TopicScore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class ScoreTest {
    @Autowired
    private MongoTemplate mongoTemplate;
    @Test
    public  void scoreTest() {
        TopicDetails t = new TopicDetails();

    }
}
