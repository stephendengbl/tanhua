package com.itheima.service.db;


import com.itheima.domain.db.Freeze;


public interface FreezeService {
    Freeze findFreezeByUserId(Integer userId);

    void saveFreeze(Freeze freeze1);

    void updateFreeze(Freeze freeze);

    void update(String reasonsForThawing, Integer userId);
    
}
