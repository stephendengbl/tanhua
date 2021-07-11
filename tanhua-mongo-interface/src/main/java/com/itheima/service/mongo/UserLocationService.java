package com.itheima.service.mongo;

import java.util.List;

public interface UserLocationService {

    //保存用户位置信息
    void save(Long userId, Double longitude, Double latitude, String address);

    //查询附近的人
    List<Long> findNearUserIdList(Long userId, Long distance);
}
