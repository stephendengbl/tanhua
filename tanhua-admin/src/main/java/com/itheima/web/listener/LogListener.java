package com.itheima.web.listener;

import com.itheima.domain.db.Log;
import com.itheima.service.db.LogService;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = "consumer", topic = "tanhua-log")
public class LogListener implements RocketMQListener<Log> {

    @Reference
    private LogService logService;

    @Override
    public void onMessage(Log log) {
        //调用service保存log
        logService.save(log);
    }
}
