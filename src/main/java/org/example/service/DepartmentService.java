package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.DepartmentInfo;

import java.util.List;

public interface DepartmentService extends IService<DepartmentInfo> {

    /**
     * 根据部门ID获取部门信息
     *
     * @param departmentId 部门ID
     * @return 部门信息对象
     */
    DepartmentInfo getDepartmentById(Integer departmentId);

    /**
     * 根据部门名称模糊查询部门信息
     *
     * @param departmentName 部门名称
     * @return 部门信息列表
     */
    List<DepartmentInfo> getDepartmentsByName(String departmentName);

    /**
     * 获取指定部门下的所有医生ID
     *
     * @param departmentId 部门ID
     * @return 医生ID列表
     */
    List<Integer> getalldocById(int departmentId);

    /**
     * 更新指定部门的信息
     *
     * @param departmentId   部门ID
     * @param departmentInfo 更新后的部门信息
     */
    void updatedepById(int departmentId, DepartmentInfo departmentInfo);
}