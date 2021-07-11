package com.itheima.test;

import com.itheima.autoconfig.huawei.HuaWeiUGCTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HuaWeiTest {

    @Autowired
    private HuaWeiUGCTemplate template;

    @Test
    public void testText() {
        boolean check = template.textContentCheck("你好");
        System.out.println(check);
    }

    @Test
    public void testImages() {
        String[] urls = new String[]{
                "http://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/logo/9.jpg",
                "http://tanhua-dev.oss-cn-zhangjiakou.aliyuncs.com/images/logo/10.jpg"
        };
        boolean check = template.imageContentCheck(urls);
        System.out.println(check);
    }
}