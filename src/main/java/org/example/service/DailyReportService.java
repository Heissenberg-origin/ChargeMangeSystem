package org.example.service;

import org.example.dto.DailyReportDTO;
import org.example.dto.DailyReportRequest;
import org.example.dto.DailyReportSummary;
import org.example.mapper.DailyReportMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class DailyReportService {

    private final DailyReportMapper dailyReportMapper;

    public DailyReportService(DailyReportMapper dailyReportMapper) {
        this.dailyReportMapper = dailyReportMapper;
    }

    public DailyReportSummary getDailyReport(DailyReportRequest request) {
        List<DailyReportDTO> reportList = dailyReportMapper.getDailyReport(
                request.getStartDate(),
                request.getEndDate());

        DailyReportSummary summary = new DailyReportSummary();
        summary.setReportList(reportList);

        // 计算合计
        BigDecimal totalReceivableSum = BigDecimal.ZERO;
        BigDecimal actualReceivedSum = BigDecimal.ZERO;
        BigDecimal differenceSum = BigDecimal.ZERO;
        BigDecimal refundAmountSum = BigDecimal.ZERO;
        BigDecimal settlementAmountSum = BigDecimal.ZERO;

        for (DailyReportDTO daily : reportList) {
            totalReceivableSum = totalReceivableSum.add(daily.getTotalReceivable());
            actualReceivedSum = actualReceivedSum.add(daily.getActualReceived());
            differenceSum = differenceSum.add(daily.getDifference());
            refundAmountSum = refundAmountSum.add(daily.getRefundAmount());
            settlementAmountSum = settlementAmountSum.add(daily.getSettlementAmount());
        }

        summary.setTotalReceivableSum(totalReceivableSum);
        summary.setActualReceivedSum(actualReceivedSum);
        summary.setDifferenceSum(differenceSum);
        summary.setRefundAmountSum(refundAmountSum);
        summary.setSettlementAmountSum(settlementAmountSum);

        return summary;
    }
}