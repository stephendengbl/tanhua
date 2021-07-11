package com.itheima.web.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.domain.db.Freeze;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.mongo.Comment;
import com.itheima.domain.mongo.Movement;
import com.itheima.domain.mongo.Video;
import com.itheima.service.db.FreezeService;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.mongo.CommentService;
import com.itheima.service.mongo.MovementService;
import com.itheima.service.mongo.VideoService;
import com.itheima.util.ConstantUtil;
import com.itheima.util.DateFormatUtil;
import com.itheima.vo.*;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;

@Service
public class UserManager {

    @Reference
    private UserInfoService userInfoService;

    @Reference
    private VideoService videoService;

    @Reference
    private MovementService movementService;

    @Reference
    private FreezeService freezeService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Reference
    private CommentService commentService;

    //查询用户列表
    public PageBeanVo findUserList( Integer pageNum, Integer pageSize) {
        //获取用户信息
        PageBeanVo pageBeanVo = userInfoService.findUserList(pageNum, pageSize);
        List<UserInfo> userInfoList= (List<UserInfo>) pageBeanVo.getItems();
        //查询用户信息，遍历获得id
        ArrayList<freezeVo> freezeVoList = new ArrayList<>();
        for (UserInfo userInfo : userInfoList) {
            //根据id查询
            Long id = userInfo.getId();
            //freeze freeze = freezeService.findById(id.intValue());

            freezeVo freezeVo = new freezeVo();

            if (stringRedisTemplate.opsForValue().get("freezeDengLu"+id) !=null||
                    stringRedisTemplate.opsForValue().get("freezeFayan"+id) !=null||
                    stringRedisTemplate.opsForValue().get("freezeDongTai"+id) !=null){
                freezeVo.setUserStatus("2");
            }else {
                freezeVo.setUserStatus("1");
            }
            BeanUtil.copyProperties(userInfo,freezeVo);
            //处理查询结果
            freezeVoList.add(freezeVo);
        }
        pageBeanVo.setItems(freezeVoList);
        return pageBeanVo;
    }

    /* //查询用户详情
     public UserInfo findUserInfo(Long userId) {
         return userInfoService.findById(userId);
     }*/
    //查询用户详情
    public freezeVo findUserInfo(Long userId) {
        //获取用户id
        UserInfo userInfo = userInfoService.findById(userId);
        //freeze freeze= freezeService.findById(userId.intValue());
        //处理结果
        freezeVo freezeVo = new freezeVo();
        BeanUtil.copyProperties(userInfo,freezeVo);

        if (stringRedisTemplate.opsForValue().get("freezeDengLu"+userId) !=null||
                stringRedisTemplate.opsForValue().get("freezeFayan"+userId) !=null||
                stringRedisTemplate.opsForValue().get("freezeDongTai"+userId) !=null){
            freezeVo.setUserStatus("2");
        }else {
            freezeVo.setUserStatus("1");
        }
        return freezeVo;
    }


    //查询用户视频列表
    public PageBeanVo findUserVideoList(Long uid, Integer pageNum, Integer pageSize) {
        //1. 调用service查询指定用户的视频列表
        PageBeanVo pageBeanVo = videoService.findUserVideoList(uid, pageNum, pageSize);

        //2. 处理返回结果
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

                //3-5 收集
                videoVoList.add(videoVo);
            }
        }

        pageBeanVo.setItems(videoVoList);
        return pageBeanVo;
    }

    //用户动态列表
    public PageBeanVo findMovementList(Long uid, Integer stateInt, Integer pageNum, Integer pageSize) {
        //1. 调用service查询
        PageBeanVo pageBeanVo = movementService.findMovementList(uid, stateInt, pageNum, pageSize);
        //2. 处理返回结果
        List<Movement> movementList = (List<Movement>) pageBeanVo.getItems();
        List<MovementVo> movementVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(movementList)) {
            for (Movement movement : movementList) {
                UserInfo userInfo = userInfoService.findById(movement.getUserId());
                MovementVo movementVo = new MovementVo();
                movementVo.setUserInfo(userInfo);
                movementVo.setMovement(movement);
                movementVo.setCreateDate(DateUtil.offsetDay(new Date(movement.getCreated()), 0).toString());
                movementVoList.add(movementVo);
            }
        }

        //3. 替换pageBeanVo中的items部分
        pageBeanVo.setItems(movementVoList);
        return pageBeanVo;
    }

    //查询动态详情
    public MovementVo findMovementById(String movementId) {
        //1. 根据动态id,调用service查询
        Movement movement = movementService.findMovementById(movementId);

        //2. 处理返回vo
        UserInfo userInfo = userInfoService.findById(movement.getUserId());
        MovementVo movementVo = new MovementVo();
        movementVo.setUserInfo(userInfo);
        movementVo.setMovement(movement);
        movementVo.setCreateDate(DateUtil.offsetDay(new Date(movement.getCreated()), 0).toString());

        return movementVo;
    }

    //查询动态评论
    public PageBeanVo findMovementComment(String movementId, Integer pageNum, Integer pageSize) {
        //1. 根据动态id,分页查询评论列表
        PageBeanVo pageBeanVo = commentService.findMovementComment(movementId, pageNum, pageSize);

        //2. 封装返回结果
        List<Comment> commentList = (List<Comment>) pageBeanVo.getItems();
        List<CommentVo> commentVoList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(commentList)) {
            for (Comment comment : commentList) {
                //创建vo
                CommentVo commentVo = new CommentVo();

                //用户信息
                UserInfo userInfo = userInfoService.findById(comment.getUserId());
                commentVo.setAvatar(userInfo.getAvatar());
                commentVo.setNickname(userInfo.getNickname());

                //评论信息
                commentVo.setId(comment.getId().toHexString());
                commentVo.setContent(comment.getContent());
                commentVo.setCreateDate(DateFormatUtil.format(new Date(comment.getCreated())));

                commentVoList.add(commentVo);
            }
        }

        pageBeanVo.setItems(commentVoList);
        return pageBeanVo;
    }

    //冻结用户
    public Map<String, String> freeze(Freeze freeze) {
        //1.先到冻结表中查询一下是否有当前用户的冻结信息
        Freeze freezeOne=freezeService.findFreezeByUserId(freeze.getUserId());
        Boolean b = false;
        //2.如果没有，就添加
        if (freezeOne == null) {
            Freeze freeze1 = new Freeze();
            freeze1.setUserId(freeze.getUserId());
            if (freeze.getFreezingTime()==1){
                //冻结时间存入表中
                freeze1.setFreezingTime(1);
                //冻结时间存入redis中
                //stringRedisTemplate.opsForValue().set("freezeTime"+1+"_"+freeze.getUserId(),"2", Duration.ofDays(3));

                //判断冻结范围
                if (freeze.getFreezingRange()==1){
                    freeze1.setFreezingRange(1);
                    stringRedisTemplate.opsForValue().set("freezeDengLu"+freeze.getUserId(),"1",Duration.ofDays(3));

                }else if (freeze.getFreezingRange()==2){
                    freeze1.setFreezingRange(2);
                    stringRedisTemplate.opsForValue().set("freezeFayan"+freeze.getUserId(),"2",Duration.ofDays(3));
                }else if (freeze.getFreezingRange()==3){
                    freeze1.setFreezingRange(3);
                    stringRedisTemplate.opsForValue().set("freezeDongTai"+freeze.getUserId(),"3",Duration.ofDays(3));
                }
            }else if (freeze.getFreezingTime()==2) {
                freeze1.setFreezingTime(2);
                //stringRedisTemplate.opsForValue().set("freezeTime"+2+"_"+freeze.getUserId(), "2", Duration.ofDays(7));

                //判断冻结类型
                if (freeze.getFreezingRange() == 1) {
                    freeze1.setFreezingRange(1);
                    stringRedisTemplate.opsForValue().set("freezeDengLu"+freeze.getUserId(), "1", Duration.ofDays(7));
                } else if (freeze.getFreezingRange() == 2) {
                    freeze1.setFreezingRange(2);
                    stringRedisTemplate.opsForValue().set("freezeFayan"+freeze.getUserId(), "2", Duration.ofDays(7));
                } else if (freeze.getFreezingRange() == 3) {
                    freeze1.setFreezingRange(3);
                    stringRedisTemplate.opsForValue().set("freezeTongTai"+freeze.getUserId(), "3", Duration.ofDays(7));//todo
                }
            }else if (freeze.getFreezingTime()==3) {
                freeze1.setFreezingTime(3);

                //判断冻结类型
                if (freeze.getFreezingRange() == 1) {
                    freeze1.setFreezingRange(1);
                    stringRedisTemplate.opsForValue().set("freezeDenglu"+freeze.getUserId(), "1", Duration.ofDays(9999));
                } else if (freeze.getFreezingRange() == 2) {
                    freeze1.setFreezingRange(2);
                    stringRedisTemplate.opsForValue().set("freezeFayan"+freeze.getUserId(), "2", Duration.ofDays(9999));
                } else if (freeze.getFreezingRange() == 3) {
                    freeze1.setFreezingRange(3);
                    stringRedisTemplate.opsForValue().set("freezeDongTai"+freeze.getUserId(), "3", Duration.ofDays(9999));
                }
            }
            //打印冻结原因
            System.out.println(freeze.getReasonsForFreezing());
            //存储原因
            freeze1.setReasonsForFreezing(freeze.getReasonsForFreezing());
            //存储备注
            freeze1.setFrozenRemarks(freeze.getFrozenRemarks());
            freezeService.saveFreeze(freeze1);
        }
        //3.如果有，就更新表状态
        if (freezeOne != null) {
            //修改
            Integer id = freezeOne.getId();

            Freeze freeze1 = new Freeze();
            freeze1.setId(id);
            freeze1.setUserId(freeze.getUserId());
            if (freeze.getFreezingTime()==1){
                //冻结时间存入表中
                freeze1.setFreezingTime(1);
                //冻结时间存入redis中
                //判断冻结范围
                if (freeze.getFreezingRange()==1){
                    freeze1.setFreezingRange(1);
                    stringRedisTemplate.opsForValue().set("freezeDengLu"+freeze.getUserId(),"1",Duration.ofDays(3));

                }else if (freeze.getFreezingRange()==2){
                    freeze1.setFreezingRange(2);
                    stringRedisTemplate.opsForValue().set("freezeFayan"+freeze.getUserId(),"2",Duration.ofDays(3));
                }else if (freeze.getFreezingRange()==3){
                    freeze1.setFreezingRange(3);
                    stringRedisTemplate.opsForValue().set("freezeDongTai"+freeze.getUserId(),"3",Duration.ofDays(3));//todo
                }
            }else if (freeze.getFreezingTime()==2) {
                freeze1.setFreezingTime(2);
                //判断冻结类型
                if (freeze.getFreezingRange() == 1) {
                    freeze1.setFreezingRange(1);
                    stringRedisTemplate.opsForValue().set("freezeDengLu" + freeze.getUserId(), "1", Duration.ofDays(7));
                } else if (freeze.getFreezingRange() == 2) {
                    freeze1.setFreezingRange(2);
                    stringRedisTemplate.opsForValue().set("freezeFayan" + freeze.getUserId(), "2", Duration.ofDays(7));
                } else if (freeze.getFreezingRange() == 3) {
                    freeze1.setFreezingRange(3);
                    stringRedisTemplate.opsForValue().set("freezeDongTai" + freeze.getUserId(), "3", Duration.ofDays(7));//todo
                }
            }else if (freeze.getFreezingTime()==3) {
                freeze1.setFreezingTime(3);
                //判断冻结类型
                if (freeze.getFreezingRange() == 1) {
                    freeze1.setFreezingRange(1);
                    stringRedisTemplate.opsForValue().set("freezeDengLu" + freeze.getUserId(), "1", Duration.ofDays(9999));
                } else if (freeze.getFreezingRange() == 2) {
                    freeze1.setFreezingRange(2);
                    stringRedisTemplate.opsForValue().set("freezeFayan" + freeze.getUserId(), "2", Duration.ofDays(9999));
                } else if (freeze.getFreezingRange() == 3) {
                    freeze1.setFreezingRange(3);
                    stringRedisTemplate.opsForValue().set("freezeDongTai" + freeze.getUserId(), "3", Duration.ofDays(9999));
                }
            }
            //打印冻结原因
            System.out.println(freeze.getReasonsForFreezing());
            //存储原因
            freeze1.setReasonsForFreezing(freeze.getReasonsForFreezing());
            //存储备注
            freeze1.setFrozenRemarks(freeze.getFrozenRemarks());

            freezeService.updateFreeze(freeze1);
        }
        Map<String, String> map = new HashMap<>();
        map.put("message", "mock");
        return map;

    }
    //解冻用户
    public Map<String, String> unfreeze(Integer userId,String reasonsForThawing) {
        //删除redis
        stringRedisTemplate.delete("freezeDengLu"+userId);
        stringRedisTemplate.delete("freezeDongTai"+userId);
        stringRedisTemplate.delete("freezeFayan"+userId);
        freezeService.update(reasonsForThawing,userId);

        return null;
    }

}
