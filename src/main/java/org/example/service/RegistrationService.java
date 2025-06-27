package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.RegistrationInfo;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface RegistrationService extends IService<RegistrationInfo> {
    void createRegistration(RegistrationInfo registrationInfo);
    RegistrationInfo getRegistrationById(int regId);
    List<RegistrationInfo> getRegistrationsByHealthCardId(int regHcardId);
    List<RegistrationInfo> getall();
    List<RegistrationInfo> getRegistrationsByPatientId(String regPId);
    List<RegistrationInfo> getRegistrationsByPatientname(String pname);
    List<RegistrationInfo> getRegistrationsByArrangeId(int regArrangeId);
    List<RegistrationInfo> getRegistrationsByDoctorName(String docname);
    List<RegistrationInfo> getRegistrationsByDepartmentName(String depname);
    void updateRegistration(RegistrationInfo registrationInfo,int redid);
    void deleteRegistration(int regId);
    List<RegistrationInfo> getRegistrationsByState(RegistrationInfo.RegistrationState state);
    void updateRegistrationState(int regId, RegistrationInfo.RegistrationState state);
    List<RegistrationInfo> getRegistrationsByTimeRange(Date startTime, Date endTime);
    void processRegistrationPayment(int regId, int dealerId, RegistrationInfo.PaymentType paymentType);
    //Map<String, Object> getRegistrationStatistics(Date startDate, Date endDate);
    List<RegistrationInfo> getRegistrationsByType(RegistrationInfo.RegistrationType type);
    void cancelRegistration(int regId);
}
