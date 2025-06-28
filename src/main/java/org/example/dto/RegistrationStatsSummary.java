package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Schema(description = "挂号统计汇总结果")
public class RegistrationStatsSummary {
    @Schema(description = "统计数据列表")
    private List<RegistrationStatsDTO> statsList;

    @Schema(description = "合计-挂号总数")
    private Integer totalRegistrations;

    @Schema(description = "合计-退号数量")
    private Integer totalCanceled;

    @Schema(description = "合计-应收金额")
    private BigDecimal totalAmount;

    @Schema(description = "合计-退号金额")
    private BigDecimal totalRefund;
}