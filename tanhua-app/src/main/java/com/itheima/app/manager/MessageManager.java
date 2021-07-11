package com.itheima.app.manager;

import cn.hutool.core.collection.CollectionUtil;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.domain.db.Announcement;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.mongo.Comment;
import com.itheima.service.db.AnnouncementService;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.mongo.CommentService;
import com.itheima.vo.AnnouncementVo;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.UserCommentVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MessageManager {

    @Reference
    private CommentService commentService;

    @Reference
    private UserInfoService userInfoService;

    @Reference
    private AnnouncementService announcementService;

    //根据操作类型和被操作人id查询评论列表
    public PageBeanVo findComment(Integer commentType, Integer pageNum, Integer pageSize) {
        //1. 登录用户
        Long userId = UserHolder.get().getId();

        //2. 调用service查询
        PageBeanVo pageBeanVo = commentService.findCountComment(userId, commentType, pageNum, pageSize);

        //3. 封装返回结果
        List<Comment> commentList = (List<Comment>) pageBeanVo.getItems();
        List<UserCommentVo> userCommentVoList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(commentList)) {
            for (Comment comment : commentList) {
                //创建vo
                UserCommentVo userCommentVo = new UserCommentVo();

                //封装
                UserInfo userInfo = userInfoService.findById(comment.getUserId());//查询的评论人
                userCommentVo.setId(comment.getId().toHexString());
                userCommentVo.setAvatar(userInfo.getAvatar());
                userCommentVo.setNickname(userInfo.getNickname());
                userCommentVo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(comment.getCreated())));

                //存入list
                userCommentVoList.add(userCommentVo);
            }
        }

        pageBeanVo.setItems(userCommentVoList);
        return pageBeanVo;
    }

    //胡智奇
    //查询公告列表
    public PageBeanVo findAnnouncement(Integer pageNum, Integer pageSize) {
        //调用service查询
        PageBeanVo pageBeanVo = announcementService.findAnnouncementList(pageNum,pageSize);
        List<Announcement> announcementList = (List<Announcement>) pageBeanVo.getItems();
        List<AnnouncementVo> announcementVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(announcementList)) {
            for (Announcement announcement : announcementList) {
                AnnouncementVo announcementVo = new AnnouncementVo();
                announcementVo.setTitle(announcement.getTitle());
                announcementVo.setDescription(announcement.getDescription());
                announcementVo.setCreateDate(new SimpleDateFormat("yyyy-MM-dd").format(announcement.getCreated()));

                announcementVoList.add(announcementVo);
            }
        }
        pageBeanVo.setItems(announcementVoList);
        return pageBeanVo;
    }
}