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

    // 构造函数，注入 DailyReportMapper
    public DailyReportService(DailyReportMapper dailyReportMapper) {
        this.dailyReportMapper = dailyReportMapper;
    }

    /**
     * 获取每日报告的汇总信息
     *
     * @param request 请求对象，包含开始和结束日期
     * @return 每日报告汇总
     */
    public DailyReportSummary getDailyReport(DailyReportRequest request) {
        // 获取每日报告列表
        List<DailyReportDTO> reportList = dailyReportMapper.getDailyReport(
                request.getStartDate(),
                request.getEndDate());

        // 创建汇总对象
        DailyReportSummary summary = new DailyReportSummary();
        summary.setReportList(reportList);

        // 初始化合计变量
        BigDecimal totalReceivableSum = BigDecimal.ZERO;
        BigDecimal actualReceivedSum = BigDecimal.ZERO;
        BigDecimal differenceSum = BigDecimal.ZERO;
        BigDecimal refundAmountSum = BigDecimal.ZERO;
        BigDecimal settlementAmountSum = BigDecimal.ZERO;

        // 遍历报告列表，计算各项合计
        for (DailyReportDTO daily : reportList) {
            totalReceivableSum = totalReceivableSum.add(daily.getTotalReceivable());
            actualReceivedSum = actualReceivedSum.add(daily.getActualReceived());
            differenceSum = differenceSum.add(daily.getDifference());
            refundAmountSum = refundAmountSum.add(daily.getRefundAmount());
            settlementAmountSum = settlementAmountSum.add(daily.getSettlementAmount());
        }

        // 设置汇总信息
        summary.setTotalReceivableSum(totalReceivableSum);
        summary.setActualReceivedSum(actualReceivedSum);
        summary.setDifferenceSum(differenceSum);
        summary.setRefundAmountSum(refundAmountSum);
        summary.setSettlementAmountSum(settlementAmountSum);

        return summary;
    }
}