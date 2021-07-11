package com.itheima.service.db.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.domain.db.UserInfo;
import com.itheima.domain.db.Freeze;
import com.itheima.mapper.FreezeMapper;
import com.itheima.service.db.FreezeService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@Service
public class FreezeServiceImpl implements FreezeService {

    @Autowired
    private FreezeMapper freezeMapper;

    @Override
    public Freeze findFreezeByUserId(Integer userId) {
        QueryWrapper<Freeze> w = new QueryWrapper<>();
        w.eq("user_id",userId);
        Freeze freeze = freezeMapper.selectOne(w);
        return freeze;
    }

    @Override
    public void saveFreeze(Freeze freeze1) {


            freezeMapper.insert(freeze1);
    }

    @Override
    public void updateFreeze(Freeze freeze) {

            QueryWrapper<Freeze> freezeQueryWrapper = new QueryWrapper<>();
            freezeQueryWrapper.eq("id",freeze.getId());
            freezeMapper.update(freeze, freezeQueryWrapper);

    }

    @Override
    public void update(String reasonsForThawing, Integer userId) {
        QueryWrapper<Freeze> wrapper = new QueryWrapper<Freeze>();
        wrapper.eq("user_id", userId);
        Freeze freeze = new Freeze();
        freeze.setReasonsForThawing(reasonsForThawing);
        freezeMapper.update(freeze, wrapper);
    }

}
