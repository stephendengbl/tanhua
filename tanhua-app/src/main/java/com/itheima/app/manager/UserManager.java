package com.itheima.app.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.SecureUtil;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.autoconfig.face.AipFaceTemplate;
import com.itheima.autoconfig.huanxin.HuanXinTemplate;
import com.itheima.autoconfig.oss.OssTemplate;
import com.itheima.autoconfig.sms.SmsTemplate;
import com.itheima.domain.db.Log;
import com.itheima.domain.db.User;
import com.itheima.domain.db.UserInfo;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.db.UserService;
import com.itheima.util.ConstantUtil;
import com.itheima.util.JwtUtil;
import com.itheima.vo.UserInfoVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

//spring的
@Service
public class UserManager {
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private SmsTemplate smsTemplate;

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private AipFaceTemplate aipFaceTemplate;

    @Autowired
    private OssTemplate ossTemplate;

    //dubbo的
    @Reference
    private UserService userService;

    @Reference
    private UserInfoService userInfoService;

    //保存用户,返回主键
    public Long save(User user) {
        //密码加密
        String md5Password = SecureUtil.md5(user.getPassword());
        user.setPassword(md5Password);

        return userService.save(user);
    }

    //根据手机号查询用户
    public User findByPhone(String phone) {
        return userService.findByPhone(phone);
    }

    //发送手机验证码
    public void sendSmsCode(String phone) {
        //1. 生成验证码
        //String smsCode = RandomUtil.randomNumbers(6);
        String smsCode = "123456";

        //2. 发送验证码
//        smsTemplate.sendSms(phone, smsCode);

        //3. 保存验证码(设置有效时间)
        stringRedisTemplate.opsForValue().set(ConstantUtil.SMS_CODE + phone, smsCode, Duration.ofMinutes(5));
    }

    //登录注册
    public Map<String, Object> regAndLogin(String phone, String verificationCode) {
        //1. 校验验证码
        String codeFromRedis = stringRedisTemplate.opsForValue().get(ConstantUtil.SMS_CODE + phone);
        if (!StringUtils.equals(codeFromRedis, verificationCode)) {
            //失败,返回错误
            throw new RuntimeException("验证码错误");
        }
        //成功,删除redis中验证码
        stringRedisTemplate.delete(ConstantUtil.SMS_CODE + phone);

        //2. 根据手机号调用service查询用户信息
        User user = userService.findByPhone(phone);

        boolean isNew;
        String type = "";
        if (user != null) {
            type = "0101";
            //查到了, 用户登录成功
            isNew = false;
        } else {
            type = "0102";
            //没查到,用户进行注册
            user = new User();

            //设置初始化密码,进行加密
            user.setPhone(phone);
            user.setPassword(SecureUtil.md5(ConstantUtil.INIT_PASSWORD));

            //调用service保存,并设置id
            Long userId = userService.save(user);
            user.setId(userId);

            isNew = true;

            //向环信中也去注册一个新的账号
            huanXinTemplate.register(userId);
        }

        //组装一个log对象
        Log log = new Log();
        log.setUserId(user.getId());
        log.setType(type);
        log.setLogTime(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        log.setPlace("北京顺义");
        log.setEquipment("华为");

        //向mq投递日志消息
        rocketMQTemplate.convertAndSend("tanhua-log", log);


        //token续期: 延长你手中token有效期限
        //方案1: 原始: 每次访问的时候,都要重新生成token,然后返回给app(访问校验拦截器); 需要前端提供一个更新token的接口方法
        //方案2: redis: 登录成功之后,向redis中记录你的token,并设置有效时间; 每次访问的时候,重置这个token对应的时间

        //3. 创建token
        Map<String, Object> tokenMap = new HashMap<>();
        tokenMap.put("id", user.getId());
        tokenMap.put("phone", user.getPhone());
        String token = JwtUtil.createToken(tokenMap);
        stringRedisTemplate.opsForValue().set(ConstantUtil.USER_TOKEN + token, "1", Duration.ofDays(30));//设置有效时间为30天


        //4. 组装返回map
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("token", token);
        resultMap.put("isNew", isNew);

        return resultMap;
    }

    //完善个人基本信息
    public void saveUserBaseInfo(UserInfo userInfo) {
        userInfoService.save(userInfo);
    }

    //完善个人头像信息
    public void saveUserHeadInfo(Long id, MultipartFile headPhoto) throws IOException {
        //1. 人脸识别,如果识别失败,返回异常
        boolean detect = aipFaceTemplate.detect(headPhoto.getBytes());
        System.out.println("人脸识别的结果:" + detect);
        if (!detect) {
            throw new RuntimeException("人脸审核失败");
        }

        //2. 阿里云文件上传,获取到访问地址
        String filePath = ossTemplate.upload(headPhoto.getOriginalFilename(), headPhoto.getInputStream());
        System.out.println("文件上传之后的路径:" + filePath);

        //3. 更新数据表user_info
        UserInfo userInfo = new UserInfo();//update set
        userInfo.setId(id);// where id = ?
        userInfo.setAvatar(filePath);//set avatar = filePath
        userInfo.setCoverPic(filePath);//coverPic = filePath
        userInfoService.update(userInfo);
    }

    //根据id查询
    public UserInfoVo findById(Long userID) {
        //1. 查询
        UserInfo userInfo = userInfoService.findById(userID);
        System.out.println(userInfo);

        //2. 结果封装
        UserInfoVo userInfoVo = new UserInfoVo();
        //2-1 复制属性,两个对象中的属性名称和类型必须完全一致
        BeanUtil.copyProperties(userInfo, userInfoVo);
        //2-2 单独复制age
        userInfoVo.setAge(userInfo.getAge() + "");

        //3. 返回结果
        return userInfoVo;
    }

    //更新个人信息
    public void updateUserBaseInfo(UserInfo userInfo) {
        userInfoService.update(userInfo);
    }

    //根据id查User
    public User findUserById(Long userId) {
        return   userService.findUserById(userId);
    }
    //校验验证码
    public Map<String, Boolean> checkCode(String code) {
        Long id = UserHolder.get().getId();
        User user = userService.findUserById(id);
        String phone = user.getPhone();
        //从redis中获取验证码
        String s = stringRedisTemplate.opsForValue().get(ConstantUtil.SMS_CODE + phone);
        if(!StringUtils.equals(code,s)){
            throw new RuntimeException("验证码错误");
        }
        Map<String, Boolean> map = new HashMap<>();
        map.put("verification", true);
        return map;
    }

    //修改电话号码
    public void updatePhone(String phone) {
        Long id = UserHolder.get().getId();
        User user = findByPhone(phone);
        if (user != null) {
            throw new RuntimeException("电话号码重复");
        }

        userService.updatePhone(phone,id);
    }
}
