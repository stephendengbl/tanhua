package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.domain.db.Log;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface LogMapper extends BaseMapper<Log> {

    //根据操作日期和类型统计
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE log_time=#{time} AND TYPE=#{type}")
    Integer countByTypeAndTime(@Param("type") String type, @Param("time") String time);

    //根据操作日期统计
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE log_time=#{time}")
    Integer countByTime(@Param("time") String time);

    //次日留存
    @Select("SELECT COUNT(DISTINCT user_id) FROM tb_log WHERE log_time=#{today} AND user_id IN(\n" +
            "\tSELECT user_id FROM tb_log WHERE log_time=#{yesterday} AND TYPE='0102')")
    Integer keep(@Param("yesterday") String yesterday, @Param("today") String today);
}