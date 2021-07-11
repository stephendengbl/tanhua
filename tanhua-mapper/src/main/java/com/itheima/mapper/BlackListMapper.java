package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.itheima.domain.db.BlackList;
import com.itheima.domain.db.UserInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

// 黑名单
public interface BlackListMapper extends BaseMapper<BlackList> {

    //根据当前用户id查询其黑名单用户信息(分页)
    @Select("SELECT tui.* FROM `tb_black_list` tbl ,`tb_user_info` tui WHERE tbl.`black_user_id` = tui.`id` AND tbl.`user_id` = #{userId}")
    IPage<UserInfo> findBlackUserByUserId(IPage page, @Param("userId") Long userId);
}