package org.example.controller;

import org.example.dto.RegistrationStatsRequest;
import org.example.dto.RegistrationStatsSummary;
import org.example.service.RegistrationStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/stats/registration")
@Tag(name = "挂号统计", description = "挂号数据统计分析接口")
public class RegistrationStatsController {

    private final RegistrationStatsService statsService;

    public RegistrationStatsController(RegistrationStatsService statsService) {
        this.statsService = statsService;
    }

    @PostMapping
    @Operation(summary = "获取挂号统计数据")
    public RegistrationStatsSummary getRegistrationStats(
            @RequestBody RegistrationStatsRequest request) {
        return statsService.getRegistrationStats(request);
    }
}