package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.example.entity.PatientInfo;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author MF
 * @since 2024-08-14
 */
@Mapper
public interface PatientInfoMapper extends BaseMapper<PatientInfo> {
    // void save(User user);

    //就诊卡余额结算
    @Update("UPDATE patient_info SET healthcard_balance=0 WHERE healthcard_id=#{healthcardId}")
    void settle(int healthcardId);

    //获取最新挂号记录
    @Select("select MAX(healthcard_id) FROM patient_info ")
    int getMaxHealthcardId();
}
