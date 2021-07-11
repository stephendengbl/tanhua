package com.itheima.autoconfig.sms;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

//短信配置读取
@Data
@ConfigurationProperties(prefix = "tanhua.sms")
public class SmsProperties {
    private String accessKey;
    private String secret;
    private String signName;
    private String templateCode;
}