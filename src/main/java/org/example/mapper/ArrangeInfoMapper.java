package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.ArrangeInfo;

import java.util.Date;
import java.util.List;

@Mapper
public interface ArrangeInfoMapper extends BaseMapper<ArrangeInfo> {

    /**
     * 根据排班ID查询排班信息
     *
     * @param id 排班ID
     * @return 排班信息
     */
    @Select("""
            SELECT 
                a.arrange_id AS arrangeId,
                a.arrange_date AS arrangeDate,
                a.arrange_timezone AS arrangeTimeZone,
                a.arrange_balance AS arrangeBalance,
                a.arrange_doc_id,
                d.doc_name AS doctorName,
                dp.department_name AS departmentName
            FROM 
                arrange_info a
            JOIN 
                doctor_info d ON a.arrange_doc_id = d.doc_id
            JOIN 
                department_info dp ON d.doc_dp_id = dp.department_id
            WHERE 
                a.arrange_id=#{id}
        """)
    @Results(id = "ArrangeResultMap", value = {
            @Result(property = "arrangeid", column = "arrangeId"),
            @Result(property = "arrangedate", column = "arrangeDate"),
            @Result(property = "arrangetimezone", column = "arrangeTimeZone"),
            @Result(property = "arrangebalance", column = "arrangeBalance"),
            @Result(property = "arrangedocid", column = "arrange_doc_id"),
            @Result(property = "docname", column = "doctorName"),
            @Result(property = "depname", column = "departmentName")
    })
    public ArrangeInfo selectArrangeById(int id);

    /**
     * 查询所有排班信息
     *
     * @return 排班信息列表
     */
    @Select("""
            SELECT 
                a.arrange_id AS arrangeId,
                a.arrange_date AS arrangeDate,
                a.arrange_timezone AS arrangeTimeZone,
                a.arrange_balance AS arrangeBalance,
                d.doc_name AS doctorName,
                dp.department_name AS departmentName
            FROM 
                arrange_info a
            JOIN 
                doctor_info d ON a.arrange_doc_id = d.doc_id
            JOIN 
                department_info dp ON d.doc_dp_id = dp.department_id
        """)
    @ResultMap(value ="ArrangeResultMap")
    public List<ArrangeInfo> listallInfo();

    /**
     * 更新排班信息
     *
     * @param arrangeInfo 排班信息对象
     * @param timezone    排班时间段
     * @param id          排班ID
     */
    @Update("UPDATE arrange_info " +
            "SET arrange_date = #{arrangeInfo.arrangedate}, " +
            "arrange_timezone = #{timezone}, " +
            "arrange_balance = #{arrangeInfo.arrangebalance}, " +
            "arrange_doc_id = #{arrangeInfo.arrangedocid} " +
            "WHERE arrange_id = #{id}")
    public void updateArrangeInfo(@Param("arrangeInfo") ArrangeInfo arrangeInfo, String timezone, int id);

    /**
     * 根据排班ID删除排班信息
     *
     * @param id 排班ID
     * @return 删除的记录数
     */
    @Delete("DELETE FROM arrange_info WHERE arrange_id=#{id}")
    public int deleteArrangeById(int id);

    /**
     * 根据医生ID查询排班信息
     *
     * @param docid 医生ID
     * @return 排班信息列表
     */
    @Select("""
            SELECT 
                a.arrange_id AS arrangeId,
                a.arrange_date AS arrangeDate,
                a.arrange_timezone AS arrangeTimeZone,
                a.arrange_balance AS arrangeBalance,
                d.doc_name AS doctorName,
                dp.department_name AS departmentName
            FROM 
                arrange_info a
            JOIN 
                doctor_info d ON a.arrange_doc_id = d.doc_id
            JOIN 
                department_info dp ON d.doc_dp_id = dp.department_id
            WHERE 
                arrange_doc_id = #{docid}
        """)
    @ResultMap(value ="ArrangeResultMap")
    public List<ArrangeInfo> selectBydocId(int docid);

    /**
     * 根据日期查询排班信息
     *
     * @param date1 查询日期
     * @return 排班信息列表
     */
    @Select("""
            SELECT 
                a.arrange_id AS arrangeId,
                a.arrange_date AS arrangeDate,
                a.arrange_timezone AS arrangeTimeZone,
                a.arrange_balance AS arrangeBalance,
                d.doc_name AS doctorName,
                dp.department_name AS departmentName
            FROM 
                arrange_info a
            JOIN 
                doctor_info d ON a.arrange_doc_id = d.doc_id
            JOIN 
                department_info dp ON d.doc_dp_id = dp.department_id
            WHERE 
                arrange_date = #{date1}
        """)
    @ResultMap(value ="ArrangeResultMap")
    public List<ArrangeInfo> selectBydate(Date date1);

    /**
     * 根据日期范围查询排班信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 排班信息列表
     */
    @Select("""
            SELECT 
                a.arrange_id AS arrangeId,
                a.arrange_date AS arrangeDate,
                a.arrange_timezone AS arrangeTimeZone,
                a.arrange_balance AS arrangeBalance,
                d.doc_name AS doctorName,
                dp.department_name AS departmentName
            FROM 
                arrange_info a
            JOIN 
                doctor_info d ON a.arrange_doc_id = d.doc_id
            JOIN 
                department_info dp ON d.doc_dp_id = dp.department_id
            WHERE 
                arrange_date BETWEEN #{startDate} AND #{endDate}
        """)
    @ResultMap(value ="ArrangeResultMap")
    public List<ArrangeInfo> selectByDaterange(Date startDate, Date endDate);

    /**
     * 获取可用的排班时间段
     *
     * @param docid 医生ID
     * @param date  查询日期
     * @return 可用时间段列表
     */
    @Select("SELECT arrange_timezone FROM arrange_info WHERE arrange_doc_id=#{docid} AND arrange_date=#{date} AND arrange_balance != 0")
    List<String> getAvailableSlots(int docid, Date date);

    /**
     * 获取指定日期和医生的详细排班信息
     *
     * @param docid 医生ID
     * @param date  查询日期
     * @return 排班信息列表
     */
    @Select("""
            SELECT 
                a.arrange_id AS arrangeId,
                a.arrange_date AS arrangeDate,
                a.arrange_timezone AS arrangeTimeZone,
                a.arrange_balance AS arrangeBalance,
                d.doc_name AS doctorName,
                dp.department_name AS departmentName
            FROM 
                arrange_info a
            JOIN 
                doctor_info d ON a.arrange_doc_id = d.doc_id
            JOIN 
                department_info dp ON d.doc_dp_id = dp.department_id
            WHERE 
                arrange_doc_id=#{docid} AND arrange_date=#{date}
        """)
    @ResultMap(value ="ArrangeResultMap")
    public List<ArrangeInfo> getdetailinfo(int docid, Date date);

    /**
     * 获取剩余排班信息
     *
     * @return 排班信息列表
     */
    @Select("""
            SELECT 
                a.arrange_id AS arrangeId,
                a.arrange_date AS arrangeDate,
                a.arrange_timezone AS arrangeTimeZone,
                a.arrange_balance AS arrangeBalance,
                d.doc_name AS doctorName,
                dp.department_name AS departmentName
            FROM 
                arrange_info a
            JOIN 
                doctor_info d ON a.arrange_doc_id = d.doc_id
            JOIN 
                department_info dp ON d.doc_dp_id = dp.department_id
            WHERE 
                arrange_balance != 0
        """)
    @ResultMap(value ="ArrangeResultMap")
    public List<ArrangeInfo> getremainingInfo();

    /**
     * 更新排班余额
     *
     * @param id      排班ID
     * @param balance 新余额
     */
    @Update("UPDATE arrange_info SET arrange_balance=#{balance} WHERE arrange_id=#{id}")
    public void updateBalance(int id, int balance);

    /**
     * 减少排班余额
     *
     * @param id     排班ID
     * @param amount 减少的金额
     */
    @Update("UPDATE arrange_info SET arrange_balance=arrange_balance-#{amount} WHERE arrange_id=#{id}")
    public void decreaseBalance(int id, int amount);

    /**
     * 增加排班余额
     *
     * @param id     排班ID
     * @param amount 增加的金额
     */
    @Update("UPDATE arrange_info SET arrange_balance=arrange_balance+#{amount} WHERE arrange_id=#{id}")
    public void increaseBalance(int id, int amount);

    /**
     * 根据医生ID统计排班数量
     *
     * @param docid 医生ID
     * @return 排班数量
     */
    @Select("SELECT count(*) FROM arrange_info WHERE arrange_doc_id=#{docid}")
    public long countbydocid(int docid);

    /**
     * 根据医生ID统计可用排班数量
     *
     * @param docid 医生ID
     * @return 可用排班数量
     */
    @Select("SELECT count(*) FROM arrange_info WHERE arrange_doc_id=#{docid} AND arrange_balance != 0")
    public long countremainingbydocid(int docid);
}