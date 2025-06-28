package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Schema(description = "业务报表统计汇总结果")
public class BusinessReportSummary {
    @Schema(description = "门诊建档总人数")
    private Integer totalPatientCount;

    @Schema(description = "门诊挂号总人数")
    private Integer totalRegistrationCount;

    @Schema(description = "门诊挂号总费用")
    private BigDecimal totalRegistrationFee;

    @Schema(description = "门诊缴费总人次")
    private Integer totalPrescriptionCount;

    @Schema(description = "门诊总费用")
    private BigDecimal totalPrescriptionFee;

    @Schema(description = "门诊药占比(%)")
    private BigDecimal avgMedicineRatio;

    @Schema(description = "门诊人均费用")
    private BigDecimal overallAvgFeePerPatient;
}