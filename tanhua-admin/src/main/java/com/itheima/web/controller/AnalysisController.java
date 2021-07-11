package com.itheima.web.controller;

import com.itheima.vo.AnalysisSummaryVo;
import com.itheima.vo.AnalysisTableVo;
import com.itheima.web.manager.AnalysisManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnalysisController {

    @Autowired
    private AnalysisManager analysisManager;

    //概要统计
    @GetMapping("/dashboard/summary")
    public AnalysisSummaryVo summary(){
        return analysisManager.summary();
    }
    //概要统计表格版
    @GetMapping("/dashboard/users")
    public AnalysisTableVo summaryTable(Long sd, Long ed, String type) {

        System.out.println(analysisManager.summaryTable(sd, ed, type));

        return analysisManager.summaryTable(sd, ed, type);
    }
}
