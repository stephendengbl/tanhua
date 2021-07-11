package com.itheima.service.db;

import com.itheima.domain.db.Admin;

public interface AdminService {

    //根据用户名查询
    Admin findByUsername(String username);
}
