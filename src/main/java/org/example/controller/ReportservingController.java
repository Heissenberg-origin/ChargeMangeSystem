package org.example.controller;

import org.example.service.OutpatientReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/report")
public class ReportservingController {

    @Autowired
    private OutpatientReportService reportService;

    @GetMapping("/outpatient")
    public Map<String, Object> getOutpatientReport(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {

        // 调整结束日期为当天的23:59:59
        endDate = new Date(endDate.getTime() + 24 * 60 * 60 * 1000 - 1);

        Map<String, Object> report = reportService.generateOutpatientReport(startDate, endDate);

        // 格式化结果
        Map<String, Object> result = new HashMap<>();
        result.put("startDate", startDate);
        result.put("endDate", endDate);
        result.put("totalPatients", report.get("totalPatients"));
        result.put("registrationCount", report.get("registrationCount"));
        result.put("registrationFeeTotal", formatCurrency((BigDecimal) report.get("registrationFeeTotal")));
        result.put("paymentCount", report.get("paymentCount"));
        result.put("prescriptionFeeTotal", formatCurrency((BigDecimal) report.get("prescriptionFeeTotal")));
        result.put("medicineRatio", formatPercentage((BigDecimal) report.get("medicineRatio")));
        result.put("avgFeePerPatient", formatCurrency((BigDecimal) report.get("avgFeePerPatient")));

        // 分组统计结果
        result.put("byDepartment", report.get("byDepartment"));
        result.put("byDoctorRank", report.get("byDoctorRank"));
        result.put("byRegType", report.get("byRegType"));
        result.put("byFeeType", report.get("byFeeType"));

        return result;
    }

    private String formatCurrency(BigDecimal amount) {
        return amount != null ? "¥" + amount.setScale(2, BigDecimal.ROUND_HALF_UP) : "¥0.00";
    }

    private String formatPercentage(BigDecimal ratio) {
        return ratio != null ? ratio.setScale(2, BigDecimal.ROUND_HALF_UP) + "%" : "0%";
    }
}