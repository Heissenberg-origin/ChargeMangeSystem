package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * DailyReportSummary 类用于封装日报表的统计汇总结果。
 */
@Data
@Schema(description = "日报表统计汇总结果")
public class DailyReportSummary {

    @Schema(description = "统计数据列表")
    private List<DailyReportDTO> reportList;  // 包含多条日报表统计数据的列表

    @Schema(description = "合计-应收金额")
    private BigDecimal totalReceivableSum;  // 所有日报表的应收金额总和

    @Schema(description = "合计-实收金额")
    private BigDecimal actualReceivedSum;  // 所有日报表的实收金额总和

    @Schema(description = "合计-差额")
    private BigDecimal differenceSum;  // 所有日报表的差额总和（应收金额 - 实收金额）

    @Schema(description = "合计-退费金额")
    private BigDecimal refundAmountSum;  // 所有日报表的退费金额总和

    @Schema(description = "合计-结算金额")
    private BigDecimal settlementAmountSum;  // 所有日报表的结算金额总和（实收金额 - 退费金额）
}