package com.itheima.service.db.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.itheima.domain.db.AnalysisByDay;
import com.itheima.mapper.AnalysisByDayMapper;
import com.itheima.mapper.LogMapper;
import com.itheima.service.db.AnalysisByDayService;
import com.itheima.util.ComputeUtil;
import com.itheima.vo.AnalysisSummaryVo;
import com.itheima.vo.AnalysisTable;
import com.itheima.vo.AnalysisTableVo;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class AnalysisByDayServiceImpl implements AnalysisByDayService {

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private AnalysisByDayMapper analysisByDayMapper;


    @Override
    public void analysis() {
        String today = DateUtil.offsetDay(new Date(), 0).toDateStr();
        String yesterday = DateUtil.offsetDay(new Date(), -1).toDateStr();

        //1. 从log表中进行查询
        Integer numRegistered = logMapper.countByTypeAndTime("0102", today);//新注册用户数
        Integer numActive = logMapper.countByTime(today);//活跃用户数
        Integer numLogin = logMapper.countByTypeAndTime("0101", today);//登录次数
        Integer numRetention1d = logMapper.keep(yesterday, today);//次日留存用户数

        //2. 向tb_analysis_by_day保存数据
        //2-1 先根据今天日期查询一下是否存在了记录
        QueryWrapper<AnalysisByDay> wrapper = new QueryWrapper<>();
        wrapper.eq("record_date", today);
        AnalysisByDay analysisByDay = analysisByDayMapper.selectOne(wrapper);

        if (analysisByDay == null) {
            //2-2 如果没有,新增
            analysisByDay = new AnalysisByDay();
            analysisByDay.setRecordDate(new Date());
            analysisByDay.setNumRegistered(numRegistered);
            analysisByDay.setNumActive(numActive);
            analysisByDay.setNumLogin(numLogin);
            analysisByDay.setNumRetention1d(numRetention1d);

            analysisByDayMapper.insert(analysisByDay);
        } else {
            //2-3 如果有了,修改
            analysisByDay.setRecordDate(new Date());
            analysisByDay.setNumRegistered(numRegistered);
            analysisByDay.setNumActive(numActive);
            analysisByDay.setNumLogin(numLogin);
            analysisByDay.setNumRetention1d(numRetention1d);

            analysisByDayMapper.updateById(analysisByDay);
        }
    }

    @Override
    public AnalysisSummaryVo summary() {
        //1. 准备基础数据
        //1-1 定义日期
        String today = DateUtil.offsetDay(new Date(), 0).toDateStr();
        String yesterday = DateUtil.offsetDay(new Date(), -1).toDateStr();
        String today_7 = DateUtil.offsetDay(new Date(), -7).toDateStr();
        String today_30 = DateUtil.offsetDay(new Date(), -30).toDateStr();

        //1-2 查询今天的数据
        QueryWrapper<AnalysisByDay> wrapper1 = new QueryWrapper<>();
        wrapper1.eq("record_date", today);
        AnalysisByDay todayData = analysisByDayMapper.selectOne(wrapper1);

        //1-3 查询昨天天的数据
        QueryWrapper<AnalysisByDay> wrapper2 = new QueryWrapper<>();
        wrapper2.eq("record_date", yesterday);
        AnalysisByDay yesterdayData = analysisByDayMapper.selectOne(wrapper2);


        //2. 凑齐9个数
        //累计用户数(分析表中sum(num_registered))
        Long cumulativeUsers = analysisByDayMapper.findRegisterNum();

        //过去30天活跃用户数(30-今天之内的活跃用户数加和)
        Long activePassMonth = analysisByDayMapper.findActiveNum(today_30, today);

        //过去7天活跃用户
        Long activePassWeek = analysisByDayMapper.findActiveNum(today_7, today);


        //今日新增用户数量
        Long newUsersToday = todayData.getNumRegistered().longValue();

        //今日新增用户涨跌率，单位百分数，正数为涨，负数为跌   BigDecimal : 商业数字格式
        BigDecimal newUsersTodayRate
                = ComputeUtil.computeRate(todayData.getNumRegistered(), yesterdayData.getNumRegistered());

        //今日登录次数
        Long loginTimesToday = todayData.getNumLogin().longValue();

        //今日登录次数涨跌率，单位百分数，正数为涨，负数为跌
        BigDecimal loginTimesTodayRate = ComputeUtil.computeRate(todayData.getNumLogin(), yesterdayData.getNumLogin());

        //今日活跃用户数量
        Long activeUsersToday = todayData.getNumActive().longValue();

        //今日活跃用户涨跌率，单位百分数，正数为涨，负数为跌
        BigDecimal activeUsersTodayRate = ComputeUtil.computeRate(todayData.getNumActive(), yesterdayData.getNumActive());

        //3. 组装vo
        AnalysisSummaryVo analysisSummaryVo = new AnalysisSummaryVo();
        analysisSummaryVo.setCumulativeUsers(cumulativeUsers);
        analysisSummaryVo.setActivePassMonth(activePassMonth);
        analysisSummaryVo.setActivePassWeek(activePassWeek);
        analysisSummaryVo.setNewUsersToday(newUsersToday);
        analysisSummaryVo.setNewUsersTodayRate(newUsersTodayRate);
        analysisSummaryVo.setLoginTimesToday(loginTimesToday);
        analysisSummaryVo.setLoginTimesTodayRate(loginTimesTodayRate);
        analysisSummaryVo.setActiveUsersToday(activeUsersToday);
        analysisSummaryVo.setActiveUsersTodayRate(activeUsersTodayRate);

        return analysisSummaryVo;
    }

    @Override
    public AnalysisTableVo summaryTable(Long sd, Long ed, String type) {

        //设置今年时间
        DateTime nowStartTime = DateUtil.offsetDay(new Date(sd), 0); //今年开始时间
        DateTime nowEndTime = DateUtil.offsetDay(new Date(ed), 0);  //今年结束时间

        //设置去年时间
        DateTime LastStartTime = DateUtil.offset(nowStartTime, DateField.YEAR, -1);//去年开始时间
        DateTime LastEndTime = DateUtil.offset(nowEndTime, DateField.YEAR, -1); //去年结束时间


        //根据时间从库中查询今年的
        List<AnalysisByDay> thisYear = analysisByDayMapper.aSpanNum(nowStartTime.toDateStr(), nowEndTime.toDateStr());
        //创建一个用来存储今年数据的空集合
        List<AnalysisTable> nowAts = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(thisYear)) {
            for (AnalysisByDay analysisByDay : thisYear) {
                //新增 101
                if (type != null && type.equals("101")) {
                    //获取日期
                    String recordDate = DateUtil.offsetDay(analysisByDay.getRecordDate(), 0).toDateStr();
                    //获取新增数据
                    Integer numRegistered = analysisByDay.getNumRegistered();

                    //存入表中
                    AnalysisTable analysisTable = new AnalysisTable();
                    analysisTable.setTitle(recordDate);
                    analysisTable.setAmount(numRegistered);

                    //放入集合
                    nowAts.add(analysisTable);

                    //活跃用户 102
                } else if (type != null && type.equals("102")) {
                    //获取日期
                    String recordDate = DateUtil.offsetDay(analysisByDay.getRecordDate(), 0).toDateStr();
                    //获取新增数据
                    Integer numActive = analysisByDay.getNumActive();

                    //存入对象
                    AnalysisTable analysisTable = new AnalysisTable();
                    analysisTable.setTitle(recordDate);
                    analysisTable.setAmount(numActive);

                    //放入集合
                    nowAts.add(analysisTable);


                    //次日留存率 103
                } else {
                    //获取日期
                    String recordDate = DateUtil.offsetDay(analysisByDay.getRecordDate(), 0).toDateStr();
                    //获取新增数据
                    Integer numRetention1d = analysisByDay.getNumRetention1d();

                    //存入表中
                    AnalysisTable analysisTable = new AnalysisTable();
                    analysisTable.setTitle(recordDate);
                    analysisTable.setAmount(numRetention1d);

                    //放入集合
                    nowAts.add(analysisTable);

                }
            }
        }



        //根据时间从库中查询去年的
        List<AnalysisByDay> lastYear = analysisByDayMapper.aSpanNum(LastStartTime.toDateStr(), LastEndTime.toDateStr());
        //创建一个用来存储今年数据的空集合
        List<AnalysisTable> lastAts = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(thisYear)) {
            for (AnalysisByDay analysisByDay : thisYear) {
                //新增
                if (type != null && type.equals("101")) {
                    //获取日期
                    String recordDate = DateUtil.offsetDay(analysisByDay.getRecordDate(), 0).toDateStr();
                    //获取新增数据
                    Integer numRegistered = analysisByDay.getNumRegistered();

                    //存入表中
                    AnalysisTable analysisTable = new AnalysisTable();
                    analysisTable.setTitle(recordDate);
                    analysisTable.setAmount(numRegistered);

                    //放入集合
                    lastAts.add(analysisTable);

                    //活跃用户 102
                } else if (type != null && type.equals("102")) {
                    //获取日期
                    String recordDate = DateUtil.offsetDay(analysisByDay.getRecordDate(), 0).toDateStr();
                    //获取新增数据
                    Integer numActive = analysisByDay.getNumActive();

                    //存入表中
                    AnalysisTable analysisTable = new AnalysisTable();
                    analysisTable.setTitle(recordDate);
                    analysisTable.setAmount(numActive);

                    //放入集合
                    lastAts.add(analysisTable);

                    //次日留存率 103
                } else {
                    //获取日期
                    String recordDate = DateUtil.offsetDay(analysisByDay.getRecordDate(), 0).toDateStr();
                    Integer numRetention1d = analysisByDay.getNumRetention1d();
                    //存入表中
                    AnalysisTable analysisTable = new AnalysisTable();
                    analysisTable.setTitle(recordDate);
                    analysisTable.setAmount(numRetention1d);

                    //放入集合
                    lastAts.add(analysisTable);

                }
            }
        }

        //组装返回数据
        AnalysisTableVo analysisTableVo = new AnalysisTableVo();
        analysisTableVo.setThisYear(nowAts);
        analysisTableVo.setLastYear(lastAts);
        return analysisTableVo;
    }


}
