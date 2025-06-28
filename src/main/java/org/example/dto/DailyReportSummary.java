package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "日报表统计汇总结果")
public class DailyReportSummary {
    @Schema(description = "统计数据列表")
    private List<DailyReportDTO> reportList;

    @Schema(description = "合计-应收金额")
    private BigDecimal totalReceivableSum;

    @Schema(description = "合计-实收金额")
    private BigDecimal actualReceivedSum;

    @Schema(description = "合计-差额")
    private BigDecimal differenceSum;

    @Schema(description = "合计-退费金额")
    private BigDecimal refundAmountSum;

    @Schema(description = "合计-结算金额")
    private BigDecimal settlementAmountSum;
}