package org.example.controller;

import org.example.dto.BusinessReportRequest;
import org.example.dto.BusinessReportSummary;
import org.example.service.BusinessReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/report/business")
@Tag(name = "业务报表统计", description = "业务报表统计分析接口")
public class BusinessReportController {

    private final BusinessReportService businessReportService;

    public BusinessReportController(BusinessReportService businessReportService) {
        this.businessReportService = businessReportService;
    }

    @PostMapping
    @Operation(summary = "获取业务报表统计数据")
    public BusinessReportSummary getBusinessReport(
            @RequestBody BusinessReportRequest request) {
        return businessReportService.getBusinessReport(request);
    }
}