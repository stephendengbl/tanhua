package com.itheima.web.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.itheima.domain.db.Admin;
import com.itheima.domain.mongo.Toplc_option;
import com.itheima.util.JwtUtil;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.TopicAdd;
import com.itheima.vo.TopicBackVo;
import com.itheima.web.manager.ToplcManager;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@RestController
public class ToplcController {

    @Autowired
    private ToplcManager toplcManager;

    //登录
    @GetMapping("/user/login")
    public String login(String telephone,String code){
        return toplcManager.login(telephone,code);
    }
    //访问认证
    @GetMapping("/user/verify")
    public String verify(@RequestHeader("Authorization") String token) {
        if (StringUtils.isEmpty(token) || StringUtils.equals(token, "null")) {
            return "401";//未通过
        }

        try {
            Map map = JwtUtil.parseToken(token);
            Admin user = BeanUtil.mapToBean(map, Admin.class, true);

            return "200";
        } catch (Exception e) {
            return "401";//未通过
        }
    }

    //分页查询
    @RequestMapping("/user/topic/findByPage")
    public PageBeanVo findByPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        //分页条件查询
        return toplcManager.findByPage(pageNum,pageSize);
    }
    //查询列表
    @RequestMapping("/user/topic/findList")
    public List<TopicBackVo> findList() {

        return toplcManager.findList();
    }

    //添加和修改题目
    @PostMapping("/user/topic/save")
    public String save(@RequestBody Map<String,String> map) throws InvocationTargetException, IllegalAccessException {
        TopicAdd topicAdd = new TopicAdd();
        BeanUtils.populate(topicAdd,map);
        //完整版
        return toplcManager.save(topicAdd);

    }
    //查询单个
    @GetMapping("/user/topic/findById")
    public TopicAdd findByQId(String id) {
        return toplcManager.findByQId(id);
    }

    //删除题目
    @DeleteMapping("/user/topic/deleteById")
    public String deleteById(String id) {
        return toplcManager.deleteById(id);

    }

    //批量删除
    @DeleteMapping("/user/topic/deleteByIds")
    public String deleteByIds(String ids) {
        String[] split = ids.split(",");
        List<String> idList = Arrays.asList(split);
        return toplcManager.deleteByIds(idList);

    }

}
