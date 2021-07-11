package com.itheima.service.db.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.domain.db.BlackList;
import com.itheima.domain.db.UserInfo;
import com.itheima.mapper.BlackListMapper;
import com.itheima.service.db.BlackListService;
import com.itheima.vo.PageBeanVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BlackListServiceImpl implements BlackListService {

    @Autowired
    private BlackListMapper blackListMapper;

    @Override
    public PageBeanVo findBlackUserByUserId(Long userId, Integer pageNum, Integer pageSize) {
        //1. 设置分页参数
        IPage<UserInfo> page = new Page<>(pageNum, pageSize);

        //2. 调用分页sql
        page = blackListMapper.findBlackUserByUserId(page, userId);

        //3. 组装返回PageBeanVo
        return new PageBeanVo(pageNum, pageSize, page.getTotal(), page.getRecords());
    }

    @Override
    public void deleteBlackUser(Long userId, Integer blackUserId) {
        //构建删除条件
        QueryWrapper<BlackList> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("black_user_id", blackUserId);

        blackListMapper.delete(wrapper);
    }
}
