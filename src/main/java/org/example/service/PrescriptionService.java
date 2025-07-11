package org.example.service;

import org.example.entity.PrescriptionInfo;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.Map;

public interface PrescriptionService {

    /**
     * 创建新的处方
     *
     * @param prescriptionInfo 处方信息列表
     */
    void createPrescription(List<PrescriptionInfo> prescriptionInfo);

    /**
     * 获取所有处方信息
     *
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getAllPrescriptions();

    /**
     * 根据序列号获取处方信息
     *
     * @param sequence 处方序列号
     * @return 处方信息对象
     */
    PrescriptionInfo getPrescriptionBySequence(int sequence);

    /**
     * 删除指定序列号的处方
     *
     * @param sequence 处方序列号
     */
    void deletePrescription(int sequence);

    /**
     * 根据处方ID获取处方信息
     *
     * @param prescriptionId 处方ID
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByPrescriptionId(int prescriptionId);

    /**
     * 根据挂号ID获取处方信息
     *
     * @param registrationId 挂号ID
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByRegistrationId(int registrationId);

    /**
     * 根据就诊卡ID获取处方信息
     *
     * @param hcardId 就诊卡ID
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByhcardId(int hcardId);

    /**
     * 根据ID类型和ID获取处方信息
     *
     * @param IDtype ID类型
     * @param Id     ID值
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByID(String IDtype, String Id);

    /**
     * 根据医生姓名获取处方信息
     *
     * @param docname 医生姓名
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsBydocname(String docname);

    /**
     * 根据部门名称获取处方信息
     *
     * @param depname 部门名称
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsBydepname(String depname);

    /**
     * 根据处方状态获取处方信息
     *
     * @param state 处方状态
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByState(PrescriptionInfo.PrescriptionState state);

    /**
     * 根据经销商ID获取处方信息
     *
     * @param dealerId 经销商ID
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByDealerId(int dealerId);

    /**
     * 根据创建时间范围获取处方信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByCreateTimeRange(Timestamp startDate, Timestamp endDate);

    /**
     * 根据支付时间范围获取处方信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByPaidTimeRange(Timestamp startDate, Timestamp endDate);

    /**
     * 根据支付类型获取处方信息
     *
     * @param paymentType 支付类型
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByPaymentType(PrescriptionInfo.PaymentType paymentType);

    /**
     * 根据收费项目ID获取处方信息
     *
     * @param chargeItemId 收费项目ID
     * @return 处方信息列表
     */
    List<PrescriptionInfo> getPrescriptionsByChargeItemId(int chargeItemId);

    /**
     * 更新处方状态
     *
     * @param sequence 处方序列号
     * @param dealerId 经销商ID
     * @param state    新的处方状态
     */
    void updatePrescriptionState(int sequence, int dealerId, PrescriptionInfo.PrescriptionState state);

    /**
     * 支付处方
     *
     * @param sequence    处方序列号
     * @param dealerId    经销商ID
     * @param paymentType 支付类型
     */
    void payPrescription(int sequence, int dealerId, PrescriptionInfo.PaymentType paymentType);

    /**
     * 退款处方
     *
     * @param sequence 处方序列号
     * @param dealerId 经销商ID
     */
    void refundPrescription(int sequence, int dealerId);

    /**
     * 获取处方统计信息
     *
     * @return 统计信息映射
     */
    Map<String, Object> getPrescriptionStatistics();

    /**
     * 根据状态获取统计信息
     *
     * @return 状态统计信息列表
     */
    List<Map<String, Integer>> getStatisticsByState();

    /**
     * 根据支付类型获取统计信息
     *
     * @param date 查询日期
     * @return 支付类型统计信息列表
     */
    List<Map<String, Object>> getStatisticsByPaymentType(Date date);

    /**
     * 根据时间范围获取统计信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间范围统计信息
     */
    Map<String, Object> getStatisticsByTimeRange(Timestamp startDate, Timestamp endDate);
}