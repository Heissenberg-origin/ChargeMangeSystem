// PrescriptionStatsSummary.java
package org.example.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class PrescriptionStatsSummary {
    private Integer totalPrescriptions; // 总处方数
    private Integer paidPrescriptions; // 已收费处方数
    private Integer unpaidPrescriptions; // 未收费处方数
    private Integer refundPrescriptions; // 退费处方数
    private BigDecimal totalAmount; // 总金额
    private BigDecimal paidAmount; // 已收费金额
    private BigDecimal unpaidAmount; // 未收费金额
    private BigDecimal refundAmount; // 退费金额
    private BigDecimal unpaidRatio; // 未收费比例
    private List<PrescriptionStatsDTO> groupedStats; // 分组统计数据(根据请求参数决定是科室还是医生)
}