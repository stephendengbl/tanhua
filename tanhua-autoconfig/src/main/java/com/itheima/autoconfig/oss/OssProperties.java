package com.itheima.autoconfig.oss;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

//读取阿里存储设置
@Data
@ConfigurationProperties(prefix = "tanhua.oss")
public class OssProperties {

    private String accessKey;
    private String secret;
    private String bucketName;
    private String url;
    private String Endpoint;
}