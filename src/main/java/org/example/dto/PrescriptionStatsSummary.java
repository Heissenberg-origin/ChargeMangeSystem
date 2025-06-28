package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "处方统计汇总结果")
public class PrescriptionStatsSummary {
    @Schema(description = "统计数据列表")
    private List<PrescriptionStatsDTO> statsList;

    @Schema(description = "合计-缴费单数")
    private Integer totalPaymentCount;

    @Schema(description = "合计-缴费金额")
    private BigDecimal totalPaymentAmount;

    @Schema(description = "合计-退费金额")
    private BigDecimal totalRefundAmount;

    @Schema(description = "合计-实际金额")
    public BigDecimal getTotalActualAmount() {
        return totalPaymentAmount.subtract(totalRefundAmount != null ? totalRefundAmount : BigDecimal.ZERO);
    }
}