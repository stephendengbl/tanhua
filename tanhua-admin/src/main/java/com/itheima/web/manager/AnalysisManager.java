package com.itheima.web.manager;

import com.itheima.service.db.AnalysisByDayService;
import com.itheima.vo.AnalysisSummaryVo;
import com.itheima.vo.AnalysisTableVo;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Service;

@Service
public class AnalysisManager {

    @Reference
    private AnalysisByDayService analysisByDayService;


    //概要统计
    public AnalysisSummaryVo summary() {
        return analysisByDayService.summary();
    }


    //概要统计表格版
    public AnalysisTableVo summaryTable(Long sd, Long ed, String type) {

        return analysisByDayService.summaryTable(sd, ed, type);
    }
}
