package com.itheima.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.domain.db.AnalysisByDay;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface AnalysisByDayMapper extends BaseMapper<AnalysisByDay> {

    //所有注册用户之和
    @Select("SELECT SUM(num_registered) FROM `tb_analysis_by_day`")
    Long findRegisterNum();


    //统计一段时间内的活跃用户数之和
    @Select("SELECT SUM(num_active) FROM `tb_analysis_by_day` WHERE record_date BETWEEN #{begin}  AND #{end}")
    Long findActiveNum(@Param("begin") String begin, @Param("end") String end);

    //查询一段时间内的数据
    @Select("SELECT * FROM `tb_analysis_by_day` WHERE record_date BETWEEN #{begin} AND #{end}")
    List<AnalysisByDay> aSpanNum(@Param("begin") String begin, @Param("end") String end);
}