package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.ArrangeInfo;

import java.util.Date;
import java.util.List;

public interface ArrangeService extends IService<ArrangeInfo> {

    /**
     * 获取所有排班信息
     *
     * @return 所有排班信息列表
     */
    List<ArrangeInfo> listall();

    /**
     * 根据排班ID获取排班信息
     *
     * @param id 排班ID
     * @return 排班信息
     */
    ArrangeInfo getArrangeById(int id);

    /**
     * 更新指定ID的排班信息
     *
     * @param arrange 排班信息对象
     * @param id      排班ID
     */
    void setArrangeByid(ArrangeInfo arrange, int id);

    /**
     * 根据ID删除排班信息
     *
     * @param id 排班ID
     */
    void deleteById(int id);

    /**
     * 根据医生ID获取排班信息列表
     *
     * @param docid 医生ID
     * @return 排班信息列表
     */
    List<ArrangeInfo> getArrangeInfosByDocId(int docid);

    /**
     * 根据日期获取排班信息列表
     *
     * @param date1 查询日期
     * @return 排班信息列表
     */
    List<ArrangeInfo> getArrangeInfosByDate(Date date1);

    /**
     * 根据日期范围获取排班信息列表
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 排班信息列表
     */
    List<ArrangeInfo> getArrangeInfosByDateRange(Date startDate, Date endDate);

    /**
     * 获取医生在指定日期的可用时间段
     *
     * @param doctorId 医生ID
     * @param date     查询日期
     * @return 可用时间段列表
     */
    List<ArrangeInfo.TimeSlot> getAvailableSlotsAsEnum(int doctorId, Date date);

    /**
     * 根据医生ID和日期获取排班信息
     *
     * @param doctorId 医生ID
     * @param date     查询日期
     * @return 排班信息列表
     */
    List<ArrangeInfo> getArrangeInfosByDoctorAndDate(int doctorId, Date date);

    /**
     * 获取剩余排班信息
     *
     * @return 剩余排班信息列表
     */
    List<ArrangeInfo> getArrangeInfosWithRemaining();

    /**
     * 更新排班余额
     *
     * @param id      排班ID
     * @param balance 新余额
     */
    void updateArrangeBalance(int id, int balance);

    /**
     * 减少排班余额
     *
     * @param id     排班ID
     * @param amount 减少的金额
     */
    void decreaseArrangeBalance(int id, int amount);

    /**
     * 增加排班余额
     *
     * @param id     排班ID
     * @param amount 增加的金额
     */
    void increaseArrangeBalance(int id, int amount);

    /**
     * 根据医生ID统计排班数量
     *
     * @param doctorId 医生ID
     * @return 排班数量
     */
    long countArrangeInfosByDoctorId(int doctorId);

    /**
     * 根据医生ID统计可用排班数量
     *
     * @param doctorId 医生ID
     * @return 可用排班数量
     */
    long countRemainingArrangeInfosByDoctorId(int doctorId);
}