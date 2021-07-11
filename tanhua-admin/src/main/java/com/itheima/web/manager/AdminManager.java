package com.itheima.web.manager;

import cn.hutool.captcha.LineCaptcha;
import cn.hutool.crypto.SecureUtil;
import com.itheima.domain.db.Admin;
import com.itheima.service.db.AdminService;
import com.itheima.util.ConstantUtil;
import com.itheima.util.JwtUtil;
import com.itheima.web.exception.BusinessException;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
public class AdminManager {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Reference
    private AdminService adminService;

    //生成验证码
    public LineCaptcha genVerification(String uuid) {
        //1. 生成验证码
        LineCaptcha lineCaptcha = new LineCaptcha(300, 100);
        String code = lineCaptcha.getCode();//获取到验证码
        System.out.println(code);

        //2. 将验证码存入到redis(设置有效期)
        stringRedisTemplate.opsForValue().set(ConstantUtil.ADMIN_CODE + uuid, code, Duration.ofMinutes(5));

        //3. 返回验证码
        return lineCaptcha;
    }

    //用户登录
    public String login(String username, String password, String verificationCode, String uuid) {
        //1. 比对验证码,如果失败,直接返回
        String codeFromRedis = stringRedisTemplate.opsForValue().get(ConstantUtil.ADMIN_CODE + uuid);
        if (!StringUtils.equals(verificationCode, codeFromRedis)) {
            throw new BusinessException("验证码错误");
        }

        //2. 根据用户名查询账户,如果失败,直接返回
        Admin admin = adminService.findByUsername(username);
        if (admin == null) {
            throw new BusinessException("当前用户名不存在");
        }

        //3. 比对密码,如果失败,直接返回
        String passwordWithmd5 = SecureUtil.md5(password);//先对前端传入的密码使用md5加密
        if (!StringUtils.equals(passwordWithmd5, admin.getPassword())) {
            throw new BusinessException("密码错误");
        }

        //4. 用户登录成功
        //4-1 删除redis中验证码
        stringRedisTemplate.delete(ConstantUtil.ADMIN_CODE + uuid);

        //4-2 创建token
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", admin.getId());
        tokenMap.put("username", admin.getUsername());
        String token = JwtUtil.createToken(tokenMap);

        //4-3 将token存入redis(有效期)
        stringRedisTemplate.opsForValue().set(ConstantUtil.ADMIN_TOKEN + token, "1", Duration.ofHours(1));

        //4-4 返回token
        return token;
    }

    //根据主键查询账户信息
    public Admin findByUsername(String username) {
        return adminService.findByUsername(username);
    }

    //用户退出
    public void logout(String token) {
        stringRedisTemplate.delete(ConstantUtil.ADMIN_TOKEN + token);
    }
}
