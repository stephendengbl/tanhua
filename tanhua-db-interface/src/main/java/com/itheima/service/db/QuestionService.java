package com.itheima.service.db;

import com.itheima.domain.db.Question;

public interface QuestionService {
    //根据用户id查询
    Question findByUserId(Long userId);

    //保存陌生人问题
    void save(Question question);

    //修改陌生人问题
    void update(Question question);
}
