package com.itheima.app.controller;

import com.itheima.app.manager.UserLikeManager;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.UserLikeCountVo;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserLikeController {

    @Autowired
    private UserLikeManager userLikeManager;

    //用户喜欢
    @GetMapping("/tanhua/{id}/love")
    public void saveUserLike(@PathVariable("id") Long likeUserId) {
        userLikeManager.saveUserLike(likeUserId);
    }

    //用户不喜欢
    @GetMapping("/tanhua/{id}/unlove")
    public void deleteUserLike(@PathVariable("id") Long likeUserId) {
        userLikeManager.deleteUserLike(likeUserId);
    }

    //统计用户数量
    @GetMapping("/users/counts")
    public UserLikeCountVo findUserCount() {
        return userLikeManager.findUserCount();
    }

    //互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表
    @GetMapping("/users/friends/{type}")
    public PageBeanVo findUserList(@PathVariable("type") Integer type,
                                   @RequestParam(value = "page", defaultValue = "1") Integer pageNum,
                                   @RequestParam(value = "pagesize", defaultValue = "10") Integer pageSize
    ) {
        return userLikeManager.findUserList(type, pageNum, pageSize);
    }

    //粉丝 - 喜欢
    @PostMapping("/users/fans/{uid}")
    public void saveFanLike(@PathVariable("uid") Long uid) {
        userLikeManager.saveUserLike(uid);
    }

    //喜欢 - 取消
    @DeleteMapping("/users/like/{uid}")
    public void deleteLike(@PathVariable("uid") Long uid) {
        userLikeManager.deleteUserLike(uid);
    }

}
