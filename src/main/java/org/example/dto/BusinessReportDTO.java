package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "业务报表统计结果")
public class BusinessReportDTO {
    @Schema(description = "日期", example = "2025-06-01")
    private String reportDate;

    @Schema(description = "门诊建档人数")
    private Integer patientCount;

    @Schema(description = "门诊挂号人数")
    private Integer registrationCount;

    @Schema(description = "门诊挂号费用")
    private BigDecimal registrationFee;

    @Schema(description = "门诊缴费人次")
    private Integer prescriptionCount;

    @Schema(description = "门诊费用")
    private BigDecimal prescriptionFee;

    @Schema(description = "门诊药占比(%)")
    private BigDecimal medicineRatio;

    @Schema(description = "门诊人均费用")
    private BigDecimal avgFeePerPatient;
}