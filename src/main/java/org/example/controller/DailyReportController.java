package org.example.controller;

import org.example.dto.DailyReportRequest;
import org.example.dto.DailyReportSummary;
import org.example.service.DailyReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report/daily")
@Tag(name = "日报表统计", description = "日报表统计分析接口")
public class DailyReportController {

    private final DailyReportService dailyReportService;

    public DailyReportController(DailyReportService dailyReportService) {
        this.dailyReportService = dailyReportService;
    }

    @PostMapping
    @Operation(summary = "获取日报表统计数据")
    public DailyReportSummary getDailyReport(
            @RequestBody DailyReportRequest request) {
        return dailyReportService.getDailyReport(request);
    }
}