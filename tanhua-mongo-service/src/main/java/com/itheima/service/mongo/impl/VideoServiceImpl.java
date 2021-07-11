package com.itheima.service.mongo.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.itheima.domain.mongo.RecommendVideo;
import com.itheima.domain.mongo.Video;
import com.itheima.service.mongo.VideoService;
import com.itheima.util.ConstantUtil;
import com.itheima.vo.PageBeanVo;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private IdService idService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public PageBeanVo findVideo(Long userId, Integer pageNum, Integer pageSize) {
        //1. 根据userId查询推荐表,返回的是视频集合
        Query query = new Query(
                Criteria.where("userId").is(userId)
        ).with(Sort.by(Sort.Order.desc("score"), Sort.Order.desc("date")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);
        List<RecommendVideo> recommendVideoList = mongoTemplate.find(query, RecommendVideo.class);

        //2. 遍历集合,得到视频id
        List<Video> videoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(recommendVideoList)) {
            for (RecommendVideo recommendVideo : recommendVideoList) {
                //3. 根据视频id查询视频表,获取视频详情
                ObjectId videoId = recommendVideo.getVideoId();//推荐视频id
                Video video = mongoTemplate.findById(videoId, Video.class);
                videoList.add(video);
            }
        }

        //4 统计数量
        long count = mongoTemplate.count(query, RecommendVideo.class);

        //5. 返回
        return new PageBeanVo(pageNum, pageSize, count, videoList);
    }

    @Override
    public void saveVideo(Video video) {
        //1. 设置video的vid
        video.setVid(idService.getNextId(ConstantUtil.VIDEO_ID));

        //2. 保存video
        mongoTemplate.save(video);

        //3. 将当前视频推荐给自己(问题)
        RecommendVideo recommendVideo = new RecommendVideo();
        recommendVideo.setDate(System.currentTimeMillis());
        recommendVideo.setVid(video.getVid());
        recommendVideo.setUserId(video.getUserId());
        recommendVideo.setVideoId(video.getId());
        recommendVideo.setScore(100D);
        mongoTemplate.save(recommendVideo);
    }

    @Override
    public PageBeanVo findUserVideoList(Long uid, Integer pageNum, Integer pageSize) {
        //1. 构建查询条件
        Query query = new Query(Criteria.where("userId").is(uid))
                .with(Sort.by(Sort.Order.desc("created")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);

        //2. 查询
        List<Video> videoList = mongoTemplate.find(query, Video.class);

        //3. 统计数量
        long count = mongoTemplate.count(query, Video.class);

        //4. 返回
        return new PageBeanVo(pageNum,pageSize,count,videoList);
    }
}
