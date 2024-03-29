package com.itheima.autoconfig.huawei;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "tanhua.huawei")
@Data
public class HuaWeiUGCProperties {
    private String username;
    private String password;
    private String project;
    private String domain;
    private String cagegoriesText;
    private String cagegoriesImage;
}