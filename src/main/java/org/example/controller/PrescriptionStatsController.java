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
    @Operation(summary = "获取处方统计数据")
    public PrescriptionStatsSummary getPrescriptionStats(
            @RequestBody PrescriptionStatsRequest request) {
        return statsService.getPrescriptionStats(request);
    }
}