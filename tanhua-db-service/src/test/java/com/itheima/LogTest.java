package com.itheima;

import com.itheima.domain.db.Log;
import com.itheima.mapper.LogMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LogTest {

    @Autowired
    private LogMapper logMapper;

    @Test
    public void testInsertLoginLog() {

        String yesterday = "2021-04-28";//昨天
        String today = "2021-04-29";//今天

        //模拟昨日注册
        for (int i = 0; i < 10; i++) {
            Log log = new Log();
            log.setUserId((long) (i + 10));
            log.setLogTime(yesterday);
            log.setType("0102");
            logMapper.insert(log);
        }

        //模拟今日登录
        for (int i = 0; i < 5; i++) {
            Log log = new Log();
            log.setUserId((long) (i + 10));
            log.setLogTime(today);
            log.setType("0101");
            logMapper.insert(log);
        }

        //模拟今日注册
        for (int i = 0; i < 5; i++) {
            Log log = new Log();
            log.setUserId((long) (i + 20));
            log.setLogTime(today);
            log.setType("0102");
            logMapper.insert(log);
        }


        //模拟今日其他操作
        String[] types = new String[]{"0201", "0202", "0203", "0204", "0205", "0206", "0207"};
        for (int i = 0; i < 10; i++) {
            Log log = new Log();
            log.setUserId((long) (i + 1));
            log.setLogTime(today);
            int index = new Random().nextInt(7);
            log.setType(types[index]);
            logMapper.insert(log);
        }
    }
}