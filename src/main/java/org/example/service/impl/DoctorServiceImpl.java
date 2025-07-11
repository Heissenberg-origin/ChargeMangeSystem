package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.DoctorInfo;
import org.example.mapper.DoctorInfoMapper;
import org.example.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl extends ServiceImpl<DoctorInfoMapper, DoctorInfo> implements DoctorService {

    @Autowired
    private final DoctorInfoMapper doctorInfoMapper;


    // 构造函数，注入 DoctorInfoMapper
    public DoctorServiceImpl(DoctorInfoMapper doctorInfoMapper) {
        this.doctorInfoMapper = doctorInfoMapper;
    }

    /**
     * 根据部门ID获取医生列表
     *
     * @param depId 部门ID
     * @return 医生信息列表
     */
    public List<DoctorInfo> getDoctorBydepId(int depId) {
        return doctorInfoMapper.getbydepId(depId);
    }

    /**
     * 根据医生ID获取医生信息
     *
     * @param docId 医生ID
     * @return 医生信息对象
     */
    public DoctorInfo getDoctorById(int docId) {
        return doctorInfoMapper.getbyId(docId);
    }

    /**
     * 根据医生姓名搜索医生信息
     *
     * @param docName 医生姓名
     * @return 医生信息列表
     */
    public List<DoctorInfo> searchDoctorsByName(String docName) {
        return doctorInfoMapper.searchbyname(docName);
    }

    /**
     * 添加新的医生信息
     *
     * @param doctorInfo 医生信息对象
     */
    public void addDoctor(DoctorInfo doctorInfo) {
        doctorInfoMapper.insert(doctorInfo);
    }

    /**
     * 更新医生信息
     *
     * @param docid      医生ID
     * @param doctorInfo 更新后的医生信息对象
     */
    public void updateDoctor(int docid, DoctorInfo doctorInfo) {
        doctorInfoMapper.updatedocbyId(docid, doctorInfo);
    }

    /**
     * 根据医生ID删除医生信息
     *
     * @param docId 医生ID
     */
    public void deleteDoctor(int docId) {
        doctorInfoMapper.deleteById(docId);
    }

    /**
     * 获取所有医生信息
     *
     * @return 医生信息列表
     */
    public List<DoctorInfo> getalldoc() {
        return doctorInfoMapper.getall();
    }
}