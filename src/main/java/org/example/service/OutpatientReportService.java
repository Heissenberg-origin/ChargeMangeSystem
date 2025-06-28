package org.example.service;

import org.example.entity.*;
import org.example.mapper.StatisticsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OutpatientReportService {

    @Autowired
    private StatisticsMapper statisticsMapper;

    /**
     * 生成门诊业务报表
     */
    public Map<String, Object> generateOutpatientReport(Date startDate, Date endDate) {
        Map<String, Object> report = new HashMap<>();

        // 1. 门诊建档总人数
        int totalPatients = statisticsMapper.countPatientsByCreateTime(startDate, endDate);
        report.put("totalPatients", totalPatients);

        // 2. 门诊挂号人数
        int registrationCount = statisticsMapper.countRegistrationsByTime(startDate, endDate);
        report.put("registrationCount", registrationCount);

        // 3. 门诊挂号费用
        BigDecimal registrationFeeTotal = statisticsMapper.sumRegistrationFeesByTime(startDate, endDate);
        report.put("registrationFeeTotal", registrationFeeTotal != null ? registrationFeeTotal : BigDecimal.ZERO);

        // 4. 门诊缴费人次
        int paymentCount = statisticsMapper.countPaidPrescriptionsByTime(startDate, endDate);
        report.put("paymentCount", paymentCount);

        // 5. 门诊费用(处方项目总费用)和药占比计算
        List<PrescriptionInfo> prescriptions = statisticsMapper.findPrescriptionsByTime(startDate, endDate);
        System.out.println(prescriptions);
        BigDecimal prescriptionFeeTotal = BigDecimal.ZERO;
        BigDecimal medicineFeeTotal = BigDecimal.ZERO;

        for (PrescriptionInfo prescription : prescriptions) {
            ChargeItemsInfo item = statisticsMapper.findChargeItemById(prescription.getPreCiId());
            if (item != null) {
                BigDecimal itemTotal = item.getChargeItemPrice().multiply(new BigDecimal(prescription.getPreCiNum()));
                prescriptionFeeTotal = prescriptionFeeTotal.add(itemTotal);

                if (isMedicineItem(item.getChargeItemType())) {
                    medicineFeeTotal = medicineFeeTotal.add(itemTotal);
                }
            }
        }

        report.put("prescriptionFeeTotal", prescriptionFeeTotal);

        // 6. 门诊药占比
        BigDecimal medicineRatio = BigDecimal.ZERO;
        if (prescriptionFeeTotal.compareTo(BigDecimal.ZERO) > 0) {
            medicineRatio = medicineFeeTotal.divide(prescriptionFeeTotal, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal(100));
        }
        report.put("medicineRatio", medicineRatio);

        // 7. 门诊人均费用
        BigDecimal avgFeePerPatient = BigDecimal.ZERO;
        if (registrationCount > 0) {
            BigDecimal totalFee = (registrationFeeTotal != null ? registrationFeeTotal : BigDecimal.ZERO)
                    .add(prescriptionFeeTotal);
            avgFeePerPatient = totalFee.divide(new BigDecimal(registrationCount), 2, RoundingMode.HALF_UP);
        }
        report.put("avgFeePerPatient", avgFeePerPatient);

        // 8. 分组统计
        report.put("byDepartment", statisticsMapper.countRegistrationsByDepartment(startDate, endDate));
        report.put("byDoctorRank", statisticsMapper.countRegistrationsByDoctorRank(startDate, endDate));
        report.put("byRegType", statisticsMapper.countRegistrationsByType(startDate, endDate));
        report.put("byFeeType", statisticsMapper.countRegistrationsByFeeType(startDate, endDate));

        return report;
    }

    /**
     * 判断是否为药品项目
     */
    private boolean isMedicineItem(ChargeItemsInfo.ChargeItemType type) {
        return type == ChargeItemsInfo.ChargeItemType.西药 ||
                type == ChargeItemsInfo.ChargeItemType.中成药 ||
                type == ChargeItemsInfo.ChargeItemType.中草药;
    }
}