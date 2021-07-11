package com.itheima.app.controller;

import com.itheima.app.interceptor.UserHolder;
import com.itheima.app.manager.MovementManager;
import com.itheima.domain.mongo.Movement;
import com.itheima.vo.MovementVo;
import com.itheima.vo.PageBeanVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
public class MovementController {

    @Autowired
    private MovementManager movementManager;

    //发布动态
    @PostMapping("/movements")
    public void saveMovement(Movement movement, MultipartFile[] imageContent) throws IOException {

        //调用manager
        movementManager.saveMovement(movement, imageContent);
    }


    //查询我的动态
    @GetMapping("/movements/all")
    public PageBeanVo findMyMovement(Long userId,
                                     @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        //调用manager查询
        return movementManager.findMyMovement(userId, pageNum, pageSize);
    }


    //查询我的好友的动态
    @GetMapping("/movements")
    public PageBeanVo findMyFriendMovement(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize) {
        //调用manager查询
        return movementManager.findMyFriendMovement(pageNum, pageSize);
    }

    //查询推荐给我的动态
    @GetMapping("/movements/recommend")
    public PageBeanVo findRecommendMovement(
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {
        //调用manager查询
        return movementManager.findRecommendMovement(pageNum, pageSize);
    }


    //动态点赞
    @GetMapping("/movements/{id}/like")
    public Integer saveMovementLike(@PathVariable("id") String movementId) {
        return movementManager.saveMovementLike(movementId);
    }


    //动态取消点赞
    @GetMapping("/movements/{id}/dislike")
    public Integer deleteMovementLike(@PathVariable("id") String movementId) {
        return movementManager.deleteMovementLike(movementId);
    }


    //动态喜欢
    @GetMapping("/movements/{id}/love")
    public Integer saveMovementLove(@PathVariable("id") String movementId) {
        return movementManager.saveMovementLove(movementId);
    }


    //动态取消点赞
    @GetMapping("/movements/{id}/unlove")
    public Integer deleteMovementLove(@PathVariable("id") String movementId) {
        return movementManager.deleteMovementLove(movementId);
    }

    //查询指定id的动态详情
    @GetMapping("/movements/{id}")
    public MovementVo findMovementById(@PathVariable("id") String movementId) {
        return movementManager.findMovementById(movementId);
    }


    //查询动态评论
    @GetMapping("/comments")
    public PageBeanVo findMovementComment(
            String movementId,
            @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {
        return movementManager.findMovementComment(movementId, pageNum, pageSize);
    }


    //对动态进行语言评论
    @PostMapping("/comments")
    public void saveMovementComment(@RequestBody Map<String, String> map) {
        //1. 接收参数
        String movementId = map.get("movementId");
        String commentContent = map.get("comment");

        //2. 调用service保存评论
        movementManager.saveMovementComment(movementId,commentContent);
    }

    //姚远
    //动态评论点赞
    @GetMapping("/comments/{id}/like")
    public Integer saveMovementCommentLike(@PathVariable("id") String commentId) {
        return movementManager.saveMovementCommentLike(UserHolder.get().getId(),commentId);
    }
    //动态评论取消点赞
    @GetMapping("/comments/{id}/dislike")
    public Integer deleteMovementCommentLike(@PathVariable("id") String commentId) {
        return movementManager.deleteMovementCommentLike(UserHolder.get().getId(),commentId);
    }

}
