package com.itheima.service.mongo;

import com.itheima.domain.mongo.FocusUser;

public interface FocusUserService {

    //添加关注
    void save(FocusUser focusUser);

    //删除关注
    void delete(FocusUser focusUser);
}
