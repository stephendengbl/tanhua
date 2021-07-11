package com.itheima.app.controller;

import com.itheima.app.manager.MakeFriendManager;
import com.itheima.app.manager.MessageManager;
import com.itheima.vo.AkustischVo;
import com.itheima.vo.PageBeanVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class MessageController {

    @Autowired
    private MessageManager messageManager;

    @Autowired
    private MakeFriendManager makeFriendManager;

    //点赞列表
    @GetMapping("/messages/likes")
    public PageBeanVo findLikeComment(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ){
        //确定操作类型
        Integer commentType = 1;

        //调用manager
        return messageManager.findComment(commentType,pageNum,pageSize);
    }

    //评论列表
    @GetMapping("/messages/comments")
    public PageBeanVo findComment(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ){

        //确定操作类型
        Integer commentType = 2;

        //调用manager
        return messageManager.findComment(commentType,pageNum,pageSize);
    }

    //喜欢列表
    @GetMapping("/messages/loves")
    public PageBeanVo findLoveComment(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ){
        //确定操作类型
        Integer commentType = 3;

        //调用manager
        return messageManager.findComment(commentType,pageNum,pageSize);
    }
    //胡智琪
    //公告列表
    @GetMapping("/messages/announcements")
    public PageBeanVo findAnnouncement(@RequestParam(value = "page",defaultValue = "1")Integer pageNum,
                                       @RequestParam(value = "pagesize",defaultValue = "10")Integer pageSize){
        //调用manager
        return messageManager.findAnnouncement(pageNum,pageSize);
    }

}
