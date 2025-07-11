package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.DoctorInfo;

import java.util.List;

@Mapper
public interface DoctorInfoMapper extends BaseMapper<DoctorInfo> {

    /**
     * 查询所有医生信息及其对应的部门名称
     *
     * @return 医生信息列表
     */
    @Select("""
    SELECT 
        di.doc_id,
        di.doc_name,
        di.doc_rank,
        di.doc_phone,
        di.doc_dp_id,
        di.doc_fee,
        dpi.department_name as dpname
    FROM
        doctor_info di
    JOIN department_info dpi ON dpi.department_id=di.doc_dp_id
    """)
    @Results(id="DoctorResultsMap", value = {
            @Result(property = "docId", column = "doc_id"),
            @Result(property = "docName", column = "doc_name"),
            @Result(property = "docRank", column = "doc_rank"),
            @Result(property = "docPhone", column = "doc_phone"),
            @Result(property = "docDpId", column = "doc_dp_id"),
            @Result(property = "docFee", column = "doc_fee"),
            @Result(property = "docDpName", column = "dpname") // 部门名称
    })
    List<DoctorInfo> getall();

    /**
     * 根据部门ID查询医生信息
     *
     * @param id 部门ID
     * @return 医生信息列表
     */
    @Select("""
    SELECT 
        di.doc_id,
        di.doc_name,
        di.doc_rank,
        di.doc_phone,
        di.doc_dp_id,
        di.doc_fee,
        dpi.department_name as dpname
    FROM
        doctor_info di
    JOIN department_info dpi ON dpi.department_id=di.doc_dp_id
    WHERE 
        di.doc_dp_id=#{id}
    """)
    @ResultMap("DoctorResultsMap")
    List<DoctorInfo> getbydepId(int id);

    /**
     * 根据医生ID查询医生信息
     *
     * @param id 医生ID
     * @return 医生信息
     */
    @Select("""
    SELECT 
        di.doc_id,
        di.doc_name,
        di.doc_rank,
        di.doc_phone,
        di.doc_dp_id,
        di.doc_fee,
        dpi.department_name as dpname
    FROM
        doctor_info di
    JOIN department_info dpi ON dpi.department_id=di.doc_dp_id
    WHERE 
        di.doc_id=#{id}
    """)
    @ResultMap("DoctorResultsMap")
    DoctorInfo getbyId(int id);

    /**
     * 根据医生姓名模糊查询医生信息
     *
     * @param name 医生姓名
     * @return 医生信息列表
     */
    @Select("""
    SELECT 
        di.doc_id,
        di.doc_name,
        di.doc_rank,
        di.doc_phone,
        di.doc_dp_id,
        di.doc_fee,
        dpi.department_name as dpname
    FROM
        doctor_info di
    JOIN department_info dpi ON dpi.department_id=di.doc_dp_id
    WHERE 
        di.doc_name LIKE CONCAT('%', #{name}, '%')
    """)
    @ResultMap("DoctorResultsMap")
    List<DoctorInfo> searchbyname(String name);

    /**
     * 更新医生信息
     *
     * @param docid       医生ID
     * @param doctorInfo  更新后的医生信息
     */
    @Update("UPDATE doctor_info SET " +
            "doc_name=#{doctorInfo.docName}, " +
            "doc_rank=#{doctorInfo.docRank}, " +
            "doc_phone=#{doctorInfo.docPhone}, " +
            "doc_dp_id=#{doctorInfo.docDpId}, " +
            "doc_fee=#{doctorInfo.docFee} " +
            "WHERE doc_id=#{docid}")
    void updatedocbyId(int docid, DoctorInfo doctorInfo);

    // 默认继承 selectById 等方法
}