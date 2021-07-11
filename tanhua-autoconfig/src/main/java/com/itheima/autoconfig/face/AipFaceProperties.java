package com.itheima.autoconfig.face;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//读取百度云人脸识别配置
@Data
@ConfigurationProperties("tanhua.aip")
public class AipFaceProperties {
    private String appId;
    private String apiKey;
    private String secretKey;
}