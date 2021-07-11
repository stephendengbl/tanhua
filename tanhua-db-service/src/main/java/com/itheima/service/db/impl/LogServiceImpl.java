package com.itheima.service.db.impl;

import com.itheima.domain.db.Log;
import com.itheima.mapper.LogMapper;
import com.itheima.service.db.LogService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public void save(Log log) {
        logMapper.insert(log);
    }
}
