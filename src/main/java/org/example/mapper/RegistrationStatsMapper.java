package org.example.mapper;

import org.example.dto.RegistrationStatsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

@Mapper
public interface RegistrationStatsMapper {
    List<RegistrationStatsDTO> statsByDepartment(//按部门统计挂号数据
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("timeType") String timeType
    );

    List<RegistrationStatsDTO> statsByDoctor(//按医生统计挂号数据
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("timeType") String timeType
    );
}