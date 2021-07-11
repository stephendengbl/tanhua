package com.itheima.app.interceptor;

import cn.hutool.core.bean.BeanUtil;
import com.itheima.domain.db.User;
import com.itheima.util.ConstantUtil;
import com.itheima.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.Map;

@Component
public class TokenInterceptor implements HandlerInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    //进入controller之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //1. 判断请求头中是否有token,没有就返回401
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            response.setStatus(401);
            return false;
        }

        //2. 从redis中查询,看看这个token是否在有效期
        String s = stringRedisTemplate.opsForValue().get(ConstantUtil.USER_TOKEN + token);
        if (StringUtils.isEmpty(s)) {
            response.setStatus(401);
            return false;
        }

        //3. 解析token,失败就401; 成功,1)将user信息存入th,2)token续期
        try {
            Map map = JwtUtil.parseToken(token);
            User user = BeanUtil.mapToBean(map, User.class, true);
            //成功
            // 1)将user信息存入th
            UserHolder.set(user);
            // 2)token续期
            stringRedisTemplate.opsForValue().set(ConstantUtil.USER_TOKEN + token, "1", Duration.ofDays(30));

            //放行
            return true;
        } catch (Exception e) {
            //解析token,失败就401;
            response.setStatus(401);
            return false;
        }
    }


    //页面渲染完毕之后
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.remove();
    }
}


