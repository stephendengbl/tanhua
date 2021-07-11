package com.itheima.service.db;

import com.itheima.vo.PageBeanVo;

public interface AnnouncementService {
    //查询公告列表
    PageBeanVo findAnnouncementList(Integer pageNum, Integer pageSize);
}
