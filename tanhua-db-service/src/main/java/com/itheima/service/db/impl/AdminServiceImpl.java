package com.itheima.service.db.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.domain.db.Admin;
import com.itheima.mapper.AdminMapper;
import com.itheima.service.db.AdminService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public Admin findByUsername(String username) {
        QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);

        return adminMapper.selectOne(queryWrapper);
    }
}
