// PrescriptionStatsMapper.java
package org.example.mapper;

import org.apache.ibatis.annotations.Select;
import org.example.dto.PrescriptionStatsDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

@Mapper
public interface PrescriptionStatsMapper {
    List<PrescriptionStatsDTO> statsByDepartment(//按部门统计
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("timeType") String timeType
    );

    List<PrescriptionStatsDTO> statsByDoctor(//按医生统计
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime,
            @Param("timeType") String timeType
    );

    // 新增总统计数据方法
    @Select("SELECT " +
            "COUNT(*) AS totalPrescriptions, " +
            "SUM(CASE WHEN pre_state = '已完成' THEN 1 ELSE 0 END) AS paidPrescriptions, " +
            "SUM(CASE WHEN pre_state = '待缴费' THEN 1 ELSE 0 END) AS unpaidPrescriptions, " +
            "SUM(CASE WHEN pre_state = '已退费' THEN 1 ELSE 0 END) AS refundPrescriptions, " +
            "SUM(ci.chargeitem_price * p.pre_ci_num) AS totalAmount, " +
            "SUM(CASE WHEN p.pre_state = '已完成' THEN ci.chargeitem_price * p.pre_ci_num ELSE 0 END) AS paidAmount, " +
            "SUM(CASE WHEN p.pre_state = '待缴费' THEN ci.chargeitem_price * p.pre_ci_num ELSE 0 END) AS unpaidAmount, " +
            "SUM(CASE WHEN p.pre_state = '已退费' THEN ci.chargeitem_price * p.pre_ci_num ELSE 0 END) AS refundAmount " +
            "FROM prescription_info p " +
            "JOIN chargeitems_info ci ON p.pre_ci_id = ci.chargeitem_id " +
            "WHERE p.pre_time BETWEEN #{startTime} AND #{endTime}")
    PrescriptionStatsDTO getTotalStats(//获取统计数据
            @Param("startTime") Date startTime,
            @Param("endTime") Date endTime
    );
}