package org.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
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

    @Select("select count(*) from patient_info pi where pi.gender=#{gender.displayValue}")
    int getgenderamount(PatientInfo.Gender gender);

    @Update("UPDATE patient_info SET healthcard_balance=0 WHERE healthcard_id=#{healthcardId}")
    void settle(int healthcardId);
}
