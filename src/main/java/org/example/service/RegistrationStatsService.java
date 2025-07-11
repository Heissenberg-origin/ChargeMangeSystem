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

    // 构造函数，注入 RegistrationStatsMapper
    public RegistrationStatsService(RegistrationStatsMapper statsMapper) {
        this.statsMapper = statsMapper;
    }

    /**
     * 获取挂号统计数据
     *
     * @param request 统计请求对象，包含开始时间、结束时间和分组依据
     * @return 挂号统计汇总信息
     */
    public RegistrationStatsSummary getRegistrationStats(RegistrationStatsRequest request) {
        List<RegistrationStatsDTO> statsList;

        // 根据请求的分组依据获取统计数据
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

        int totalReg = 0; // 总挂号数
        int totalCancel = 0; // 总取消数
        BigDecimal totalAmount = BigDecimal.ZERO; // 总金额
        BigDecimal totalRefund = BigDecimal.ZERO; // 总退款

        // 遍历统计列表，累加各项数据
        for (RegistrationStatsDTO stat : statsList) {
            totalReg += stat.getTotalRegistrations();
            totalCancel += stat.getCanceledCount();
            totalAmount = totalAmount.add(stat.getTotalAmount());
            totalRefund = totalRefund.add(stat.getRefundAmount() != null ? stat.getRefundAmount() : BigDecimal.ZERO);
        }

        // 设置统计汇总信息
        summary.setTotalRegistrations(totalReg);
        summary.setTotalCanceled(totalCancel);
        summary.setTotalAmount(totalAmount);
        summary.setTotalRefund(totalRefund);

        return summary;
    }
}