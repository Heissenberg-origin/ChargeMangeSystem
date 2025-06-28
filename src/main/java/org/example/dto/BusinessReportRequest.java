package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@Schema(description = "业务报表统计请求参数")
public class BusinessReportRequest {
    @Schema(description = "开始日期", required = true, example = "2025-06-01")
    private Date startDate;

    @Schema(description = "结束日期", required = true, example = "2025-06-30")
    private Date endDate;
}