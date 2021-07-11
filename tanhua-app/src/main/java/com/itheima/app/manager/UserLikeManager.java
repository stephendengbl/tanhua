package com.itheima.app.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.autoconfig.huanxin.HuanXinTemplate;
import com.itheima.domain.db.UserInfo;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.mongo.FriendService;
import com.itheima.service.mongo.UserLikeService;
import com.itheima.vo.PageBeanVo;
import com.itheima.vo.UserLikeCountVo;
import com.itheima.vo.UserLikeVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Service
public class UserLikeManager {

    @Autowired
    private HuanXinTemplate huanXinTemplate;

    @Reference
    private UserLikeService userLikeService;

    @Reference
    private FriendService friendService;

    @Reference
    private UserInfoService userInfoService;

    //用户喜欢
    public void saveUserLike(Long likeUserId) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用userLikeService保存喜欢关系
        userLikeService.save(userId, likeUserId);

        //3. 调用userLikeService查询二人是否互相喜欢
        boolean isMutualLike = userLikeService.isMutualLike(userId, likeUserId);

        //4. 如果是互相喜欢
        if (isMutualLike) {
            //4-1 调用friendService保存好友关系
            friendService.save(userId, likeUserId);

            //4-2 调用环信保存好友关系
            huanXinTemplate.addContacts(userId, likeUserId);
        }
    }

    //用户不喜欢
    public void deleteUserLike(Long likeUserId) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用friendService删除好友
        friendService.delete(userId, likeUserId);

        //3. 调用环信删除好友
        huanXinTemplate.deleteContacts(userId, likeUserId);

        //4. 调用userLikeService删除我对它的喜欢
        userLikeService.delete(userId, likeUserId);
    }

    //统计用户数量
    public UserLikeCountVo findUserCount() {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 返回统计对象
        return userLikeService.findUserCount(userId);
    }

    //互相喜欢、喜欢、粉丝、谁看过我 - 翻页列表
    public PageBeanVo findUserList(Integer type, Integer pageNum, Integer pageSize) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 调用service查询
        PageBeanVo pageBeanVo = userLikeService.findUserList(userId, type, pageNum, pageSize);

        //3. 处理一下LikeUserVo中用户信息部分
        List<UserLikeVo> userLikeVoList = (List<UserLikeVo>) pageBeanVo.getItems();
        if (CollectionUtil.isNotEmpty(userLikeVoList)) {
            for (UserLikeVo userLikeVo : userLikeVoList) {
                UserInfo userInfo = userInfoService.findById(userLikeVo.getId());
                BeanUtil.copyProperties(userInfo, userLikeVo);
            }
        }

        return pageBeanVo;
    }
}