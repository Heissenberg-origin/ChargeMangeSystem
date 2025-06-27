package org.example.service;

import org.example.dto.PrescriptionStatsRequest;
import org.example.dto.PrescriptionStatsDTO;
import org.example.dto.PrescriptionStatsSummary;
import org.example.mapper.PrescriptionStatsMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionStatsService {

    private final PrescriptionStatsMapper statsMapper;

    public PrescriptionStatsService(PrescriptionStatsMapper statsMapper) {
        this.statsMapper = statsMapper;
    }

    public PrescriptionStatsSummary getPrescriptionStats(PrescriptionStatsRequest request) {
        List<PrescriptionStatsDTO> statsList;

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

        // 按实际金额从高到低排序
        statsList = statsList.stream()
                .sorted((a, b) -> b.getActualAmount().compareTo(a.getActualAmount()))
                .collect(Collectors.toList());

        // 计算合计
        PrescriptionStatsSummary summary = new PrescriptionStatsSummary();
        summary.setStatsList(statsList);

        int totalPaymentCount = 0;
        BigDecimal totalPaymentAmount = BigDecimal.ZERO;
        BigDecimal totalRefundAmount = BigDecimal.ZERO;

        for (PrescriptionStatsDTO stat : statsList) {
            totalPaymentCount += stat.getPaymentCount();
            totalPaymentAmount = totalPaymentAmount.add(stat.getPaymentAmount());
            totalRefundAmount = totalRefundAmount.add(stat.getRefundAmount() != null ?
                    stat.getRefundAmount() : BigDecimal.ZERO);
        }

        summary.setTotalPaymentCount(totalPaymentCount);
        summary.setTotalPaymentAmount(totalPaymentAmount);
        summary.setTotalRefundAmount(totalRefundAmount);

        return summary;
    }
}