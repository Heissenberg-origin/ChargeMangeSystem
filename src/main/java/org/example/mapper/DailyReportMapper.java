package org.example.mapper;

import org.example.dto.DailyReportDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface DailyReportMapper {
    List<DailyReportDTO> getDailyReport(//获取日结报表数据
            @Param("startDate") Date startDate,
            @Param("endDate") Date endDate);
}