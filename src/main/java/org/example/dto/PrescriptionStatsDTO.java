package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "处方统计结果")
public class PrescriptionStatsDTO {
    @Schema(description = "分组名称(科室/医生)")
    private String groupName;

    @Schema(description = "时间区间(按日/周/月)")
    private String timePeriod;

    @Schema(description = "缴费单数")
    private Integer paymentCount;

    @Schema(description = "缴费金额")
    private BigDecimal paymentAmount;

    @Schema(description = "退费金额")
    private BigDecimal refundAmount;

    @Schema(description = "实际金额")
    public BigDecimal getActualAmount() {
        return paymentAmount.subtract(refundAmount != null ? refundAmount : BigDecimal.ZERO);
    }
}