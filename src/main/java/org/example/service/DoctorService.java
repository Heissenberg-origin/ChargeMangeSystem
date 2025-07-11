package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.DoctorInfo;

import java.util.List;

public interface DoctorService extends IService<DoctorInfo> {

    /**
     * 根据医生ID获取医生信息
     *
     * @param docId 医生ID
     * @return 医生信息对象
     */
    DoctorInfo getDoctorById(int docId);

    /**
     * 根据医生姓名模糊查询医生信息
     *
     * @param docName 医生姓名
     * @return 医生信息列表
     */
    List<DoctorInfo> searchDoctorsByName(String docName);

    /**
     * 添加新的医生信息
     *
     * @param doctorInfo 医生信息对象
     */
    void addDoctor(DoctorInfo doctorInfo);

    /**
     * 更新指定医生的信息
     *
     * @param docid      医生ID
     * @param doctorInfo 更新后的医生信息
     */
    void updateDoctor(int docid, DoctorInfo doctorInfo);

    /**
     * 根据医生ID删除医生信息
     *
     * @param docId 医生ID
     */
    void deleteDoctor(int docId);

    /**
     * 获取所有医生信息
     *
     * @return 医生信息列表
     */
    List<DoctorInfo> getalldoc();

    /**
     * 根据部门ID获取该部门的医生信息
     *
     * @param depId 部门ID
     * @return 医生信息列表
     */
    List<DoctorInfo> getDoctorBydepId(int depId);
}