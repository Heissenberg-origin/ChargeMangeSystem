package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "挂号统计结果")
public class RegistrationStatsDTO {
    @Schema(description = "分组名称(科室/医生)")
    private String groupName;

    @Schema(description = "时间区间(按日/周/月)")
    private String timePeriod;

    @Schema(description = "挂号总数")
    private Integer totalRegistrations;

    @Schema(description = "退号数量")
    private Integer canceledCount;

    @Schema(description = "应收金额")
    private BigDecimal totalAmount;

    @Schema(description = "退号金额")
    private BigDecimal refundAmount;

    @Schema(description = "实收金额")
    public BigDecimal getActualAmount() {
        return totalAmount.subtract(refundAmount != null ? refundAmount : BigDecimal.ZERO);
    }
}

