package org.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.DepartmentInfo;
import org.example.mapper.DepartmentInfoMapper;
import org.example.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentInfoMapper, DepartmentInfo> implements DepartmentService {

    @Autowired
    private final DepartmentInfoMapper departmentInfoMapper;



    // 构造函数，注入 DepartmentInfoMapper
    public DepartmentServiceImpl(DepartmentInfoMapper departmentInfoMapper) {
        this.departmentInfoMapper = departmentInfoMapper;
    }

    /**
     * 获取指定部门下所有医生的ID
     *
     * @param departmentId 部门ID
     * @return 医生ID列表
     */
    public List<Integer> getalldocById(int departmentId) {
        return departmentInfoMapper.getalldoc(departmentId);
    }

    /**
     * 根据部门ID更新部门信息
     *
     * @param departmentId   部门ID
     * @param departmentInfo 更新后的部门信息
     */
    public void updatedepById(int departmentId, DepartmentInfo departmentInfo) {
        departmentInfoMapper.updatebyid(departmentId, departmentInfo);
    }

    /**
     * 根据部门ID获取部门信息
     *
     * @param departmentId 部门ID
     * @return 部门信息对象
     */
    @Override
    public DepartmentInfo getDepartmentById(Integer departmentId) {
        return departmentInfoMapper.selectById(departmentId);
    }

    /**
     * 根据部门名称获取部门列表
     *
     * @param departmentName 部门名称
     * @return 部门信息列表
     */
    @Override
    public List<DepartmentInfo> getDepartmentsByName(String departmentName) {
        QueryWrapper<DepartmentInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.like("department_name", departmentName);
        return departmentInfoMapper.selectList(queryWrapper);
    }
}