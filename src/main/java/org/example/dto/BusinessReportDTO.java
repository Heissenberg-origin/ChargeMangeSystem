package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * BusinessReportDTO 类用于封装业务报表的统计结果。
 */
@Data
@Schema(description = "业务报表统计结果")
public class BusinessReportDTO {

    @Schema(description = "日期", example = "2025-06-01")
    private String reportDate;  // 报表日期，格式为 YYYY-MM-DD

    @Schema(description = "门诊建档人数")
    private Integer patientCount;  // 门诊建档的总人数

    @Schema(description = "门诊挂号人数")
    private Integer registrationCount;  // 挂号的总人数

    @Schema(description = "门诊挂号费用")
    private BigDecimal registrationFee;  // 挂号产生的总费用

    @Schema(description = "门诊缴费人次")
    private Integer prescriptionCount;  // 支付挂号费的人次

    @Schema(description = "门诊费用")
    private BigDecimal prescriptionFee;  // 门诊的总费用

    @Schema(description = "门诊药占比(%)")
    private BigDecimal medicineRatio;  // 药品费用占总费用的比例，单位为百分比

    @Schema(description = "门诊人均费用")
    private BigDecimal avgFeePerPatient;  // 每位患者的平均费用
}