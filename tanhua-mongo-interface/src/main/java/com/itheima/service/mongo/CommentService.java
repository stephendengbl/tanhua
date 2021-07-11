package com.itheima.service.mongo;

import com.itheima.domain.mongo.Comment;
import com.itheima.vo.PageBeanVo;

public interface CommentService {

    //保存动态评论(返回操作之后的次数)
    Integer saveMovementComment(Comment comment);

    //删除动态评论(返回操作之后的次数)
    Integer deleteMovementComment(Comment comment);

    //分页查询指定动态的评论列表
    PageBeanVo findMovementComment(String movementId, Integer pageNum, Integer pageSize);

    //根据被操作人id和操作类型查询
    PageBeanVo findCountComment(Long userId,Integer commentType,Integer pageNum, Integer pageSize);


    //
    //视频点赞【实战】
    void saveVideoComment(Comment comment);

    //视频点赞取消【实战】
    void deleteVideoComment(Comment comment);

    //【实战】视频评论列表
    PageBeanVo findVideoComment(String videoId, Integer pageNum, Integer pageSize);

    //【实战】视频评论点赞
    void saveVideoCommentLike(Comment comment);

    //【实战】取消视频评论点赞
    void deleteVideoCommentLike(Comment comment);

    //【实战】动态评论点赞
    Integer saveMovementCommentLike(Comment comment);

    //【实战】取消动态评论点赞
    Integer deleteMovementCommentLike(Comment comment);
}
