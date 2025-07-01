package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.PrescriptionInfo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Mapper
public interface PrescriptionInfoMapper extends BaseMapper<PrescriptionInfo> {

    // ==================== 基础CRUD操作 ====================

    @Select("SELECT IFNULL(MAX(pre_id), 0) FROM prescription_info")
    int selectMaxPreId();

    /**
     * 批量插入处方记录
     * @param preId 处方批次ID
     * @param list 处方记录列表
     * @return 插入记录数
     */
    @Insert({
            "<script>",
            "INSERT INTO prescription_info (",
            "pre_id, pre_reg_id, pre_content, pre_ci_id, pre_ci_num,",
            "pre_state, pre_time, pre_receipt_id, pre_dealer_id, pre_deal_type, pre_dealtime",
            ") VALUES ",
            "<foreach collection='list' item='item' separator=','>",
            "(",
            "#{preId}, #{item.preRegId}, #{item.preContent}, #{item.preCiId}, #{item.preCiNum},",
            "#{item.preState}, #{item.preTime}, #{item.preReceiptId}, #{item.preDealerId},",
            "#{item.preDealType}, #{item.preDealTime}",
            ")",
            "</foreach>",
            "</script>"
    })
    int batchInsert(@Param("preId") int preId, @Param("list") List<PrescriptionInfo> list);

    /**
     * 组合操作：自动生成preId并批量插入
     * @param list 处方记录列表
     * @return 插入记录数
     */
    default int batchInsertWithAutoPreId(List<PrescriptionInfo> list) {
        // 获取当前最大pre_id并加1
        int newPreId = selectMaxPreId() + 1;
        // 设置统一的preId和创建时间
        Date now = new Date();
        for (PrescriptionInfo item : list) {
            item.setPreId(newPreId);
            if (item.getPreTime() == null) {
                item.setPreTime(now);
            }
            if (item.getPreState() == null) {
                item.setPreState(String.valueOf(PrescriptionInfo.PrescriptionState.待缴费));
            }
        }
        // 执行批量插入
        return batchInsert(newPreId, list);
    }

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id    
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    """)
    @Results(id = "prescriptionResultMap", value = {
            @Result(property = "preSequence", column = "pre_sequence", id = true),
            @Result(property = "preId", column = "pre_id"),
            @Result(property = "preRegId", column = "pre_reg_id"),
            @Result(property = "prehcard",column="hcardId"),
            @Result(property = "prepname",column="patientName"),
            @Result(property = "predepname",column="depName"),
            @Result(property = "predocname",column="docName"),
            @Result(property = "preContent", column = "pre_content"),
            @Result(property = "preCiId", column = "pre_ci_id"),//多表查询chargeitems_info出
            @Result(property = "preCiNum", column = "pre_ci_num"),
            @Result(property = "preprice",column="prePrice"),
            @Result(property = "preState", column = "pre_state"),
            @Result(property = "preTime", column = "pre_time"),
            @Result(property = "preReceiptId", column = "pre_receipt_id"),
            @Result(property = "preDealerId", column = "pre_dealer_id"),
            @Result(property = "preDealType", column = "pre_deal_type"),
            @Result(property = "preDealTime", column = "pre_dealtime")
    })
    List<PrescriptionInfo> selectAll();




    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id    
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_sequence = #{sequence}
    """)
    @ResultMap("prescriptionResultMap")
    PrescriptionInfo selectBySequence(int sequence);

    @Delete("DELETE FROM prescription_info WHERE pre_sequence = #{sequence} ")
    void delete(int sequence);

    // ==================== 业务查询操作 ====================

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id  
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_id = #{prescriptionId}
    """)
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByPrescriptionId(int prescriptionId);

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_reg_id = #{registrationId}
    """)
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByRegistrationId(int registrationId);

    @Select("<script>" +
            "SELECT pi.pre_sequence, pi.pre_id, pi.pre_reg_id, pi.pre_content, " +
            "pi.pre_ci_id, pi.pre_ci_num, pi.pre_state, pi.pre_time, " +
            "pi.pre_receipt_id, pi.pre_dealer_id, pi.pre_deal_type, pi.pre_dealtime, " +
            "ri.reg_hcard_id AS hcardId, pti.name AS patientName, " +
            "depi.department_name AS depName, doci.doc_name AS docName, " +
            "pi.pre_ci_num * ci.chargeitem_price AS prePrice " +
            "FROM prescription_info pi " +
            "JOIN registration_info ri ON pi.pre_reg_id = ri.reg_id " +
            "JOIN chargeitems_info ci ON pi.pre_ci_id = ci.chargeitem_id " +
            "JOIN patient_info pti ON ri.reg_hcard_id = pti.healthcard_id " +
            "JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id " +
            "JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id " +
            "JOIN department_info depi ON doci.doc_dp_id = depi.department_id " +
            "WHERE pi.pre_reg_id IN " +
            "<foreach item='id' collection='grouprid' open='(' separator=',' close=')'>" +
            "   #{id}" +
            "</foreach>" +
            "</script>")
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByGrouprid(@Param("grouprid") List<Integer> grouprid);
    @Select("SELECT MAX(reg_id) FROM registration_info WHERE reg_hcard_id = #{hcardId}")
    int getregidByHcardId(int hcardId);

    @Select("SELECT reg_id from registration_info Where reg_hcard_id in (select healthcard_id FROM patient_info WHERE identification_type=#{Idtype} AND identification_id=#{Id})")
    List<Integer> getregidbyidf(String Idtype, String Id);

    @Select("SELECT reg_id FROM registration_info " +
            "WHERE reg_arrange_id IN (" +
            "SELECT arrange_id FROM arrange_info " +
            "WHERE arrange_doc_id IN (" +
            "SELECT doc_id FROM doctor_info " +
            "WHERE doc_name LIKE CONCAT('%', #{docname}, '%')))")
    List<Integer> getregidBydocname(String docname);

    @Select("SELECT reg_id FROM registration_info " +
            "WHERE reg_arrange_id IN (" +
            "SELECT arrange_id FROM arrange_info " +
            "WHERE arrange_doc_id IN (" +
            "SELECT doc_id FROM doctor_info " +
            "WHERE doc_dp_id IN (" +
            "SELECT department_id FROM department_info " + // 确保使用正确的主键
            "WHERE department_name LIKE CONCAT('%', #{depname}, '%'))))")
    List<Integer> getregidBydepname(String depname);

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_state = #{state}
    """)
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByState(PrescriptionInfo.PrescriptionState state);

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_dealer_id = #{dealerId}
    """)
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByDealerId(int dealerId);

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id    
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_time BETWEEN #{startDate} AND #{endDate}
    """)
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByCreateTimeRange(@Param("startDate") Timestamp startDate,
                                                   @Param("endDate") Timestamp endDate);

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_dealtime BETWEEN #{startDate} AND #{endDate}
    """)
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByPaidTimeRange(@Param("startDate") Timestamp startDate,
                                                 @Param("endDate") Timestamp endDate);

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id    
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_deal_type = #{paymentType}
    """)
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByPaymentType(PrescriptionInfo.PaymentType paymentType);

    @Select("""
    SELECT 
        pi.pre_sequence,
        pi.pre_id,
        pi.pre_reg_id,
        pi.pre_content, 
        pi.pre_ci_id, 
        pi.pre_ci_num, 
        pi.pre_state, 
        pi.pre_time, 
        pi.pre_receipt_id, 
        pi.pre_dealer_id, 
        pi.pre_deal_type, 
        pi.pre_dealtime,
        ri.reg_hcard_id as hcardId,
        pti.name as patientName,
        depi.department_name as depName,
        doci.doc_name as docName,
        pi.pre_ci_num * ci.chargeitem_price as prePrice
    FROM
        prescription_info pi
    JOIN registration_info ri ON pre_reg_id=ri.reg_id
    JOIN chargeitems_info ci ON pre_ci_id=ci.chargeitem_id
    JOIN patient_info pti ON ri.reg_hcard_id=pti.healthcard_id
    JOIN arrange_info ai ON ri.reg_arrange_id=ai.arrange_id
    JOIN doctor_info doci ON ai.arrange_doc_id=doci.doc_id    
    JOIN department_info depi ON  doci.doc_dp_id=depi.department_id
    WHERE 
        pre_ci_id = #{chargeItemId}
    """)
    @ResultMap("prescriptionResultMap")
    List<PrescriptionInfo> selectByChargeItemId(int chargeItemId);

    // ==================== 业务操作 ====================

    @Update("UPDATE prescription_info SET " +
            "pre_state = #{state}, " +
            "pre_dealer_id = #{dealerId} " +
            "WHERE pre_sequence = #{sequence}")
    int updateState(@Param("sequence") int sequence,
                    @Param("state") String state,
                    @Param("dealerId") Integer dealerId);

    @Update("UPDATE prescription_info SET " +
            "pre_state = #{state}, " +
            "pre_dealer_id = #{dealerId}, " +
            "pre_deal_type = #{paymentType} "+
            "WHERE pre_sequence = #{sequence}")
    int updaterpayState(@Param("sequence") int sequence,
                          @Param("state") String state,
                          @Param("dealerId") Integer dealerId,
                          @Param("paymentType") PrescriptionInfo.PaymentType paymentType);

    @Update({
            "UPDATE patient_info SET " +
                    "healthcard_balance = healthcard_balance - (SELECT pre_ci_num * ci.chargeitem_price " +
                    "FROM prescription_info pi " +
                    "JOIN chargeitems_info ci ON pi.pre_ci_id = ci.chargeitem_id " +
                    "WHERE pi.pre_sequence = #{sequence}) " +
                    "WHERE healthcard_id = (SELECT reg_hcard_id FROM registration_info WHERE reg_id = " +
                    "(SELECT pre_reg_id FROM prescription_info WHERE pre_sequence = #{sequence})) " +
                    "AND #{paymentType.displayValue} = '就诊卡' " +
                    "AND (SELECT pre_state FROM prescription_info WHERE pre_sequence = #{sequence}) = '待缴费'"
    })
    int payPrescription(@Param("sequence") int sequence,
                        @Param("dealerId") int dealerId,
                        @Param("paymentType") PrescriptionInfo.PaymentType paymentType);

    @Update({
            "UPDATE patient_info SET " +
                    "healthcard_balance = healthcard_balance + 0.8 * (SELECT pre_ci_num * ci.chargeitem_price " +
                    "FROM prescription_info pi " +
                    "JOIN chargeitems_info ci ON pi.pre_ci_id = ci.chargeitem_id " +
                    "WHERE pi.pre_sequence = #{sequence}) " +
                    "WHERE healthcard_id = (SELECT reg_hcard_id FROM registration_info WHERE reg_id = " +
                    "(SELECT pi.pre_reg_id FROM prescription_info pi WHERE pre_sequence = #{sequence})) " +
                    "AND (SELECT pre_state FROM prescription_info WHERE pre_sequence = #{sequence}) = '待执行'"
    })
    int refundPrescription(@Param("sequence") int sequence,
                           @Param("dealerId") int dealerId);

    // ==================== 统计查询操作 ====================

    @Select("SELECT " +
            "COUNT(*) AS totalCount, " +
            "SUM(pre_ci_num) AS totalItems, " +
            "SUM(pre_ci_num * (SELECT chargeitem_price FROM chargeitems_info WHERE chargeitem_id = pre_ci_id)) AS totalAmount " +
            "FROM prescription_info")
    Map<String, Object> selectStatistics();

    @Select("SELECT pre_state AS state, COUNT(*) AS count FROM prescription_info GROUP BY pre_state")
    List<Map<String, Integer>> selectStatisticsByState();

    @Select("SELECT " +
            "pre_deal_type AS paymentType, " +
            "COUNT(*) AS count, " +
            "SUM(pre_ci_num * (SELECT chargeitem_price FROM chargeitems_info WHERE chargeitem_id = pre_ci_id)) AS amount " +
            "FROM prescription_info " +
            "WHERE pre_deal_type IS NOT NULL " +
            "AND DATE(pre_time) = DATE(#{date}) " +
            "GROUP BY pre_deal_type")
    List<Map<String, Object>> selectStatisticsByPaymentType(Date date);

    @Select("SELECT " +
            "COUNT(*) AS count, " +
            "SUM(pre_ci_num * (SELECT chargeitem_price FROM chargeitems_info WHERE chargeitem_id = pre_ci_id)) AS amount " +
            "FROM prescription_info " +
            "WHERE pre_time BETWEEN #{startDate} AND #{endDate}")
    Map<String, Object> selectStatisticsByTimeRange(@Param("startDate") Timestamp startDate,
                                                    @Param("endDate")Timestamp endDate);
}