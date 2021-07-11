package com.itheima.web.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.domain.db.Freeze;
import com.itheima.domain.db.UserInfo;
import com.itheima.vo.MovementVo;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.ThawingVo;
import com.itheima.vo.freezeVo;
import com.itheima.web.manager.UserManager;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserManager userManager;

    //查询用户列表
    @GetMapping("/manage/users")
    public PageBeanVo findUserList(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {

        return userManager.findUserList(pageNum, pageSize);
    }

    //查询用户详情
    @GetMapping("/manage/users/{userID}")
    public freezeVo findUserInfo(@PathVariable("userID") Long userId) {

        return userManager.findUserInfo(userId);
    }

    //查询用户的小视频
    @GetMapping("/manage/videos")
    public PageBeanVo findUserVideoList(Long uid,
                                        @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                        @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {
        return userManager.findUserVideoList(uid, pageNum, pageSize);
    }

    //查询动态
    @GetMapping("/manage/messages")
    public PageBeanVo findMovementList(
            Long uid, String state,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {
        //处理一下参数(由于前端传递的state为空的时候,会是一个"")
        Integer stateInt = null;
        if (StringUtils.isNotEmpty(state) && !"''".equals(state)) {
            stateInt = Integer.parseInt(state);
        }

        return userManager.findMovementList(uid, stateInt, pageNum, pageSize);
    }

    //动态详情
    @GetMapping("/manage/messages/{id}")
    public MovementVo findMovementById(@PathVariable("id") String movementId) {
        return userManager.findMovementById(movementId);
    }


    //动态评论列表
    @GetMapping("/manage/messages/comments")
    public PageBeanVo findMovementComment(
            String messageID,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {
        return userManager.findMovementComment(messageID, pageNum, pageSize);
    }

    //冻结
    @PostMapping("/manage/users/freeze")
    public Map<String,String> freeze(@RequestBody Freeze freeze){
        return userManager.freeze(freeze);

    }
    //解冻
    @PostMapping("/manage/users/unfreeze")
    public Map<String,String> unfreeze(@RequestBody ThawingVo thawingVo){
        //接受参数
        Integer userId = thawingVo.getUserId();
        String reasonsForThawing = thawingVo.getReasonsForThawing();
        userManager.unfreeze(userId,reasonsForThawing);
        return  null;
    }

}
