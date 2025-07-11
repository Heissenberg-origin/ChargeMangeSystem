package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.PatientInfo;

import java.util.List;

public interface PatientService extends IService<PatientInfo> {

    /**
     * 根据就诊卡ID更新患者信息
     *
     * @param healthcardId 就诊卡号
     * @param patientInfo  更新后的患者信息对象
     * @return 更新是否成功
     */
    boolean updateByHealthcardId(int healthcardId, PatientInfo patientInfo);

    /**
     * 就诊卡充值
     *
     * @param healthcardId 就诊卡号
     * @param amount       充值金额（单位：元）
     * @throws IllegalArgumentException 参数不合法时抛出
     */
    void recharge(int healthcardId, float amount);

    /**
     * 结算就诊卡
     *
     * @param healthcardId 就诊卡号
     */
    void settlehcard(int healthcardId);

    /**
     * 保存患者信息
     *
     * @param patientInfo 患者信息对象
     * @return 保存后的患者ID
     */
    int mysave(PatientInfo patientInfo);
}