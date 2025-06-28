// PrescriptionStatsDTO.java
package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PrescriptionStatsDTO {
    private String groupName; // 分组名称(科室/医生)
    private String timePeriod; // 时间周期(日/周/月)
    private Integer totalPrescriptions; // 总处方数
    private Integer paidPrescriptions; // 已收费处方数
    private Integer unpaidPrescriptions; // 未收费处方数
    private BigDecimal totalAmount; // 总金额
    private BigDecimal paidAmount; // 已收费金额
    private BigDecimal unpaidAmount; // 未收费金额
    private BigDecimal unpaidRatio; // 未收费比例
    private Integer refundPrescriptions; // 退费处方数
    private BigDecimal refundAmount; // 退费金额
}