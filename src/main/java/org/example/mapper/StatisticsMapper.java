package org.example.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.example.entity.ChargeItemsInfo;
import org.example.entity.PrescriptionInfo;
import org.example.entity.RegistrationInfo;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface StatisticsMapper {

    /**
     * 统计时间段内新增患者数量
     */
    @Select("SELECT COUNT(*) FROM patient_info")
    int countPatientsByCreateTime(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 统计时间段内挂号记录数量
     */
    @Select("SELECT COUNT(*) FROM registration_info WHERE reg_time BETWEEN #{startDate} AND #{endDate}")
    int countRegistrationsByTime(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 统计时间段内挂号费用总和
     */
    @Select("SELECT SUM(doc.doc_fee) FROM registration_info r " +
            "JOIN arrange_info ai ON ai.arrange_id=r.reg_arrange_id "+
            "JOIN doctor_info doc ON ai.arrange_doc_id = doc.doc_id " +
            "WHERE r.reg_time BETWEEN #{startDate} AND #{endDate}")
    BigDecimal sumRegistrationFeesByTime(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 统计时间段内已缴费处方数量
     */
    @Select("SELECT COUNT(*) FROM prescription_info " +
            "WHERE pre_dealtime IS NOT NULL " +
            "AND pre_dealtime BETWEEN #{startDate} AND #{endDate}")
    int countPaidPrescriptionsByTime(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 获取时间段内的所有处方信息（用于计算处方总费用和药占比）
     */
    @Select("SELECT * FROM prescription_info " +
            "WHERE pre_time BETWEEN #{startDate} AND #{endDate}")
    @Results({})
    List<PrescriptionInfo> findPrescriptionsByTime(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 获取收费项目详情（用于计算处方费用）
     */
    @Select("SELECT * FROM chargeitems_info WHERE chargeitem_id = #{itemId}")
    ChargeItemsInfo findChargeItemById(@Param("itemId") int itemId);

    /**
     * 按科室统计挂号人数
     */
    @Select("SELECT d.department_name AS name, COUNT(*) AS value " +
            "FROM registration_info r " +
            "JOIN doctor_info doc ON r.reg_doc_id = doc.doc_id " +
            "JOIN department_info d ON doc.doc_dp_id = d.department_id " +
            "WHERE r.reg_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY d.department_name")
    List<Map<String, Object>> countRegistrationsByDepartment(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 按医生职称统计挂号人数
     */
    @Select("SELECT doc.doc_rank AS name, COUNT(*) AS value " +
            "FROM registration_info r " +
            "JOIN arrange_info ai ON ai.arrange_id=r.reg_arrange_id "+
            "JOIN doctor_info doc ON ai.arrange_doc_id = doc.doc_id " +
            "WHERE r.reg_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY doc.doc_rank")
    List<Map<String, Object>> countRegistrationsByDoctorRank(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 按挂号类型统计挂号人数
     */
    @Select("SELECT reg_type AS name, COUNT(*) AS value " +
            "FROM registration_info " +
            "WHERE reg_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY reg_type")
    List<Map<String, Object>> countRegistrationsByType(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 按收费类型统计挂号人数
     */
    @Select("SELECT reg_fee_type AS name, COUNT(*) AS value " +
            "FROM registration_info " +
            "WHERE reg_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY reg_fee_type")
    List<Map<String, Object>> countRegistrationsByFeeType(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    /**
     * 获取时间段内的挂号记录（用于详细分析）
     */
    @Select("SELECT * FROM registration_info " +
            "WHERE reg_time BETWEEN #{startDate} AND #{endDate}")
    List<RegistrationInfo> findRegistrationsByTime(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}