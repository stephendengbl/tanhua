package com.itheima.app.controller;

import com.itheima.app.interceptor.UserHolder;
import com.itheima.app.manager.VideoManager;
import com.itheima.vo.PageBeanVo;
import com.sun.org.apache.xpath.internal.operations.Mult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class VideoController {

    @Autowired
    private VideoManager videoManager;

    //value=video  key=99_1_10   userId_pageNum_pageSize

    //查询小视频列表

    @GetMapping("/smallVideos")
    public PageBeanVo findVideo(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {

        Long userId = UserHolder.get().getId();

        return videoManager.findVideo(userId, pageNum, pageSize);
    }


    //发布小视频
    @PostMapping("/smallVideos")
    public void saveVideo(MultipartFile videoThumbnail, MultipartFile videoFile) throws IOException {
        Long userId = UserHolder.get().getId();
        videoManager.saveVideo(userId, videoThumbnail, videoFile);

    }


    //添加关注
    @PostMapping("/smallVideos/{uid}/userFocus")
    public void saveUserFocus(@PathVariable("uid") Long focusUserId) {
        videoManager.saveUserFocus(focusUserId);
    }

    //移除关注
    @PostMapping("/smallVideos/{uid}/userUnFocus")
    public void deleteUserFocus(@PathVariable("uid") Long focusUserId) {

        videoManager.deleteUserFocus(focusUserId);
    }

    //姚远

    @PostMapping("/smallVideos/{id}/like")
    public void saveVideoLike(@PathVariable("id") String videoId){
        videoManager.saveVideoLike(UserHolder.get().getId(),videoId);
    }

    //【实战】取消视频点赞
    @PostMapping("/smallVideos/{id}/dislike")
    public void deleteVideoLike(@PathVariable("id") String videoId){
        videoManager.deleteVideoLike(UserHolder.get().getId(),videoId);
    }

    //视频评论发布
    @PostMapping("/smallVideos/{id}/comments")
    public void saveVideoComment(@PathVariable("id") String videoId, @RequestBody Map<String,String> map){
        //1.接收参数
        String commentContent = map.get("comment");
        //2. 调用service保存评论
        videoManager.saveVideoComment(UserHolder.get().getId(),videoId,commentContent);
    }
    //视频评论列表
    @GetMapping("/smallVideos/{id}/comments")
    public PageBeanVo findVideoComment(@PathVariable("id")String videoId,
                                       @RequestParam(value = "page",defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pagesize",defaultValue = "10") Integer pageSize){
        return videoManager.findVideoComment(videoId,pageNum,pageSize);
    }

    //视频评论点赞
    @PostMapping("/smallVideos/comments/{id}/like")
    public void saveVideoCommentLike(@PathVariable("id") String commentId) {
        videoManager.saveVideoCommentLike(UserHolder.get().getId(),commentId);
    }
    //取消视频评论点赞
    @PostMapping("/smallVideos/comments/{id}/dislike")
    public void deleteVideoCommentLike(@PathVariable("id") String commentId) {
        videoManager.deleteVideoCommentLike(UserHolder.get().getId(),commentId);
    }
}
