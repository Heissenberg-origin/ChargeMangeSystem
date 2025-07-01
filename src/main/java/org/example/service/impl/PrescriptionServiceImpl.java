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

    public void createPrescription(List<PrescriptionInfo> prescriptionInfos){
        int insertedCount = prescriptionInfoMapper.batchInsertWithAutoPreId(prescriptionInfos);
        System.out.println("成功插入 " + insertedCount + " 条处方记录");
    }
    public List<PrescriptionInfo> getAllPrescriptions(){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectAll();
        return list;
    }
    public PrescriptionInfo getPrescriptionBySequence(int sequence){
        PrescriptionInfo pre=prescriptionInfoMapper.selectBySequence(sequence);
        return pre;
    }
    public void deletePrescription(int sequence){
        prescriptionInfoMapper.delete(sequence);
    }
    public List<PrescriptionInfo> getPrescriptionsByPrescriptionId(int prescriptionId){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectByPrescriptionId(prescriptionId);
        return list;
    }
    public List<PrescriptionInfo>getPrescriptionsByhcardId(int hcardId){
        int i=prescriptionInfoMapper.getregidByHcardId(hcardId);
        List<PrescriptionInfo> list=prescriptionInfoMapper.selectByRegistrationId(i);
        return list;
    }
    public List<PrescriptionInfo> getPrescriptionsByRegistrationId(int registrationId){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectByRegistrationId(registrationId);
        return list;
    }

    public List<PrescriptionInfo>getPrescriptionsByID(String IDtype,String Id){
        List<Integer>list1=prescriptionInfoMapper.getregidbyidf(IDtype,Id);
        List<PrescriptionInfo>list2=prescriptionInfoMapper.selectByGrouprid(list1);
        return list2;
    }
    public List<PrescriptionInfo>getPrescriptionsBydocname(String docname){
        List<Integer>list1=prescriptionInfoMapper.getregidBydocname(docname);
        List<PrescriptionInfo>list2=prescriptionInfoMapper.selectByGrouprid(list1);
        return list2;
    }
    public List<PrescriptionInfo>getPrescriptionsBydepname(String depname){
        List<Integer>list1=prescriptionInfoMapper.getregidBydepname(depname);
        List<PrescriptionInfo>list2=prescriptionInfoMapper.selectByGrouprid(list1);
        return list2;
    }

    public List<PrescriptionInfo> getPrescriptionsByState(PrescriptionInfo.PrescriptionState state){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectByState(state);
        return list;
    }
    public List<PrescriptionInfo> getPrescriptionsByDealerId(int dealerId){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectByDealerId(dealerId);
        return list;
    }
    public List<PrescriptionInfo> getPrescriptionsByCreateTimeRange(Timestamp startDate, Timestamp endDate){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectByCreateTimeRange(startDate,endDate);
        return list;
    }
    public List<PrescriptionInfo> getPrescriptionsByPaidTimeRange(Timestamp startDate, Timestamp endDate){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectByPaidTimeRange(startDate,endDate);
        return list;
    }
    public List<PrescriptionInfo> getPrescriptionsByPaymentType(PrescriptionInfo.PaymentType paymentType){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectByPaymentType(paymentType);
        return list;
    }
    public List<PrescriptionInfo> getPrescriptionsByChargeItemId(int chargeItemId){
        List<PrescriptionInfo>list=prescriptionInfoMapper.selectByChargeItemId(chargeItemId);
        return list;
    }

    public void updatePrescriptionState(int sequence,int dealerId,PrescriptionInfo.PrescriptionState state){
        prescriptionInfoMapper.updateState(sequence, state.getDisplayValue(),dealerId);
    }
    public void payPrescription(int sequence, int dealerId, PrescriptionInfo.PaymentType paymentType){
        prescriptionInfoMapper.payPrescription(sequence,dealerId,paymentType);
        prescriptionInfoMapper.updaterpayState(sequence,"待执行",dealerId,paymentType);

    }
    public void refundPrescription(int sequence, int dealerId){
        prescriptionInfoMapper.refundPrescription(sequence,dealerId);
        prescriptionInfoMapper.updateState(sequence,"已退费",dealerId);

    }

    public Map<String, Object> getPrescriptionStatistics(){
        Map<String, Object>map=prescriptionInfoMapper.selectStatistics();
        return map;
    }
    public List<Map<String, Integer>> getStatisticsByState(){
        List<Map<String, Integer>>map=prescriptionInfoMapper.selectStatisticsByState();
        return map;
    }
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
    public Map<String, Object> getStatisticsByTimeRange(Timestamp startDate, Timestamp endDate){
        Map<String, Object>map=prescriptionInfoMapper.selectStatisticsByTimeRange(startDate,endDate);
        return map;
    }
}
