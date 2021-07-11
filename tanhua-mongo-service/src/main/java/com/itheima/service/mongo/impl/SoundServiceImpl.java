package com.itheima.service.mongo.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.itheima.domain.mongo.Sound;
import com.itheima.service.mongo.SoundService;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collections;
import java.util.List;

@Service
public class SoundServiceImpl implements SoundService {

    @Autowired
    private MongoTemplate mongoTemplate;

    //发送保存语音
    @Override
    public void save(Sound sound) {
        mongoTemplate.save(sound);
    }

    //接收查找语音
    @Override
    public Sound findByGender(String gender) {
        List<Sound> soundList = mongoTemplate.find(new Query(Criteria.where("gender").ne(gender)), Sound.class);

        Sound sound = null;
        if (CollectionUtil.isNotEmpty(soundList)) {
            Collections.shuffle(soundList);
            sound = soundList.get(0);
        } else {
            throw new RuntimeException("未查找到传音信息");
        }
        return sound;
    }


    //根据语音id删除
    @Override
    public void soundDeleteById(ObjectId id) {
        Query query = new Query(Criteria.where("id").is(id));
        this.mongoTemplate.remove(query,Sound.class);
    }
}
