package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * PrescriptionStatsRequest 类用于封装处方统计请求的参数。
 */
@Data
@Schema(description = "处方统计请求参数")
public class PrescriptionStatsRequest {

    @Schema(description = "开始时间", required = true, example = "2023-01-01")
    private Date startTime;  // 统计的开始时间，必须提供，格式为 YYYY-MM-DD

    @Schema(description = "结束时间", required = true, example = "2023-12-31")
    private Date endTime;    // 统计的结束时间，必须提供，格式为 YYYY-MM-DD

    @Schema(description = "时间类型(day/week/month)", required = true, example = "day")
    private String timeType;  // 统计的时间类型，可以是 day、week 或 month

    @Schema(description = "统计方式(department/doctor)", required = true, example = "department")
    private String groupBy;    // 统计的分组方式，可以是 department（科室）或 doctor（医生）
}