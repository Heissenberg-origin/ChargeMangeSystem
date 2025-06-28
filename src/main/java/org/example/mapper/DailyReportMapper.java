package org.example.mapper;

import org.example.dto.DailyReportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface DailyReportMapper {
    List<DailyReportDTO> getDailyReport(
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}