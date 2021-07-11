package com.itheima;

import com.itheima.autoconfig.face.AipFaceProperties;
import com.itheima.autoconfig.face.AipFaceTemplate;
import com.itheima.autoconfig.huanxin.HuanXinProperties;
import com.itheima.autoconfig.huanxin.HuanXinTemplate;
import com.itheima.autoconfig.huawei.HuaWeiUGCProperties;
import com.itheima.autoconfig.huawei.HuaWeiUGCTemplate;
import com.itheima.autoconfig.oss.OssProperties;
import com.itheima.autoconfig.oss.OssTemplate;
import com.itheima.autoconfig.sms.SmsProperties;
import com.itheima.autoconfig.sms.SmsTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({
        SmsProperties.class,//加载短信配置
        OssProperties.class,//加载阿里文件配置
        AipFaceProperties.class,//加载百度人脸识别配置
        HuanXinProperties.class,//加载环信配置
        HuaWeiUGCProperties.class,//加载华为审核配置
})
public class TanhuaAutoConfiguration {

    @Bean
    public HuaWeiUGCTemplate huaWeiUGCTemplate(HuaWeiUGCProperties huaWeiUGCProperties) {
        return new HuaWeiUGCTemplate(huaWeiUGCProperties);
    }

    @Bean
    public HuanXinTemplate huanXinTemplate(HuanXinProperties huanXinProperties) {
        return new HuanXinTemplate(huanXinProperties);
    }

    @Bean
    public SmsTemplate smsTemplate(SmsProperties smsProperties) {
        return new SmsTemplate(smsProperties);
    }

    @Bean
    public OssTemplate ossTemplate(OssProperties ossProperties) {
        return new OssTemplate(ossProperties);
    }

    @Bean
    public AipFaceTemplate aipFaceTemplate(AipFaceProperties aipFaceProperties) {
        return new AipFaceTemplate(aipFaceProperties);
    }
}