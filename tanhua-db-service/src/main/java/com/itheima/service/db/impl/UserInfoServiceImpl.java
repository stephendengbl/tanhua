package com.itheima.service.db.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.domain.db.UserInfo;
import com.itheima.mapper.UserInfoMapper;
import com.itheima.service.db.UserInfoService;
import com.itheima.vo.PageBeanVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserInfoServiceImpl implements UserInfoService {

    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public void save(UserInfo userInfo) {
        userInfoMapper.insert(userInfo);
    }

    @Override
    public void update(UserInfo userInfo) {
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public UserInfo findById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public PageBeanVo findUserList(Integer pageNum, Integer pageSize) {
        //1. 设置Page对象
        IPage<UserInfo> page = new Page<>(pageNum, pageSize);

        //2. 调用查询方法
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        page = userInfoMapper.selectPage(page, wrapper);

        //3. 组装返回对象
        return new PageBeanVo(pageNum, pageSize, page.getTotal(), page.getRecords());
    }
}
