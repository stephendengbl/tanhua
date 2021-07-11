package com.itheima.web.interceptor;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.domain.db.Admin;
import com.itheima.util.ConstantUtil;
import com.itheima.util.JwtUtil;
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

    // 获取token，拦截登录用户
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 1、获取请求头的token,没有就返回401
            String token = request.getHeader("Authorization");
        token = token.replaceAll("Bearer ", "");// 前端工程师，有一个不成文的规定，在拼接token的时候前面会携带一个 Bearer（持有者）
        if (StrUtil.isEmpty(token)) {
            response.setStatus(401);
            return false;
        }

        //2. 从redis中查询,看看这个token是否在有效期
        if (stringRedisTemplate.hasKey(ConstantUtil.USER_TOKEN + token)) {
            response.setStatus(401);
            return false;
        }

        //3. 解析token,失败就401; 成功,1)将user信息存入th,2)token续期
        try {
            Map map = JwtUtil.parseToken(token);
            Admin admin = BeanUtil.mapToBean(map, Admin.class, true);

            //成功
            // 1)将user信息存入th
            AdminHolder.set(admin);

            // 2)token续期
            stringRedisTemplate.opsForValue().set(ConstantUtil.ADMIN_TOKEN + token, "1", Duration.ofMinutes(60));

            //放行
            return true;
        } catch (Exception e) {
            //解析token,失败就401;
            response.setStatus(401);
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        AdminHolder.remove();
    }
}