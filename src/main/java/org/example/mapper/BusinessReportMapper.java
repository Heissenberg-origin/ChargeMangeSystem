package org.example.mapper;

import org.example.dto.BusinessReportSummary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;

@Mapper
public interface BusinessReportMapper {
    BusinessReportSummary getBusinessReportSummary(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}