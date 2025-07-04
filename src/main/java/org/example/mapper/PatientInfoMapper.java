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

    @Update("UPDATE patient_info SET healthcard_balance=0 WHERE healthcard_id=#{healthcardId}")
    void settle(int healthcardId);

    @Insert("INSERT INTO patient_info (" +
            "name, gender, healthcard_id, healthcard_balance, identification_type, identification_id, " +
            "birthdate, age, nationality, ethnicity, marital_status, occupation, phonenumber, " +
            "email, address, now_postcode, registered_address, registered_postcode, " +
            "guardian1_name, guardian1_relationship, guardian1_phonenum, " +
            "guardian2_name, guardian2_relationship, guardian2_phonenum, " +
            "guardian3_name, guardian3_relationship, guardian3_phonenum, " +
            "type, MIcard_id" +
            ") VALUES (" +
            "#{name}, #{gender}, #{healthcard_id}, #{healthcard_balance}, #{identification_type}, #{identification_id}, " +
            "#{birthdate}, #{age}, #{nationality}, #{ethnicity}, #{marital_status}, #{occupation}, #{phonenumber}, " +
            "#{email}, #{address}, #{now_postcode}, #{registered_address}, #{registered_postcode}, " +
            "#{guardian1_name}, #{guardian1_relationship}, #{guardian1_phonenum}, " +
            "#{guardian2_name}, #{guardian2_relationship}, #{guardian2_phonenum}, " +
            "#{guardian3_name}, #{guardian3_relationship}, #{guardian3_phonenum}, " +
            "#{type}, #{MIcard_id}" +
            ")")
    @Options(useGeneratedKeys = true, keyProperty = "healthcard_id", keyColumn = "healthcard_id")
    void saveinfo(PatientInfo patientInfo);

    @Select("select MAX(healthcard_id) FROM patient_info ")
    int getMaxHealthcardId();
}
