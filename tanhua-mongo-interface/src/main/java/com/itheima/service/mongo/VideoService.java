package com.itheima.service.mongo;

import com.itheima.domain.mongo.Video;
import com.itheima.vo.PageBeanVo;

public interface VideoService {

    //根据用户id查询推荐的视频列表
    PageBeanVo findVideo(Long userId, Integer pageNum, Integer pageSize);

    //保存视频
    void saveVideo(Video video);

    //根据用户id查询视频列表
    PageBeanVo findUserVideoList(Long uid, Integer pageNum, Integer pageSize);
}
