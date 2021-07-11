package com.itheima.service.mongo;

import com.itheima.domain.mongo.Visitor;

import java.util.List;

public interface VisitorService {

    //根据登录用户id查询其上传访问之后的访客
    List<Visitor> findVisitorList(Long userId, String lastAccessTime);
}
