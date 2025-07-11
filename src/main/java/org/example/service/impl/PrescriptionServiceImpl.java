package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.PrescriptionInfo;
import org.example.mapper.PrescriptionInfoMapper;
import org.example.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PrescriptionServiceImpl extends ServiceImpl<PrescriptionInfoMapper, PrescriptionInfo> implements PrescriptionService {

    @Autowired
    private PrescriptionInfoMapper prescriptionInfoMapper;


    /**
     * 创建处方记录
     *
     * @param prescriptionInfos 处方信息列表
     */
    public void createPrescription(List<PrescriptionInfo> prescriptionInfos) {
        int insertedCount = prescriptionInfoMapper.batchInsertWithAutoPreId(prescriptionInfos);
        System.out.println("成功插入 " + insertedCount + " 条处方记录");
    }

    /**
     * 获取所有处方信息
     *
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getAllPrescriptions() {
        return prescriptionInfoMapper.selectAll();
    }

    /**
     * 根据序列号获取处方信息
     *
     * @param sequence 处方序列号
     * @return 处方信息对象
     */
    public PrescriptionInfo getPrescriptionBySequence(int sequence) {
        return prescriptionInfoMapper.selectBySequence(sequence);
    }

    /**
     * 删除指定序列号的处方
     *
     * @param sequence 处方序列号
     */
    public void deletePrescription(int sequence) {
        prescriptionInfoMapper.delete(sequence);
    }

    /**
     * 根据处方ID获取处方信息
     *
     * @param prescriptionId 处方ID
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByPrescriptionId(int prescriptionId) {
        return prescriptionInfoMapper.selectByPrescriptionId(prescriptionId);
    }

    /**
     * 根据就诊卡ID获取处方信息
     *
     * @param hcardId 就诊卡ID
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByhcardId(int hcardId) {
        int registrationId = prescriptionInfoMapper.getregidByHcardId(hcardId);
        return prescriptionInfoMapper.selectByRegistrationId(registrationId);
    }

    /**
     * 根据登记ID获取处方信息
     *
     * @param registrationId 登记ID
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByRegistrationId(int registrationId) {
        return prescriptionInfoMapper.selectByRegistrationId(registrationId);
    }

    /**
     * 根据ID类型和ID值获取处方信息
     *
     * @param IDtype ID类型
     * @param Id     ID值
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByID(String IDtype, String Id) {
        List<Integer> registrationIds = prescriptionInfoMapper.getregidbyidf(IDtype, Id);
        return prescriptionInfoMapper.selectByGrouprid(registrationIds);
    }

    /**
     * 根据医生姓名获取处方信息
     *
     * @param docname 医生姓名
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsBydocname(String docname) {
        List<Integer> registrationIds = prescriptionInfoMapper.getregidBydocname(docname);
        return prescriptionInfoMapper.selectByGrouprid(registrationIds);
    }

    /**
     * 根据部门名称获取处方信息
     *
     * @param depname 部门名称
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsBydepname(String depname) {
        List<Integer> registrationIds = prescriptionInfoMapper.getregidBydepname(depname);
        return prescriptionInfoMapper.selectByGrouprid(registrationIds);
    }

    /**
     * 根据处方状态获取处方信息
     *
     * @param state 处方状态
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByState(PrescriptionInfo.PrescriptionState state) {
        return prescriptionInfoMapper.selectByState(state);
    }

    /**
     * 根据经销商ID获取处方信息
     *
     * @param dealerId 经销商ID
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByDealerId(int dealerId) {
        return prescriptionInfoMapper.selectByDealerId(dealerId);
    }

    /**
     * 根据创建时间范围获取处方信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByCreateTimeRange(Timestamp startDate, Timestamp endDate) {
        return prescriptionInfoMapper.selectByCreateTimeRange(startDate, endDate);
    }

    /**
     * 根据支付时间范围获取处方信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByPaidTimeRange(Timestamp startDate, Timestamp endDate) {
        return prescriptionInfoMapper.selectByPaidTimeRange(startDate, endDate);
    }

    /**
     * 根据支付类型获取处方信息
     *
     * @param paymentType 支付类型
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByPaymentType(PrescriptionInfo.PaymentType paymentType) {
        return prescriptionInfoMapper.selectByPaymentType(paymentType);
    }

    /**
     * 根据收费项目ID获取处方信息
     *
     * @param chargeItemId 收费项目ID
     * @return 处方信息列表
     */
    public List<PrescriptionInfo> getPrescriptionsByChargeItemId(int chargeItemId) {
        return prescriptionInfoMapper.selectByChargeItemId(chargeItemId);
    }

    /**
     * 更新处方状态
     *
     * @param sequence 处方序列号
     * @param dealerId 经销商ID
     * @param state    新的处方状态
     */
    public void updatePrescriptionState(int sequence, int dealerId, PrescriptionInfo.PrescriptionState state) {
        prescriptionInfoMapper.updateState(sequence, state.getDisplayValue(), dealerId);
    }

    /**
     * 支付处方
     *
     * @param sequence    处方序列号
     * @param dealerId    经销商ID
     * @param paymentType 支付类型
     */
    public void payPrescription(int sequence, int dealerId, PrescriptionInfo.PaymentType paymentType) {
        prescriptionInfoMapper.payPrescription(sequence, dealerId, paymentType);
        prescriptionInfoMapper.updaterpayState(sequence, "待执行", dealerId, paymentType);
    }

    /**
     * 退款处方
     *
     * @param sequence 处方序列号
     * @param dealerId 经销商ID
     */
    public void refundPrescription(int sequence, int dealerId) {
        prescriptionInfoMapper.refundPrescription(sequence, dealerId);
        prescriptionInfoMapper.updateState(sequence, "已退费", dealerId);
    }

    /**
     * 获取处方统计信息
     *
     * @return 统计信息映射
     */
    public Map<String, Object> getPrescriptionStatistics() {
        return prescriptionInfoMapper.selectStatistics();
    }

    /**
     * 根据状态获取统计信息
     *
     * @return 状态统计信息列表
     */
    public List<Map<String, Integer>> getStatisticsByState() {
        return prescriptionInfoMapper.selectStatisticsByState();
    }

    /**
     * 根据支付类型获取统计信息
     *
     * @param date 查询日期
     * @return 支付类型统计信息列表
     */
    public List<Map<String, Object>> getStatisticsByPaymentType(Date date) {
        // 确保传入的是日期部分（去掉时间部分）
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date dateOnly = calendar.getTime();

        return prescriptionInfoMapper.selectStatisticsByPaymentType(dateOnly);
    }

    /**
     * 根据时间范围获取统计信息
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 时间范围统计信息
     */
    public Map<String, Object> getStatisticsByTimeRange(Timestamp startDate, Timestamp endDate) {
        return prescriptionInfoMapper.selectStatisticsByTimeRange(startDate, endDate);
    }
}