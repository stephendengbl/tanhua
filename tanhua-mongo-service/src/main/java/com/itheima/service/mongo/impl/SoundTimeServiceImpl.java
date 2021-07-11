package com.itheima.service.mongo.impl;

import com.itheima.domain.mongo.SoundTime;
import com.itheima.service.mongo.SoundTimeServic;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Service
public class SoundTimeServiceImpl implements SoundTimeServic {

    @Autowired
    private MongoTemplate mongoTemplate;

    //保存更新用户剩余次数
    @Override
    public void save(SoundTime soundTime) {
        mongoTemplate.save(soundTime);
    }


    //根据id查询用户剩余的次数
    @Override
    public SoundTime findSoundTime(Long userId) {
        Query query=new Query(Criteria.where("userId").is(userId));
        SoundTime soundTime = mongoTemplate.findOne(query, SoundTime.class);
        return soundTime;
    }


    //更新语音获取次数
    @Override
    public void updateTimes(int count) {
        Update update=new Update();
        update.set("soundTime",count);
        mongoTemplate.updateMulti(new Query(),update,SoundTime.class);
    }
}
