package com.itheima;

import cn.hutool.core.io.FileUtil;
import com.itheima.autoconfig.face.AipFaceTemplate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class FaceTest {

    @Autowired
    private AipFaceTemplate template;

    @Test
    public void testAip() {
//        String filename = "c:/upload/2.jpg";
//        File file = new File(filename);
//        byte[] bytes = FileUtil.readBytes(file);
//        System.out.println(template.detect(bytes));
    }
}