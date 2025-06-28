package org.example.service;

import org.example.dto.RegistrationStatsRequest;
import org.example.dto.RegistrationStatsDTO;
import org.example.dto.RegistrationStatsSummary;
import org.example.mapper.RegistrationStatsMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RegistrationStatsService {

    private final RegistrationStatsMapper statsMapper;

    public RegistrationStatsService(RegistrationStatsMapper statsMapper) {
        this.statsMapper = statsMapper;
    }

    public RegistrationStatsSummary getRegistrationStats(RegistrationStatsRequest request) {
        List<RegistrationStatsDTO> statsList;

        if ("doctor".equals(request.getGroupBy())) {
            statsList = statsMapper.statsByDoctor(
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getTimeType()
            );
        } else {
            statsList = statsMapper.statsByDepartment(
                    request.getStartTime(),
                    request.getEndTime(),
                    request.getTimeType()
            );
        }

        // 计算合计
        RegistrationStatsSummary summary = new RegistrationStatsSummary();
        summary.setStatsList(statsList);

        int totalReg = 0;
        int totalCancel = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal totalRefund = BigDecimal.ZERO;

        for (RegistrationStatsDTO stat : statsList) {
            totalReg += stat.getTotalRegistrations();
            totalCancel += stat.getCanceledCount();
            totalAmount = totalAmount.add(stat.getTotalAmount());
            totalRefund = totalRefund.add(stat.getRefundAmount() != null ? stat.getRefundAmount() : BigDecimal.ZERO);
        }

        summary.setTotalRegistrations(totalReg);
        summary.setTotalCanceled(totalCancel);
        summary.setTotalAmount(totalAmount);
        summary.setTotalRefund(totalRefund);

        return summary;
    }
}