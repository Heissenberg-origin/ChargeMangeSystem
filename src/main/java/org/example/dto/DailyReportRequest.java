package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * DailyReportRequest 类用于封装日报表统计请求的参数。
 */
@Data
@Schema(description = "日报表统计请求参数")
public class DailyReportRequest {

    @Schema(description = "开始日期", required = true, example = "2025-06-01")
    private Date startDate;  // 报表统计的开始日期，必须提供，格式为 YYYY-MM-DD

    @Schema(description = "结束日期", required = true, example = "2025-06-30")
    private Date endDate;    // 报表统计的结束日期，必须提供，格式为 YYYY-MM-DD
}