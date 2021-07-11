package com.itheima.service.mongo.impl;

import com.itheima.domain.mongo.Comment;
import com.itheima.domain.mongo.Movement;
import com.itheima.domain.mongo.Video;
import com.itheima.service.mongo.CommentService;
import com.itheima.vo.PageBeanVo;
import org.apache.dubbo.config.annotation.Service;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Integer saveMovementComment(Comment comment) {
        //1. 设置comment属性(缺失publishId commentType userId content)
        comment.setCreated(System.currentTimeMillis());
        comment.setPublishUserId(mongoTemplate.findById(comment.getPublishId(), Movement.class).getUserId());

        //2. 向评论表中添加一条评论
        mongoTemplate.save(comment);

        //3. 向动态表中更新操作次数
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        if (comment.getCommentType() == 1) {//点赞
            movement.setLikeCount(movement.getLikeCount() + 1);
        } else if (comment.getCommentType() == 2) {//语言评论
            movement.setCommentCount(movement.getCommentCount() + 1);
        } else if (comment.getCommentType() == 3) {//喜欢
            movement.setLoveCount(movement.getLoveCount() + 1);
        }
        mongoTemplate.save(movement);//按照主键更新字段

        //4. 返回操作次数
        if (comment.getCommentType() == 1) {//点赞
            return movement.getLikeCount();
        } else if (comment.getCommentType() == 2) {//语言评论
            return movement.getCommentCount();
        } else if (comment.getCommentType() == 3) {//喜欢
            return movement.getLoveCount();
        }

        return 0;
    }

    @Override
    public Integer deleteMovementComment(Comment comment) {
        //1. 组装删除条件(哪个用户对哪条动态的什么操作)
        Query query = new Query(
                Criteria.where("userId").is(comment.getUserId())
                        .and("publishId").is(comment.getPublishId())
                        .and("commentType").is(comment.getCommentType())
        );

        //2. 向评论表中删除一条评论
        mongoTemplate.remove(query, Comment.class);

        //3. 向动态表中更新操作次数
        Movement movement = mongoTemplate.findById(comment.getPublishId(), Movement.class);
        if (comment.getCommentType() == 1) {//点赞
            movement.setLikeCount(movement.getLikeCount() - 1);
        } else if (comment.getCommentType() == 2) {//语言评论
            movement.setCommentCount(movement.getCommentCount() - 1);
        } else if (comment.getCommentType() == 3) {//喜欢
            movement.setLoveCount(movement.getLoveCount() - 1);
        }
        mongoTemplate.save(movement);//按照主键更新字段

        //4. 返回操作次数
        if (comment.getCommentType() == 1) {//点赞
            return movement.getLikeCount();
        } else if (comment.getCommentType() == 2) {//语言评论
            return movement.getCommentCount();
        } else if (comment.getCommentType() == 3) {//喜欢
            return movement.getLoveCount();
        }

        return 0;
    }

    @Override
    public PageBeanVo findMovementComment(String movementId, Integer pageNum, Integer pageSize) {
        //1. 组装查询条件
        Query query = new Query(
                Criteria.where("publishId").is(new ObjectId(movementId))
                        .and("commentType").is(2)
        ).with(Sort.by(Sort.Order.desc("created")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);

        //2. 统计总记录数
        long count = mongoTemplate.count(query, Comment.class);

        //3. 返回封装结果
        return new PageBeanVo(pageNum, pageSize, count, commentList);
    }

    @Override
    public PageBeanVo findCountComment(Long userId, Integer commentType, Integer pageNum, Integer pageSize) {
        //1. 构造查询条件
        Query query = new Query(
                Criteria.where("publishUserId").is(userId)
                        .and("commentType").is(commentType)
        ).with(Sort.by(Sort.Order.desc("created")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);

        //2. 查询
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);

        //3. 统计数量
        long count = mongoTemplate.count(query, Comment.class);

        //4. 返回
        return new PageBeanVo(pageNum,pageSize,count,commentList);
    }

    //

    @Override
    public void saveVideoComment(Comment comment) {
        //1. 设置comment属性(缺失publishId commentType userId content)
        comment.setCreated(System.currentTimeMillis());
        comment.setPublishUserId(mongoTemplate.findById(comment.getPublishId(), Video.class).getUserId());
        //2.向评论表中添加一条评论
        mongoTemplate.save(comment);
        //3.向动态表中更新操作次数
        Video video = mongoTemplate.findById(comment.getPublishId(), Video.class);
        if(comment.getCommentType()==4){//点赞
            video.setLikeCount(video.getLikeCount()+1);
        }else if(comment.getCommentType()==5){//评论
            video.setCommentCount(video.getCommentCount()+1);
        }
        mongoTemplate.save(video);//根据主键更新字段
    }

    @Override
    public void deleteVideoComment(Comment comment) {
        //1. 组装删除条件(哪个用户对哪个视频的什么操作)
        Query query = new Query(
                Criteria.where("userId").is(comment.getUserId())
                        .and("publishId").is(comment.getPublishId())
                        .and("commentType").is(comment.getCommentType())
        );

        //2. 向评论表中删除一条评论
        mongoTemplate.remove(query, Comment.class);

        //3. 向视频表中更新操作次数
        Video video = mongoTemplate.findById(comment.getPublishId(), Video.class);
        if (comment.getCommentType() ==4) {//视频点赞
            video.setLikeCount(video.getLikeCount() - 1);
        }
        mongoTemplate.save(video);//按照主键更新字段

    }

    //视频评论列表
    @Override
    public PageBeanVo findVideoComment(String videoId, Integer pageNum, Integer pageSize) {

        //1. 组装查询条件
        Query query = new Query(
                Criteria.where("publishId").is(new ObjectId(videoId))
                        .and("commentType").is(5)
        ).with(Sort.by(Sort.Order.desc("created")))
                .skip((pageNum - 1) * pageSize).limit(pageSize);
        List<Comment> commentList = mongoTemplate.find(query, Comment.class);

        //2. 统计总记录数
        long count = mongoTemplate.count(query, Comment.class);
        return new PageBeanVo(pageNum,pageSize,count,commentList);

    }

    @Override
    public void saveVideoCommentLike(Comment comment) {

        //1. 设置comment属性(缺失parentId commentType userId )
        comment.setCreated(System.currentTimeMillis());
        //2.向评论表中添加一条评论
        mongoTemplate.save(comment);
        //3.向评论表中更新父节点操作次数
        Comment parent = mongoTemplate.findById(comment.getParentId(), Comment.class);
        parent.setLikeCount(parent.getLikeCount()+1);
        parent.setIsParent(true);
        mongoTemplate.save(parent);//按照主键更新字段
    }

    @Override
    public void deleteVideoCommentLike(Comment comment) {

        //1. 组装删除条件(哪个用户对哪个视频的什么操作)
        Query query = new Query(
                Criteria.where("userId").is(comment.getUserId())
                        .and("parentId").is(comment.getParentId())
                        .and("commentType").is(comment.getCommentType())
        );

        //2. 向评论表中删除一条评论
        mongoTemplate.remove(query, Comment.class);

        //3. 向评论表中更新操作次数
        Comment parent = mongoTemplate.findById(comment.getParentId(), Comment.class);
        if (comment.getCommentType() ==6) {//视频评论点赞
            parent.setLikeCount(parent.getLikeCount() - 1);
        }
        mongoTemplate.save(parent);//按照主键更新字段
    }

    @Override
    public Integer saveMovementCommentLike(Comment comment) {
        //1.设置comment属性【缺少parentId,commentType userId】
        comment.setCreated(System.currentTimeMillis());
        //2.向评论表中添加一条评论
        mongoTemplate.save(comment);
        //3.向评论表中更新父节点操作次数
        Comment parent = mongoTemplate.findById(comment.getParentId(),Comment.class);
        parent.setLikeCount(parent.getLikeCount() + 1);
        parent.setIsParent(true);
        mongoTemplate.save(parent);//按照主键更新字段
        return parent.getLikeCount();
    }

    @SuppressWarnings("all")
    @Override
    public Integer deleteMovementCommentLike(Comment comment) {
        //1. 组装删除条件(哪个用户对哪个视频的什么操作)
        Query query = new Query(
                Criteria.where("userId").is(comment.getUserId())
                        .and("parentId").is(comment.getParentId())
                        .and("commentType").is(comment.getCommentType())
        );

        //2. 向评论表中删除一条评论
        mongoTemplate.remove(query, Comment.class);

        //3. 向评论表中更新操作次数
        Comment parent = mongoTemplate.findById(comment.getParentId(), Comment.class);
        if (comment.getCommentType() ==6) {//视频评论点赞
            parent.setLikeCount(parent.getLikeCount() - 1);
        }
        mongoTemplate.save(parent);//按照主键更新字段
        return parent.getLikeCount();
    }

}
