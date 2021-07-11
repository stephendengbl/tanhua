package com.itheima.service.db.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.domain.db.Announcement;
import com.itheima.mapper.AnnouncementMapper;
import com.itheima.service.db.AnnouncementService;
import com.itheima.vo.PageBeanVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public PageBeanVo findAnnouncementList(Integer pageNum, Integer pageSize) {
        IPage<Announcement> page = new Page<>(pageNum,pageSize);
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        announcementMapper.selectPage(page,wrapper);
        //返回组装对象
        return new PageBeanVo(pageNum,pageSize,page.getTotal(),page.getRecords());
    }
}
