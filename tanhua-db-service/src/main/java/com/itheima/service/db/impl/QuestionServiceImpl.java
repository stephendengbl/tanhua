package com.itheima.service.db.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.domain.db.Question;
import com.itheima.mapper.QuestionMapper;
import com.itheima.service.db.QuestionService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class QuestionServiceImpl implements QuestionService {

    @Autowired
    private QuestionMapper questionMapper;

    @Override
    public Question findByUserId(Long userId) {

        QueryWrapper<Question> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);

        return questionMapper.selectOne(wrapper);
    }

    @Override
    public void save(Question question) {
        questionMapper.insert(question);
    }

    @Override
    public void update(Question question) {
        questionMapper.updateById(question);
    }
}
