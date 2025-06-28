package org.example.service;

import org.example.dto.BusinessReportRequest;
import org.example.dto.BusinessReportSummary;
import org.example.mapper.BusinessReportMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class BusinessReportService {

    private final BusinessReportMapper businessReportMapper;

    public BusinessReportService(BusinessReportMapper businessReportMapper) {
        this.businessReportMapper = businessReportMapper;
    }

    public BusinessReportSummary getBusinessReport(BusinessReportRequest request) {
        BusinessReportSummary summary = businessReportMapper.getBusinessReportSummary(
                request.getStartDate(),
                request.getEndDate());

        // 计算整体人均费用
        BigDecimal overallAvgFeePerPatient = BigDecimal.ZERO;
        if (summary.getTotalRegistrationCount() > 0) {
            overallAvgFeePerPatient = summary.getTotalRegistrationFee()
                    .add(summary.getTotalPrescriptionFee())
                    .divide(BigDecimal.valueOf(summary.getTotalRegistrationCount()), 2, RoundingMode.HALF_UP);
        }
        summary.setOverallAvgFeePerPatient(overallAvgFeePerPatient);

        return summary;
    }
}