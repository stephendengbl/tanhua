package com.itheima.web.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import com.itheima.domain.db.Admin;
import com.itheima.domain.mongo.Topic;
import com.itheima.domain.mongo.Toplc_option;
import com.itheima.service.db.AdminService;
import com.itheima.service.mongo.ToplcService;
import com.itheima.util.ConstantUtil;
import com.itheima.util.JwtUtil;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.TopicAdd;
import com.itheima.vo.TopicBackVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class ToplcManager {
    @Reference
    private AdminService adminService;
    @Reference
    private ToplcService toplcService;


    //登录
    public String login(String adminName, String code) {
        Admin admin = adminService.findByUsername(adminName);
        if (admin == null) {
            return "1";
        }
        if (!StringUtils.equals(code, admin.getPassword())) {
            return "0";
        }
        //3. 登录成功之后,根据user创建token,并将token返回给前端
        String token = JwtUtil.createToken(BeanUtil.beanToMap(admin));
        return token;
    }

    //分页查询
    @RequestMapping("/user/topic/findByPage")
    public PageBeanVo findByPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                 @RequestParam(defaultValue = "5") Integer pageSize) {

        PageBeanVo pageBeanVo=toplcService.findByPage(pageNum,pageSize);

        return pageBeanVo;
    }


    public List<TopicBackVo> findList() {
        return toplcService.findList();
    }

    //添加题目
    public String save(TopicAdd topicAdd) {
        return toplcService.save(topicAdd);

    }

    public TopicAdd findByQId(String id) {
        return toplcService.findByQId(id);

    }

    public String deleteById(String id) {
        return toplcService.deleteById(id);
    }

    public String deleteByIds(List<String> ids) {
        return toplcService.deleteByIds(ids);
    }
}
