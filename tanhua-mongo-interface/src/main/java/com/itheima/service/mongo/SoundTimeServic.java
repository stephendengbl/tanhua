package com.itheima.service.mongo;

import com.itheima.domain.mongo.SoundTime;

public interface SoundTimeServic {

    //保存更新用户剩余次数
    void save(SoundTime soundTime);

    //根据id查询用户剩余的次数
    SoundTime findSoundTime(Long userId);


    //更新语音获取次数
    void updateTimes(int count);
}
