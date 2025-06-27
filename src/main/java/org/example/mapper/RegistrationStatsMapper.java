package org.example.mapper;

import org.example.dto.RegistrationStatsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface RegistrationStatsMapper {
    List<RegistrationStatsDTO> statsByDepartment(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("timeType") String timeType
    );

    List<RegistrationStatsDTO> statsByDoctor(
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("timeType") String timeType
    );
}