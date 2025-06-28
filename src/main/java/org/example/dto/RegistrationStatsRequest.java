package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "挂号统计请求参数")
public class RegistrationStatsRequest {
    @Schema(description = "开始时间", required = true, example = "2023-01-01")
    private Date startTime;

    @Schema(description = "结束时间", required = true, example = "2023-12-31")
    private Date endTime;

    @Schema(description = "时间类型(day/week/month)", required = true, example = "day")
    private String timeType;

    @Schema(description = "统计方式(department/doctor)", required = true, example = "department")
    private String groupBy;
}