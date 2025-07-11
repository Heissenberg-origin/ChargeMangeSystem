package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * RegistrationStatsDTO 类用于封装挂号统计结果。
 */
@Data
@Schema(description = "挂号统计结果")
public class RegistrationStatsDTO {

    @Schema(description = "分组名称(科室/医生)")
    private String groupName;  // 按科室或医生分组的名称

    @Schema(description = "时间区间(按日/周/月)")
    private String timePeriod;  // 统计的时间区间，可以是日、周或月

    @Schema(description = "挂号总数")
    private Integer totalRegistrations;  // 挂号的总数量

    @Schema(description = "退号数量")
    private Integer canceledCount;  // 退号的数量

    @Schema(description = "应收金额")
    private BigDecimal totalAmount;  // 应收的总金额

    @Schema(description = "退号金额")
    private BigDecimal refundAmount;  // 退号的金额

    /**
     * 计算实收金额：应收金额减去退号金额。
     * @return 实收金额
     */
    @Schema(description = "实收金额")
    public BigDecimal getActualAmount() {
        return totalAmount.subtract(refundAmount != null ? refundAmount : BigDecimal.ZERO);
    }
}