package com.itheima.app.manager;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.itheima.app.interceptor.UserHolder;
import com.itheima.autoconfig.oss.OssTemplate;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.mongo.Comment;
import com.itheima.domain.mongo.Movement;
import com.itheima.service.db.UserInfoService;
import com.itheima.service.mongo.CommentService;
import com.itheima.service.mongo.MovementService;
import com.itheima.util.ConstantUtil;
import com.itheima.util.DateFormatUtil;
import com.itheima.vo.CommentVo;
import com.itheima.vo.MovementVo;
import com.itheima.vo.PageBeanVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class MovementManager {

    @Autowired
    private MQMovementManager mqMovementManager;

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Autowired
    private OssTemplate ossTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Reference
    private MovementService movementService;

    @Reference
    private UserInfoService userInfoService;

    @Reference
    private CommentService commentService;

    //发布动态
    public void saveMovement(Movement movement, MultipartFile[] imageContent) throws IOException {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        String s = stringRedisTemplate.opsForValue().get("freezeDongTai" + userId);
        if (s !=null) {
            return;
        }

        //2. 图片上传到阿里云,得到返回地址的集合
        ArrayList<String> imageList = new ArrayList<>();
        if (imageContent != null && imageContent.length > 0) {
            for (MultipartFile image : imageContent) {
                String imagePath = ossTemplate.upload(image.getOriginalFilename(), image.getInputStream());
                imageList.add(imagePath);
            }
        }

        //3. 封装一个Movement对象
        movement.setId(ObjectId.get());//自己设置一个动态id

        movement.setUserId(userId);//用户id
        //movement.setPid(0L);//todo 临时写死,一会处理
        movement.setMedias(imageList);//图片列表
        movement.setState(0);//todo 暂时写死,等到后期改查华为云审核
        movement.setCreated(System.currentTimeMillis());//创建时间
        movement.setSeeType(1);//本项目未使用

        //4. 调用service保存
        movementService.saveMovement(movement);

        //向mq投递消息
        rocketMQTemplate.convertAndSend("tanhua-movement", movement.getId().toHexString());

        //向mq投递消息(大数据推荐使用)
        mqMovementManager.sendMsg(movement.getId().toHexString(), MQMovementManager.MOVEMENT_PUBLISH);
    }

    //查询我的动态
    public PageBeanVo findMyMovement(Long userId, Integer pageNum, Integer pageSize) {
        //1. 调用service查询
        PageBeanVo pageBeanVo = movementService.findMyMovement(userId, pageNum, pageSize);

        //2. 获取到movement的集合
        List<Movement> movementList = (List<Movement>) pageBeanVo.getItems();

        //3. 遍历集合
        List<MovementVo> movementVoList = new ArrayList<>();

        if (CollectionUtil.isNotEmpty(movementList)) {
            for (Movement movement : movementList) {
                //0. 查询到动态发布人的用户信息
                UserInfo userInfo = userInfoService.findById(movement.getUserId());

                //1. 创建一个movementVo
                MovementVo movementVo = new MovementVo();

                //2. 填充movementVo个人信息部分
                movementVo.setUserInfo(userInfo);

                //3. 填充movementVo动态信息部分
                movementVo.setMovement(movement);

                //4. 设置当前动态的点赞状态
                if (stringRedisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LIKE, UserHolder.get().getId(), movementVo.getId()))) {
                    movementVo.setHasLiked(1);
                }

                //5. 收集处理完毕的动态信息
                movementVoList.add(movementVo);
            }
        }

        //4. 替换pageBeanVo中的items部分
        pageBeanVo.setItems(movementVoList);
        return pageBeanVo;
    }

    //查询我的好友的动态
    public PageBeanVo findMyFriendMovement(Integer pageNum, Integer pageSize) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 查询我的好友的动态列表
        PageBeanVo pageBeanVo = movementService.findMyFriendMovement(userId, pageNum, pageSize);

        //3. 结果集封装
        List<Movement> movementList = (List<Movement>) pageBeanVo.getItems();
        List<MovementVo> movementVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(movementList)) {
            for (Movement movement : movementList) {
                //0. 查询到动态发布人的用户信息
                UserInfo userInfo = userInfoService.findById(movement.getUserId());

                //1. 创建一个movementVo
                MovementVo movementVo = new MovementVo();

                //2. 填充movementVo个人信息部分
                movementVo.setUserInfo(userInfo);

                //3. 填充movementVo动态信息部分
                movementVo.setMovement(movement);

                //4. 设置当前动态的点赞状态
                if (stringRedisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LIKE, UserHolder.get().getId(), movementVo.getId()))) {
                    movementVo.setHasLiked(1);
                }

                //5. 收集处理完毕的动态信息
                movementVoList.add(movementVo);
            }
        }

        //4. 替换pageBeanVo中的items部分
        pageBeanVo.setItems(movementVoList);
        return pageBeanVo;
    }

    //查询推荐给我的动态
    public PageBeanVo findRecommendMovement(Integer pageNum, Integer pageSize) {
        //1. 获取登录用户id
        Long userId = UserHolder.get().getId();

        //2. 根据id调用service查询动态数据化
        PageBeanVo pageBeanVo = movementService.findRecommendMovement(userId, pageNum, pageSize);

        //3. 组装返回数据
        List<Movement> movementList = (List<Movement>) pageBeanVo.getItems();
        List<MovementVo> movementVoList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(movementList)) {
            for (Movement movement : movementList) {
                //0. 查询到动态发布人的用户信息
                UserInfo userInfo = userInfoService.findById(movement.getUserId());

                //1. 创建一个movementVo
                MovementVo movementVo = new MovementVo();

                //2. 填充movementVo个人信息部分
                movementVo.setUserInfo(userInfo);

                //3. 填充movementVo动态信息部分
                movementVo.setMovement(movement);

                //4. 设置当前动态的点赞状态
                if (stringRedisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LIKE, UserHolder.get().getId(), movementVo.getId()))) {
                    movementVo.setHasLiked(1);
                }

                //5. 设置当前动态的点赞状态
                if (stringRedisTemplate.hasKey(StrUtil.format(ConstantUtil.MOVEMENT_LOVE, UserHolder.get().getId(), movementVo.getId()))) {
                    movementVo.setHasLoved(1);
                }

                //6. 收集处理完毕的动态信息
                movementVoList.add(movementVo);
            }
        }
        pageBeanVo.setItems(movementVoList);
        return pageBeanVo;
    }

    //动态点赞
    public Integer saveMovementLike(String movementId) {
        //1. 组装comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(1);
        comment.setUserId(UserHolder.get().getId());

        //2. 调用service进行点赞
        Integer num = commentService.saveMovementComment(comment);

        //3. 向redis中保存点赞标识
        stringRedisTemplate.opsForValue().set(StrUtil.format(ConstantUtil.MOVEMENT_LIKE, UserHolder.get().getId(), movementId), "1");


        //向mq投递消息(大数据推荐使用)
        mqMovementManager.sendMsg(movementId, MQMovementManager.MOVEMENT_LIKE);

        //4. 返回点赞数量
        return num;
    }

    //动态取消点赞
    public Integer deleteMovementLike(String movementId) {
        //1. 组装comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(1);
        comment.setUserId(UserHolder.get().getId());

        //2. 调用service进行取消点赞
        Integer num = commentService.deleteMovementComment(comment);

        //3. 从redis中删除点赞标识
        stringRedisTemplate.delete(StrUtil.format(ConstantUtil.MOVEMENT_LIKE, UserHolder.get().getId(), movementId));


        //向mq投递消息(大数据推荐使用)
        mqMovementManager.sendMsg(movementId, MQMovementManager.MOVEMENT_DISLIKE);

        //4. 返回点赞数量
        return num;
    }

    //动态喜欢
    public Integer saveMovementLove(String movementId) {
        //1. 组装comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(3);
        comment.setUserId(UserHolder.get().getId());

        //2. 调用service进行点赞
        Integer num = commentService.saveMovementComment(comment);

        //3. 向redis中保存点赞标识
        stringRedisTemplate.opsForValue().set(StrUtil.format(ConstantUtil.MOVEMENT_LOVE, UserHolder.get().getId(), movementId), "1");

        //向mq投递消息(大数据推荐使用)
        mqMovementManager.sendMsg(movementId, MQMovementManager.MOVEMENT_LOVE);

        //4. 返回点赞数量
        return num;
    }

    //动态取消喜欢
    public Integer deleteMovementLove(String movementId) {
        //1. 组装comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(3);
        comment.setUserId(UserHolder.get().getId());

        //2. 调用service进行取消点赞
        Integer num = commentService.deleteMovementComment(comment);

        //3. 从redis中删除点赞标识
        stringRedisTemplate.delete(StrUtil.format(ConstantUtil.MOVEMENT_LOVE, UserHolder.get().getId(), movementId));

        //向mq投递消息(大数据推荐使用)
        mqMovementManager.sendMsg(movementId, MQMovementManager.MOVEMENT_DISLOVE);

        //4. 返回点赞数量
        return num;
    }

    //根据id查询动态信息
    public MovementVo findMovementById(String movementId) {
        //1. 根据id查询动态信息
        Movement movement = movementService.findMovementById(movementId);

        //2. 根据动态的userId查询用户信息
        UserInfo userInfo = userInfoService.findById(movement.getUserId());

        //3. 组装返回结果
        MovementVo movementVo = new MovementVo();
        //注意赋值的时候,这个顺序是不能调换的
        movementVo.setUserInfo(userInfo);
        movementVo.setMovement(movement);

        //向mq投递消息(大数据推荐使用)
        mqMovementManager.sendMsg(movementId, MQMovementManager.MOVEMENT_BROWSE);

        return movementVo;
    }

    //根据动态id,分页查询评论列表
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

    //保存评论
    public void saveMovementComment(String movementId, String commentContent) {

        if (stringRedisTemplate.opsForValue().get("freezeFayan" + UserHolder.get().getId())!=null) {
            return ;
        }

        //1. 封装comment对象
        Comment comment = new Comment();
        comment.setPublishId(new ObjectId(movementId));
        comment.setCommentType(2);
        comment.setUserId(UserHolder.get().getId());
        comment.setContent(commentContent);

        //向mq投递消息(大数据推荐使用)
        mqMovementManager.sendMsg(movementId, MQMovementManager.MOVEMENT_COMMENT);

        //2. 调用service保存评论
        commentService.saveMovementComment(comment);
    }

    //姚远
    //【实战】动态评论点赞
    public Integer saveMovementCommentLike(Long userId, String commentId) {
        //1. 封装comment对象
        Comment comment = new Comment();
        comment.setUserId(UserHolder.get().getId());
        comment.setCommentType(6);
        comment.setParentId(new ObjectId(commentId));

        //2.调用service进行点赞
        Integer num= commentService.saveMovementCommentLike(comment);
        //3.向redis中保存点赞标识
        stringRedisTemplate.opsForValue().set(StrUtil.format(ConstantUtil.COMMENT_LIKE, UserHolder.get().getId(), commentId), "1");
        //4.返回点赞数
        return num;
    }

    //【实战】取消动态评论点赞
    public Integer deleteMovementCommentLike(Long id, String commentId) {
        //1. 封装comment对象
        Comment comment = new Comment();
        comment.setUserId(UserHolder.get().getId());
        comment.setCommentType(6);
        comment.setParentId(new ObjectId(commentId));
        //2.调用service进行取消点赞
        Integer num= commentService.deleteMovementCommentLike(comment);
        //3. 从redis中删除点赞标识
        stringRedisTemplate.delete(StrUtil.format(ConstantUtil.COMMENT_LIKE, UserHolder.get().getId(), commentId));
        //4.返回点赞数
        return num;
    }
}