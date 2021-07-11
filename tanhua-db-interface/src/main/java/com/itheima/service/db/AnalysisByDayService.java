package com.itheima.service.db;

        import com.itheima.vo.AnalysisSummaryVo;
        import com.itheima.vo.AnalysisTableVo;

public interface AnalysisByDayService {

    //日志分析
    void analysis();

    //概要统计
    AnalysisSummaryVo summary();
    //概要统计表格版
    AnalysisTableVo summaryTable(Long sd, Long ed, String type);
}
