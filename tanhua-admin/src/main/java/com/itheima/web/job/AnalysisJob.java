package com.itheima.web.job;

import com.itheima.service.db.AnalysisByDayService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class AnalysisJob {

    @Reference
    private AnalysisByDayService analysisByDayService;

    //@Scheduled(cron = "0 0 0/1 * * ?")
    @Scheduled(cron = "0 0/1 * * * ?")
    public void analysis() {
        System.out.println("开始日志分析了~~~~~~~~~~~~~~");

        analysisByDayService.analysis();
    }
}
