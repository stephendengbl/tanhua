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
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class FreezeInterceptor implements HandlerInterceptor {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {


        String token = request.getHeader("Authorization");
        Map map = JwtUtil.parseToken(token);
        User user = BeanUtil.mapToBean(map, User.class, true);
        UserHolder.set(user);
        String freezeTime1 = stringRedisTemplate.opsForValue().get("freezeDengLu"+user.getId());
        if (!StringUtils.isEmpty(freezeTime1)) {
            stringRedisTemplate.delete(ConstantUtil.USER_TOKEN + token);
            response.setStatus(401);
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
