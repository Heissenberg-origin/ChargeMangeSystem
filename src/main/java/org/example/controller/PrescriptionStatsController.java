// PrescriptionStatsController.java
package org.example.controller;

import org.example.dto.PrescriptionStatsRequest;
import org.example.dto.PrescriptionStatsSummary;
import org.example.service.PrescriptionStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stats/prescription")
@Tag(name = "处方统计", description = "处方数据统计分析接口")
public class PrescriptionStatsController {

    private final PrescriptionStatsService statsService;

    public PrescriptionStatsController(PrescriptionStatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping
    @Operation(summary = "获取处方统计数据",
            description = "统计已开方数、已开方金额、未收费处方数、未收费金额、未收费比例等指标。"
                    + "groupBy参数决定分组方式(department/doctor)。")
    public PrescriptionStatsSummary getPrescriptionStats(
            @RequestBody PrescriptionStatsRequest request) {
        return statsService.getPrescriptionStats(request);
    }
}