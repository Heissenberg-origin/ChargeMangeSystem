package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.PatientInfo;
import org.example.entity.RegistrationInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RegistrationService extends IService<RegistrationInfo> {

    /**
     * 创建新的挂号信息
     *
     * @param registrationInfo 挂号信息对象
     */
    void createRegistration(RegistrationInfo registrationInfo);

    /**
     * 根据挂号ID获取挂号信息
     *
     * @param regId 挂号ID
     * @return 挂号信息对象
     */
    RegistrationInfo getRegistrationById(int regId);

    /**
     * 根据就诊卡ID获取挂号信息列表
     *
     * @param regHcardId 就诊卡ID
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByHealthCardId(int regHcardId);

    /**
     * 获取所有挂号信息
     *
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getall();

    /**
     * 根据患者ID获取挂号信息列表
     *
     * @param regPId 患者ID
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByPatientId(String regPId);

    /**
     * 根据患者姓名获取挂号信息列表
     *
     * @param pname 患者姓名
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByPatientname(String pname);

    /**
     * 根据排班ID获取挂号信息列表
     *
     * @param regArrangeId 排班ID
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByArrangeId(int regArrangeId);

    /**
     * 根据医生姓名获取挂号信息列表
     *
     * @param docname 医生姓名
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByDoctorName(String docname);

    /**
     * 根据部门名称获取挂号信息列表
     *
     * @param depname 部门名称
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByDepartmentName(String depname);

    /**
     * 更新挂号信息
     *
     * @param registrationInfo 更新后的挂号信息对象
     * @param redid            挂号ID
     */
    void updateRegistration(RegistrationInfo registrationInfo, int redid);

    /**
     * 根据挂号ID删除挂号信息
     *
     * @param regId 挂号ID
     */
    void deleteRegistration(int regId);

    /**
     * 根据状态获取挂号信息列表
     *
     * @param state 挂号状态
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByState(RegistrationInfo.RegistrationState state);

    /**
     * 更新挂号状态
     *
     * @param regId 挂号ID
     * @param state 新的挂号状态
     */
    void updateRegistrationState(int regId, RegistrationInfo.RegistrationState state);

    /**
     * 根据时间范围获取挂号信息列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByTimeRange(Date startTime, Date endTime);

    /**
     * 处理挂号支付
     *
     * @param regId      挂号ID
     * @param dealerId   经销商ID
     * @param paymentType 支付类型
     */
    void processRegistrationPayment(int regId, int dealerId, RegistrationInfo.PaymentType paymentType);

    /**
     * 根据挂号类型获取挂号信息列表
     *
     * @param type 挂号类型
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationsByType(RegistrationInfo.RegistrationType type);

    /**
     * 取消挂号
     *
     * @param regId 挂号ID
     */
    void cancelRegistration(int regId);

    /**
     * 根据日期获取性别统计信息
     *
     * @param date 查询日期
     * @return 性别统计数据映射
     */
    Map<String, Integer> getGenderStatsByDate(String date);

    /**
     * 根据医生ID、日期和状态获取挂号信息
     *
     * @param docId 医生ID
     * @param date  查询日期
     * @param state 挂号状态
     * @return 挂号信息列表
     */
    List<RegistrationInfo> getRegistrationByneed(int docId, Date date, RegistrationInfo.RegistrationState state);
}