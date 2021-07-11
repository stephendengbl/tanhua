package com.itheima;

import com.itheima.autoconfig.oss.OssTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class OssTest {

    @Autowired
    private OssTemplate ossTemplate;

    @Test
    public void testUpload() throws FileNotFoundException {
        System.out.println(ossTemplate.upload("1.jpg", new FileInputStream("C:/upload/1.jpg")));
    }
}
