package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.PatientInfo;
import org.example.entity.RegistrationInfo;
import org.example.mapper.RegistrationInfoMapper;
import org.example.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RegistrationServiceImpl extends ServiceImpl<RegistrationInfoMapper, RegistrationInfo> implements RegistrationService {

    @Autowired
    private RegistrationInfoMapper registrationInfoMapper;

    /**
     * 创建新的挂号记录
     *
     * @param registrationInfo 挂号信息对象
     */
    public void createRegistration(RegistrationInfo registrationInfo) {
        registrationInfoMapper.insertnew(registrationInfo);
    }

    /**
     * 根据挂号ID获取挂号信息
     *
     * @param regId 挂号ID
     * @return 挂号信息对象
     */
    public RegistrationInfo getRegistrationById(int regId) {
        return registrationInfoMapper.getById(regId);
    }

    /**
     * 根据健康卡ID获取挂号信息列表
     *
     * @param regHcardId 健康卡ID
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByHealthCardId(int regHcardId) {
        return registrationInfoMapper.getByhcardId(regHcardId);
    }

    /**
     * 获取所有挂号信息
     *
     * @return 所有挂号信息列表
     */
    public List<RegistrationInfo> getall() {
        return registrationInfoMapper.getallreg();
    }

    /**
     * 根据患者ID获取挂号信息列表
     *
     * @param regPId 患者ID
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByPatientId(String regPId) {
        return registrationInfoMapper.getbyPId(regPId);
    }

    /**
     * 根据患者姓名获取挂号信息列表
     *
     * @param pname 患者姓名
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByPatientname(String pname) {
        return registrationInfoMapper.getbypname(pname);
    }

    /**
     * 根据排班ID获取挂号信息列表
     *
     * @param regArrangeId 排班ID
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByArrangeId(int regArrangeId) {
        return registrationInfoMapper.getbyarrangId(regArrangeId);
    }

    /**
     * 根据医生姓名获取挂号信息列表
     *
     * @param docname 医生姓名
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByDoctorName(String docname) {
        return registrationInfoMapper.getbydocname(docname);
    }

    /**
     * 根据部门名称获取挂号信息列表
     *
     * @param depname 部门名称
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByDepartmentName(String depname) {
        return registrationInfoMapper.getbydepname(depname);
    }

    /**
     * 更新挂号信息
     *
     * @param registrationInfo 更新后的挂号信息对象
     * @param id               挂号ID
     */
    public void updateRegistration(RegistrationInfo registrationInfo, int id) {
        registrationInfoMapper.myupdateById(registrationInfo, id);
    }

    /**
     * 根据挂号ID删除挂号信息
     *
     * @param regId 挂号ID
     */
    public void deleteRegistration(int regId) {
        registrationInfoMapper.mydeleteById(regId);
    }

    /**
     * 根据挂号状态获取挂号信息列表
     *
     * @param state 挂号状态
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByState(RegistrationInfo.RegistrationState state) {
        return registrationInfoMapper.getbystate(state.displayValue);
    }

    /**
     * 更新挂号状态
     *
     * @param regId 挂号ID
     * @param state 新的挂号状态
     */
    public void updateRegistrationState(int regId, RegistrationInfo.RegistrationState state) {
        registrationInfoMapper.updateState(regId, state);
    }

    /**
     * 根据时间范围获取挂号信息列表
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByTimeRange(Date startTime, Date endTime) {
        return registrationInfoMapper.getbytimerange(startTime, endTime);
    }

    /**
     * 处理挂号支付
     *
     * @param regId      挂号ID
     * @param dealerId   经销商ID
     * @param paymentType 支付类型
     */
    public void processRegistrationPayment(int regId, int dealerId, RegistrationInfo.PaymentType paymentType) {
        registrationInfoMapper.handlepayment(regId, paymentType);
        registrationInfoMapper.handlepaystate(regId, dealerId, paymentType);
    }

    /**
     * 根据类型获取挂号信息列表
     *
     * @param type 挂号类型
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationsByType(RegistrationInfo.RegistrationType type) {
        return registrationInfoMapper.getbytype(type.displayValue);
    }

    /**
     * 取消挂号
     *
     * @param regId 挂号ID
     */
    public void cancelRegistration(int regId) {
        registrationInfoMapper.handlecancel(regId);
        registrationInfoMapper.handlecancelstate(regId, 3);
    }

    /**
     * 根据日期获取性别统计信息
     *
     * @param date 查询日期
     * @return 性别统计信息映射
     */
    public Map<String, Integer> getGenderStatsByDate(String date) {
        Map<String, Map<String, Long>> rawResult = registrationInfoMapper.getGenderStatsByDate(date);

        Map<String, Integer> result = new HashMap<>();
        result.put("男", 0);
        result.put("女", 0);

        rawResult.forEach((gender, stats) -> {
            if (stats != null && stats.containsKey("count")) {
                // 安全地将 Long 转换为 Integer
                result.put(gender, stats.get("count").intValue());
            }
        });

        return result;
    }

    /**
     * 根据医生ID、日期和状态获取挂号信息
     *
     * @param docId 医生ID
     * @param date  查询日期
     * @param state 挂号状态
     * @return 挂号信息列表
     */
    public List<RegistrationInfo> getRegistrationByneed(int docId, Date date, RegistrationInfo.RegistrationState state) {
        return registrationInfoMapper.getbyneed(docId, date, state);
    }
}