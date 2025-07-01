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
public class RegistrationServiceImpl extends ServiceImpl <RegistrationInfoMapper, RegistrationInfo> implements RegistrationService {
    @Autowired
    private RegistrationInfoMapper registrationInfoMapper;

    public void createRegistration(RegistrationInfo registrationInfo){
        registrationInfoMapper.insertnew(registrationInfo);
    }
    public RegistrationInfo getRegistrationById(int regId){
        return registrationInfoMapper.getById(regId);
    }
    public List<RegistrationInfo>  getRegistrationsByHealthCardId(int regHcardId){
        return registrationInfoMapper.getByhcardId(regHcardId);
    }
    public List<RegistrationInfo> getall(){
        return registrationInfoMapper.getallreg();
    }
    public List<RegistrationInfo> getRegistrationsByPatientId(String regPId){
        return registrationInfoMapper.getbyPId(regPId);
    }
    public List<RegistrationInfo> getRegistrationsByPatientname(String pname){
        return registrationInfoMapper.getbypname(pname);
    }
    public List<RegistrationInfo> getRegistrationsByArrangeId(int regArrangeId){
        return registrationInfoMapper.getbyarrangId(regArrangeId);
    }
    public List<RegistrationInfo> getRegistrationsByDoctorName(String docname){
        return registrationInfoMapper.getbydocname(docname);
    }
    public List<RegistrationInfo> getRegistrationsByDepartmentName(String depname){
        return registrationInfoMapper.getbydepname(depname);
    }
    public void updateRegistration(RegistrationInfo registrationInfo,int id){
        registrationInfoMapper.myupdateById(registrationInfo,id);
    }
    public void deleteRegistration(int regId){
        registrationInfoMapper.mydeleteById(regId);
    }
    public List<RegistrationInfo> getRegistrationsByState(RegistrationInfo.RegistrationState state){
        return registrationInfoMapper.getbystate(state.displayValue);
    }
    public void updateRegistrationState(int regId, RegistrationInfo.RegistrationState state){
        registrationInfoMapper.updateState(regId,state);
    }
    public List<RegistrationInfo> getRegistrationsByTimeRange(Date startTime, Date endTime){
        return registrationInfoMapper.getbytimerange(startTime,endTime);
    }
    public void processRegistrationPayment(int regId, int dealerId, RegistrationInfo.PaymentType paymentType){
            registrationInfoMapper.handlepayment(regId,paymentType);
            registrationInfoMapper.handlepaystate(regId,dealerId,paymentType);
    }
//    public Map<String, Object> getRegistrationStatistics(Date startDate, Date endDate){
//
//    }
    public List<RegistrationInfo> getRegistrationsByType(RegistrationInfo.RegistrationType type){
        return registrationInfoMapper.getbytype(type.displayValue);
    }
    public void cancelRegistration(int regId){
        registrationInfoMapper.handlecancel(regId);
        registrationInfoMapper.handlecancelstate(regId,3);
    }
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
}
