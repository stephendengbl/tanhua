package com.itheima.service.db.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.domain.db.User;
import com.itheima.mapper.UserMapper;
import com.itheima.service.db.UserService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

//阿里的
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public Long save(User user) {
        //mybatisplus 会进行自动的主键返回
        userMapper.insert(user);

        return user.getId();
    }

    @Override
    public User findByPhone(String phone) {
        //构造查询条件
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);

        return userMapper.selectOne(wrapper);
    }
    @Override
    public User findUserById(Long userId) {
        return userMapper.selectById(userId);
    }
    @Override
    public void updatePhone(String phone,Long id) {
        User user = new User();
        user.setPassword("123456");
        user.setPhone(phone);
        user.setId(id);
        userMapper.updateById(user);
    }
}
