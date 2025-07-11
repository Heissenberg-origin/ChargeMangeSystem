package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * DailyReportDTO 类用于封装日报表的统计结果。
 */
@Data
@Schema(description = "日报表统计结果")
public class DailyReportDTO {

    @Schema(description = "日期", example = "2025-06-01")
    private String reportDate;  // 报表日期，格式为 YYYY-MM-DD

    @Schema(description = "应收金额(挂号+处方)")
    private BigDecimal totalReceivable;  // 应收的总金额，包括挂号费和处方费

    @Schema(description = "实收金额(有效记录)")
    private BigDecimal actualReceived;  // 实际收到的金额，基于有效记录

    @Schema(description = "差额(应收-实收)")
    private BigDecimal difference;  // 差额计算：应收金额减去实收金额

    @Schema(description = "退费金额(退号+退费)")
    private BigDecimal refundAmount;  // 退费的总金额，包括退号和其他退款

    @Schema(description = "结算金额(实收-退费)")
    private BigDecimal settlementAmount;  // 最终结算金额：实收减去退费
}