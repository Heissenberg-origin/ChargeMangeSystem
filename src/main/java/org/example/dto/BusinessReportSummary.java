package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * BusinessReportSummary 类用于封装业务报表的统计汇总结果。
 */
@Data
@Schema(description = "业务报表统计汇总结果")
public class BusinessReportSummary {

    @Schema(description = "门诊建档总人数")
    private Integer totalPatientCount;  // 门诊建档的总人数

    @Schema(description = "门诊挂号总人数")
    private Integer totalRegistrationCount;  // 挂号的总人数

    @Schema(description = "门诊挂号总费用")
    private BigDecimal totalRegistrationFee;  // 挂号产生的总费用

    @Schema(description = "门诊缴费总人次")
    private Integer totalPrescriptionCount;  // 支付挂号费的人次

    @Schema(description = "门诊总费用")
    private BigDecimal totalPrescriptionFee;  // 门诊的总费用

    @Schema(description = "门诊药占比(%)")
    private BigDecimal avgMedicineRatio;  // 药品费用占总费用的比例，单位为百分比

    @Schema(description = "门诊人均费用")
    private BigDecimal overallAvgFeePerPatient;  // 每位患者的平均费用
}