package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.PatientInfo;
import org.example.mapper.PatientInfoMapper;
import org.example.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PatientServiceImpl extends ServiceImpl<PatientInfoMapper, PatientInfo> implements PatientService {

    @Autowired
    private PatientInfoMapper patientInfoMapper;


    /**
     * 为指定的健康卡结算
     *
     * @param healthcardId 健康卡ID
     */
    public void settlehcard(int healthcardId) {
        patientInfoMapper.settle(healthcardId);
    }

    /**
     * 根据健康卡ID更新患者信息
     *
     * @param healthcardId 健康卡ID
     * @param patientInfo  更新后的患者信息
     * @return 是否更新成功
     */
    @Override
    public boolean updateByHealthcardId(int healthcardId, PatientInfo patientInfo) {
        // 确保输入不为空
        if (healthcardId == 0 || patientInfo == null) {
            throw new IllegalArgumentException("healthcardId 和 patientInfo 不能为空");
        }

        // 构建更新条件
        QueryWrapper<PatientInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("healthcard_id", healthcardId);

        // 执行更新
        return update(patientInfo, queryWrapper);
    }

    private static final BigDecimal FLOAT_MAX = BigDecimal.valueOf(Float.MAX_VALUE);

    /**
     * 为指定健康卡充值
     *
     * @param healthcardId 健康卡ID
     * @param amount       充值金额
     */
    @Override
    @Transactional
    public void recharge(int healthcardId, float amount) {
        // 参数校验
        if (amount <= 0) {
            throw new IllegalArgumentException("充值金额必须大于0");
        }

        // 检查金额是否超过float最大值
        BigDecimal amountDecimal = BigDecimal.valueOf(amount);
        if (amountDecimal.compareTo(FLOAT_MAX) > 0) {
            throw new IllegalArgumentException("充值金额超过系统限制");
        }

        // 查询患者信息（使用悲观锁防止并发问题）
        PatientInfo patient = getBaseMapper().selectOne(new QueryWrapper<PatientInfo>()
                .eq("healthcard_id", healthcardId)
                .last("FOR UPDATE")); // 加锁

        if (patient == null) {
            throw new IllegalArgumentException("就诊卡号不存在");
        }

        // 计算新余额（使用BigDecimal进行精确计算）
        BigDecimal currentBalance = BigDecimal.valueOf(patient.getHealthcard_balance());
        BigDecimal newBalance = currentBalance.add(amountDecimal);

        // 检查是否超过float最大值
        if (newBalance.compareTo(FLOAT_MAX) > 0) {
            throw new IllegalArgumentException("余额超过系统限制");
        }

        // 更新余额（转换回float）
        float updatedBalance = newBalance.floatValue();
        patient.setHealthcard_balance(updatedBalance);

        // 更新数据库
        if (!updateById(patient)) {
            throw new RuntimeException("充值失败，请重试");
        }
    }

    /**
     * 保存患者信息并返回健康卡ID
     *
     * @param patientInfo 患者信息对象
     * @return 新健康卡ID
     */
    public int mysave(PatientInfo patientInfo) {
        patientInfoMapper.insert(patientInfo);
        return patientInfoMapper.getMaxHealthcardId();
    }
}