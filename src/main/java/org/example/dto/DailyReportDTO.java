package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "日报表统计结果")
public class DailyReportDTO {
    @Schema(description = "日期", example = "2025-06-01")
    private String reportDate;

    @Schema(description = "应收金额(挂号+处方)")
    private BigDecimal totalReceivable;

    @Schema(description = "实收金额(有效记录)")
    private BigDecimal actualReceived;

    @Schema(description = "差额(应收-实收)")
    private BigDecimal difference;

    @Schema(description = "退费金额(退号+退费)")
    private BigDecimal refundAmount;

    @Schema(description = "结算金额(实收-退费)")
    private BigDecimal settlementAmount;
}