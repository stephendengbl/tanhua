package com.itheima;

import com.itheima.autoconfig.sms.SmsTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SmsTest {

    @Autowired
    private SmsTemplate smsTemplate;

    @Test
    public void testSendSms(){
        //smsTemplate.sendSms("18611622426","123456");
    }
}
