package com.itheima.service.mongo;

import com.itheima.domain.mongo.Sound;
import org.bson.types.ObjectId;

public interface SoundService {
    //保存语音消息
    void save(Sound sound);

    //查找语音消息
    Sound findByGender(String gender);

    //根据语音id删除
    void soundDeleteById(ObjectId id);
}
