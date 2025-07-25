package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.PatientInfo;
import org.example.entity.RegistrationInfo;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface RegistrationInfoMapper extends BaseMapper<RegistrationInfo> {

    /**
     * 根据登记ID查询登记信息及相关医生和患者信息
     *
     * @param regid 登记ID
     * @return 登记信息
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE ri.reg_id=#{regid}
    """)
    @Results(id="RegistrationResultsMap", value = {
            @Result(property = "regId", column = "reg_id"),
            @Result(property = "regHcardId", column = "reg_hcard_id"),
            @Result(property = "regPname", column = "reg_pname"),
            @Result(property = "regdocName", column = "regdoc_name"),
            @Result(property = "regdepName", column = "regdep_name"),
            @Result(property = "regfee", column = "reg_fee"),
            @Result(property = "regState", column = "reg_state"),
            @Result(property = "regTime", column = "reg_time"),
            @Result(property = "regType", column = "reg_type"),
            @Result(property = "regFeeType", column = "reg_fee_type"),
            @Result(property = "regConsultationType", column = "reg_consultation_type"),
            @Result(property = "regArrangeId", column = "reg_arrange_id"),
            @Result(property = "regTimezone", column = "reg_timezone"),
            @Result(property = "regDealerId", column = "reg_dealer_id"),
            @Result(property = "regDealTime", column = "reg_deal_time"),
            @Result(property = "regDealType", column = "reg_deal_type")
    })
    RegistrationInfo getById(int regid);

    /**
     * 根据健康卡ID查询登记信息
     *
     * @param hcardid 健康卡ID
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE ri.reg_hcard_id=#{hcardid}
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getByhcardId(int hcardid);

    /**
     * 查询所有登记信息
     *
     * @return 所有登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getallreg();

    /**
     * 根据医生姓名查询登记信息
     *
     * @param docname 医生姓名
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE doci.doc_name LIKE CONCAT('%', #{docname}, '%')
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbydocname(String docname);

    /**
     * 根据身份证号码查询登记信息
     *
     * @param PId 身份证号码
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE pi.identification_id=#{PId}
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbyPId(String PId);

    /**
     * 根据患者姓名查询登记信息
     *
     * @param pname 患者姓名
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE pi.name LIKE CONCAT('%', #{pname}, '%')
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbypname(String pname);

    /**
     * 根据安排ID查询登记信息
     *
     * @param arrangeid 安排ID
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE ri.reg_arrange_id=#{arrangeid}
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbyarrangId(int arrangeid);

    /**
     * 根据部门名称查询登记信息
     *
     * @param depname 部门名称
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE depi.department_name LIKE CONCAT('%', #{depname}, '%')
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbydepname(String depname);

    /**
     * 根据登记状态查询登记信息
     *
     * @param state 登记状态
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE ri.reg_state=#{state}
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbystate(String state);

    /**
     * 更新登记状态
     *
     * @param id    登记ID
     * @param state 新状态
     */
    @Update("UPDATE registration_info SET reg_state=#{state.displayValue} WHERE reg_id=#{id}")
    void updateState(int id, RegistrationInfo.RegistrationState state);

    /**
     * 根据时间范围查询登记信息
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE ri.reg_time BETWEEN #{startTime} AND #{endTime}
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbytimerange(Date startTime, Date endTime);

    /**
     * 查询登记统计信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 统计信息
     */
    @Select("SELECT " +
            "COUNT(*) AS count, " +
            "SUM(pre_ci_num * (SELECT chargeitem_price FROM chargeitems_info WHERE chargeitem_id = pre_ci_id)) AS amount " +
            "FROM registration_info " +
            "WHERE pre_time BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> selectStatisticsByTimeRange(@Param("startDate") Timestamp startDate,
                                                    @Param("endDate") Timestamp endDate);

    /**
     * 根据登记类型查询登记信息
     *
     * @param type 登记类型
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE ri.reg_type = #{type}
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbytype(String type);

    /**
     * 处理登记支付状态
     *
     * @param regId      登记ID
     * @param dealerId   经手人ID
     * @param paymentType 支付类型
     */
    @Update("UPDATE registration_info SET " +
            "reg_dealer_id=#{dealerId}," +
            "reg_deal_type=#{paymentType.displayValue}," +
            "reg_state='待就诊'," +
            "reg_deal_time=NOW() " +
            "WHERE reg_id=#{regId} AND reg_state='待缴费'")
    void handlepaystate(int regId, int dealerId, RegistrationInfo.PaymentType paymentType);

    /**
     * 处理登记支付
     *
     * @param regID      登记ID
     * @param paymentType 支付类型
     */
    @Update({
            "UPDATE patient_info SET " +
                    "healthcard_balance = healthcard_balance - (" +
                    "SELECT d.doc_fee " +
                    "FROM doctor_info d " +
                    "JOIN arrange_info a ON d.doc_id = a.arrange_doc_id " +
                    "WHERE a.arrange_id = (" +
                    "SELECT r.reg_arrange_id " +
                    "FROM registration_info r " +
                    "WHERE r.reg_id = #{regID})" +
                    ") " +
                    "WHERE healthcard_id = (" +
                    "SELECT r.reg_hcard_id " +
                    "FROM registration_info r " +
                    "WHERE r.reg_id = #{regID} AND r.reg_state='待缴费' AND #{paymentType.displayValue}='就诊卡')"
    })
    void handlepayment(int regID, RegistrationInfo.PaymentType paymentType);

    /**
     * 处理登记取消状态
     *
     * @param regId    登记ID
     * @param dealerId 经手人ID
     */
    @Update("UPDATE registration_info SET " +
            "reg_dealer_id=#{dealerId}," +
            "reg_state='已退号'," +
            "reg_deal_time=NOW() " +
            "WHERE reg_id=#{regId} AND reg_state='待就诊'")
    void handlecancelstate(int regId, int dealerId);

    /**
     * 处理登记退款
     *
     * @param regID 登记ID
     */
    @Update({
            "UPDATE patient_info SET " +
                    "healthcard_balance = healthcard_balance + 0.8 * (" +
                    "SELECT d.doc_fee " +
                    "FROM doctor_info d " +
                    "JOIN arrange_info a ON d.doc_id = a.arrange_doc_id " +
                    "WHERE a.arrange_id = (" +
                    "SELECT r.reg_arrange_id " +
                    "FROM registration_info r " +
                    "WHERE r.reg_id = #{regID})" +
                    ") " +
                    "WHERE healthcard_id = (" +
                    "SELECT r.reg_hcard_id " +
                    "FROM registration_info r " +
                    "WHERE r.reg_id = #{regID} AND r.reg_state='待就诊')"
    })
    void handlecancel(int regID);

    /**
     * 插入新的登记记录
     *
     * @param registrationInfo 登记信息对象
     */
    @Insert("INSERT INTO registration_info " +
            "(reg_hcard_id, reg_state, reg_time, reg_type, " +
            "reg_fee_type, reg_consultation_type, reg_arrange_id, reg_dealer_id, " +
            "reg_deal_time, reg_deal_type) " +
            "VALUES " +
            "(#{regHcardId}, #{regState.displayValue}, NOW(), " +
            "#{regType.displayValue}, #{regFeeType.displayValue}, #{regConsultationType.displayValue}, #{regArrangeId}, " +
            "#{regDealerId}, #{regDealTime}, #{regDealType.displayValue})")
    void insertnew(RegistrationInfo registrationInfo);

    /**
     * 更新登记记录
     *
     * @param registrationInfo 更新的登记信息
     * @param id              登记ID
     */
    @Update("UPDATE registration_info SET " +
            "reg_hcard_id = #{registrationInfo.regHcardId}, " +
            "reg_state = #{registrationInfo.regState.displayValue}, " +
            "reg_time = #{registrationInfo.regTime}, " +
            "reg_type = #{registrationInfo.regType.displayValue}, " +
            "reg_fee_type = #{registrationInfo.regFeeType.displayValue}, " +
            "reg_consultation_type = #{registrationInfo.regConsultationType.displayValue}, " +
            "reg_arrange_id = #{registrationInfo.regArrangeId}, " +
            "reg_dealer_id = #{registrationInfo.regDealerId}, " +
            "reg_deal_time = #{registrationInfo.regDealTime}, " +
            "reg_deal_type = #{registrationInfo.regDealType.displayValue} " +
            "WHERE reg_id = #{id}")
    void myupdateById(RegistrationInfo registrationInfo, int id);

    /**
     * 根据ID删除登记记录
     *
     * @param id 登记ID
     */
    @Delete("DELETE FROM registration_info WHERE reg_id=#{id}")
    void mydeleteById(int id);

    /**
     * 根据日期获取患者性别统计
     *
     * @param date 统计日期
     * @return 性别及其对应的患者数量
     */
    @Select("SELECT pi.gender, COUNT(DISTINCT pi.healthcard_id) as count " +
            "FROM patient_info pi " +
            "JOIN registration_info ri ON pi.healthcard_id = ri.reg_hcard_id " +
            "WHERE DATE(ri.reg_time) = #{date} " +
            "GROUP BY pi.gender")
    @MapKey("gender")
    Map<String, Map<String, Long>> getGenderStatsByDate(String date);

    /**
     * 根据医生ID和日期及状态获取所需的登记信息
     *
     * @param docId 医生ID
     * @param date  日期
     * @param state 登记状态
     * @return 登记信息列表
     */
    @Select("""
    SELECT 
        ri.reg_id,
        ri.reg_hcard_id,
        pi.name AS reg_pname,
        doci.doc_name AS regdoc_name,
        depi.department_name AS regdep_name,
        doci.doc_fee AS reg_fee,
        ri.reg_state,
        ri.reg_time,
        ri.reg_type,
        ri.reg_fee_type,
        ri.reg_consultation_type,
        ri.reg_arrange_id,
        ai.arrange_timezone AS reg_timezone,
        ri.reg_dealer_id,
        ri.reg_deal_time,
        ri.reg_deal_type
    FROM registration_info ri
    JOIN patient_info pi ON ri.reg_hcard_id=pi.healthcard_id
    JOIN arrange_info ai ON ai.arrange_id=ri.reg_arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON doci.doc_dp_id=depi.department_id
    WHERE ri.reg_state=#{state.displayValue} 
      AND ai.arrange_date=CAST(#{date} AS DATE) 
      AND ai.arrange_doc_id=#{docId}
    """)
    @ResultMap("RegistrationResultsMap")
    List<RegistrationInfo> getbyneed(
            @Param("docId") int docId,
            @Param("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date,
            @Param("state") RegistrationInfo.RegistrationState state);
}