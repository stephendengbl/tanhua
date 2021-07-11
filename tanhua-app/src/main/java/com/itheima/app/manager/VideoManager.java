package com.itheima.app.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.github.tobato.fastdfs.domain.conn.FdfsWebServer;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.autoconfig.oss.OssTemplate;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.mongo.Comment;
import com.itheima.domain.mongo.FocusUser;
import com.itheima.domain.mongo.Video;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.mongo.CommentService;
import com.itheima.service.mongo.FocusUserService;
import com.itheima.service.mongo.VideoService;
import com.itheima.util.ConstantUtil;
import com.itheima.util.DateFormatUtil;
import com.itheima.vo.CommentVo;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.VideoVo;
import org.apache.dubbo.config.annotation.Reference;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VideoManager {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private FdfsWebServer fdfsWebServer;

    @Reference
    private VideoService videoService;

    @Reference
    private CommentService commentService;

    @Reference
    private UserInfoService userInfoService;

    @Reference
    private FocusUserService focusUserService;

    //查询小视频列表
    @Cacheable(value = "video", key = "#userId+'_'+#pageNum+'_'+#pageSize")
    public PageBeanVo findVideo(Long userId, Integer pageNum, Integer pageSize) {
        //1. 登录用户id
        //Long userId = UserHolder.get().getId();

        //2. 调用service查询
        PageBeanVo pageBeanVo = videoService.findVideo(userId, pageNum, pageSize);

        //3. 处理结果
        List<Video> videoList = (List<Video>) pageBeanVo.getItems();
        List<VideoVo> videoVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(videoList)) {
            for (Video video : videoList) {
                //3-1 创建vo
                VideoVo videoVo = new VideoVo();

                //3-2 设置视频发布人信息
                Long videoUserId = video.getUserId();
                UserInfo userInfo = userInfoService.findById(videoUserId);
                BeanUtil.copyProperties(userInfo, videoVo);

                //3-3 设置视频信息
                BeanUtil.copyProperties(video, videoVo);

                //3-4 补充未赋值的字段
                videoVo.setCover(video.getPicUrl());//封面
                videoVo.setSignature(video.getText());//内容
                videoVo.setHasLiked(1);//todo 临时模拟
                //videoVo.setHasFocus(1);//todo 临时模拟
                if (stringRedisTemplate.hasKey(StrUtil.format(ConstantUtil.FOCUS_USER, userId, video.getUserId()))) {
                    videoVo.setHasFocus(1);
                } else {
                    videoVo.setHasFocus(0);
                }

                //3-5 收集
                videoVoList.add(videoVo);
            }
        }

        pageBeanVo.setItems(videoVoList);
        return pageBeanVo;
    }

    //发布视频
    //@CacheEvict(value = "video", allEntries = true)
    @CacheEvict(value = "video", key = "#userId+'_*'")
    public void saveVideo(Long userId,MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        //1. 封面保存到oss
        String picUrl = ossTemplate.upload(videoThumbnail.getOriginalFilename(), videoThumbnail.getInputStream());

        //2. 视频保存到fastDfs
        StorePath storePath = client.uploadFile(videoFile.getInputStream(), videoFile.getSize(),
                FileUtil.extName(videoFile.getOriginalFilename()), null);
        String videoUrl = fdfsWebServer.getWebServerUrl() + storePath.getFullPath();

        //3. 封装video对象
        Video video = new Video();
        video.setCreated(System.currentTimeMillis());
        video.setUserId(userId);
        video.setText("左手右手一个慢动作~~~~");
        video.setPicUrl(picUrl);
        video.setVideoUrl(videoUrl);

        //4. 调用service保存
        videoService.saveVideo(video);

    }

    //添加关注
    public void saveUserFocus(Long focusUserId) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用service添加关注
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(userId);
        focusUser.setFocusUserId(focusUserId);
        focusUser.setCreated(System.currentTimeMillis());
        focusUserService.save(focusUser);

        //3. 向redis中保存关注信息  focus_user:99_7
        stringRedisTemplate.opsForValue().set(StrUtil.format(ConstantUtil.FOCUS_USER, userId, focusUserId), "1");

    }

    //移除关注
    public void deleteUserFocus(Long focusUserId) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用service删除关注
        FocusUser focusUser = new FocusUser();
        focusUser.setUserId(userId);
        focusUser.setFocusUserId(focusUserId);
        focusUser.setCreated(System.currentTimeMillis());
        focusUserService.delete(focusUser);

        //3. 从redis中删除关注信息
        stringRedisTemplate.delete(StrUtil.format(ConstantUtil.FOCUS_USER, userId, focusUserId));
    }

    //姚远
    public void saveVideoLike(Long userid, String videoId) {
        //1.组装comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(4);
        comment.setUserId(UserHolder.get().getId());

        //2.调用service进行点赞
        commentService.saveVideoComment(comment);

        //3.向redis中保存点赞标识
        stringRedisTemplate.opsForValue().set(StrUtil.format(ConstantUtil.VIDEO_LIKE,UserHolder.get().getId(),videoId),"1");

        //4.向消息中间件发送操作，用于计算分数

    }


    //视频点赞取消【实战】
    public void deleteVideoLike(Long userid, String videoId) {
        //1. 组装comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(4);
        comment.setUserId(UserHolder.get().getId());

        //2. 调用service进行取消点赞
        commentService.deleteVideoComment(comment);

        //3. 从redis中删除点赞标识
        stringRedisTemplate.delete(StrUtil.format(ConstantUtil.VIDEO_LIKE, UserHolder.get().getId(), videoId));
        //4.发送消息给MQ
        //  videoMQManager.sendMsg(videoId, VideoMQManager.VIDEO_DISLIKE);


    }

    //【实战】视频评论发布
    public void saveVideoComment(Long userId, String videoId, String commentContent) {

        if (stringRedisTemplate.opsForValue().get("freezeFayan" + UserHolder.get().getId())!=null) {
            return ;
        }

        //1.封装comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(videoId));
        comment.setCommentType(5);
        comment.setUserId(UserHolder.get().getId());
        comment.setContent(commentContent);

        //2.调用service保存评论
        commentService.saveVideoComment(comment);


    }

    //【实战】视频评论的列表
    public PageBeanVo findVideoComment(String videoId, Integer pageNum, Integer pageSize) {
        //1.根据动态id,分页查询评论的列表
        PageBeanVo pageBeanVo = commentService.findVideoComment(videoId,pageNum,pageSize);

        //2.封装返回结果
        List<Comment> commentList = (List<Comment>) pageBeanVo.getItems();
        List<CommentVo> commentVoList = new ArrayList<>();

        if(CollectionUtil.isNotEmpty(commentList)){
            for (Comment comment : commentList) {
                //创建vo
                CommentVo commentVo = new CommentVo();
                //用户信息
                UserInfo userInfo = userInfoService.findById(comment.getUserId());
                commentVo.setAvatar(userInfo.getAvatar());
                commentVo.setNickname(userInfo.getNickname());
                //评论信息
                if(comment.getIsParent()){
                    commentVo.setLikeCount(comment.getLikeCount());
                }
                commentVo.setId(comment.getId().toHexString());
                commentVo.setContent(comment.getContent());
                commentVo.setCreateDate(DateFormatUtil.format(new Date(comment.getCreated())));
                //设置当前动态的点赞状态
                if(stringRedisTemplate.hasKey(StrUtil.format(ConstantUtil.COMMENT_LIKE,UserHolder.get().getId(),commentVo.getId()))){
                    commentVo.setHasLiked(1);
                }
                commentVoList.add(commentVo);
            }
        }
        pageBeanVo.setItems(commentVoList);

        return pageBeanVo;
    }

    //评论点赞
    public void saveVideoCommentLike(Long userId, String commentId) {

        //1. 封装comment对象
        Comment comment = new Comment();
        comment.setUserId(UserHolder.get().getId());
        comment.setCommentType(6);
        comment.setParentId(new ObjectId(commentId));

        //2.调用service进行点赞
        commentService.saveVideoCommentLike(comment);
        //3.向redis中保存点赞标识
        stringRedisTemplate.opsForValue().set(StrUtil.format(ConstantUtil.COMMENT_LIKE,UserHolder.get().getId(),commentId),"1");

    }

    //取消视频评论点赞
    public void deleteVideoCommentLike(Long userId, String commentId) {
        //1. 封装comment对象
        Comment comment = new Comment();
        comment.setUserId(UserHolder.get().getId());
        comment.setCommentType(6);
        comment.setParentId(new ObjectId(commentId));
        //2.调用service进行取消点赞
        commentService.deleteVideoCommentLike(comment);
        //3. 从redis中删除点赞标识
        stringRedisTemplate.delete(StrUtil.format(ConstantUtil.COMMENT_LIKE, UserHolder.get().getId(), commentId));
    }
}