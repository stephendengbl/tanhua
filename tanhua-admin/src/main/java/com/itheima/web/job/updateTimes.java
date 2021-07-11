package com.itheima.web.job;

import com.itheima.service.mongo.SoundTimeServic;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class updateTimes {

    @Reference
    private SoundTimeServic soundTimeServic;

    //更新获取语音次数
    @Scheduled(cron = "0 0 0 0/1 * ?")
    public void updateTimes(){
        System.out.println("更新次数");
        soundTimeServic.updateTimes(10);
    }
}
