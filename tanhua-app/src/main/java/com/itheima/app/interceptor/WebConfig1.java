package com.itheima.app.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
@Configuration
public class WebConfig1 implements WebMvcConfigurer {
    @Autowired
    private FreezeInterceptor freezeInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(freezeInterceptor).addPathPatterns("/**").excludePathPatterns("/user/login","/user/loginVerification");
    }
}
