package org.example.service;

import org.example.dto.PrescriptionStatsRequest;
import org.example.dto.PrescriptionStatsSummary;
import org.example.dto.PrescriptionStatsDTO;
import org.example.mapper.PrescriptionStatsMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;

@Service
public class PrescriptionStatsService {

    private final PrescriptionStatsMapper statsMapper;

    // 构造函数，注入 PrescriptionStatsMapper
    public PrescriptionStatsService(PrescriptionStatsMapper statsMapper) {
        this.statsMapper = statsMapper;
    }

    /**
     * 获取处方统计数据
     *
     * @param request 统计请求对象，包含开始时间、结束时间、时间类型和分组依据
     * @return 处方统计汇总信息
     */
    public PrescriptionStatsSummary getPrescriptionStats(PrescriptionStatsRequest request) {
        Date startTime = request.getStartTime();
        Date endTime = request.getEndTime();
        String timeType = request.getTimeType();
        String groupBy = request.getGroupBy();

        // 获取总统计数据
        PrescriptionStatsDTO totalStats = statsMapper.getTotalStats(startTime, endTime);

        // 计算未收费比例
        BigDecimal unpaidRatio = BigDecimal.ZERO;
        if (totalStats.getTotalPrescriptions() > 0) {
            unpaidRatio = new BigDecimal(totalStats.getUnpaidPrescriptions())
                    .divide(new BigDecimal(totalStats.getTotalPrescriptions()), 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }
        totalStats.setUnpaidRatio(unpaidRatio);

        // 构建返回结果
        PrescriptionStatsSummary summary = new PrescriptionStatsSummary();
        summary.setTotalPrescriptions(totalStats.getTotalPrescriptions());
        summary.setPaidPrescriptions(totalStats.getPaidPrescriptions());
        summary.setUnpaidPrescriptions(totalStats.getUnpaidPrescriptions());
        summary.setRefundPrescriptions(totalStats.getRefundPrescriptions());
        summary.setTotalAmount(totalStats.getTotalAmount());
        summary.setPaidAmount(totalStats.getPaidAmount());
        summary.setUnpaidAmount(totalStats.getUnpaidAmount());
        summary.setRefundAmount(totalStats.getRefundAmount());
        summary.setUnpaidRatio(totalStats.getUnpaidRatio());

        // 根据 groupBy 参数获取对应的分组统计数据
        if ("department".equalsIgnoreCase(groupBy)) {
            List<PrescriptionStatsDTO> byDepartment = statsMapper.statsByDepartment(startTime, endTime, timeType);
            calculateUnpaidRatio(byDepartment);
            summary.setGroupedStats(byDepartment);
        } else if ("doctor".equalsIgnoreCase(groupBy)) {
            List<PrescriptionStatsDTO> byDoctor = statsMapper.statsByDoctor(startTime, endTime, timeType);
            calculateUnpaidRatio(byDoctor);
            summary.setGroupedStats(byDoctor);
        }

        return summary;
    }

    /**
     * 计算每个统计项的未收费比例
     *
     * @param statsList 统计数据列表
     */
    private void calculateUnpaidRatio(List<PrescriptionStatsDTO> statsList) {
        for (PrescriptionStatsDTO stats : statsList) {
            if (stats.getTotalPrescriptions() > 0) {
                BigDecimal ratio = new BigDecimal(stats.getUnpaidPrescriptions())
                        .divide(new BigDecimal(stats.getTotalPrescriptions()), 4, RoundingMode.HALF_UP)
                        .multiply(new BigDecimal(100));
                stats.setUnpaidRatio(ratio);
            } else {
                stats.setUnpaidRatio(BigDecimal.ZERO);
            }
        }
    }
}