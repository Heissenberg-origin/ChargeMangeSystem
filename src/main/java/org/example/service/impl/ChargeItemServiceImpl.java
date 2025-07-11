package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.ChargeItemsInfo;
import org.example.mapper.ChargeItemsInfoMapper;
import org.example.service.ChargeItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Service
public class ChargeItemServiceImpl extends ServiceImpl<ChargeItemsInfoMapper, ChargeItemsInfo> implements ChargeItemService {

    @Autowired
    private ChargeItemsInfoMapper chargeItemsInfoMapper;


    /**
     * 创建新的收费项目
     *
     * @param chargeItemsInfo 收费项目对象
     */
    public void createChargeItem(ChargeItemsInfo chargeItemsInfo) {
        chargeItemsInfoMapper.insertInfo(chargeItemsInfo, "系统管理员");
    }

    /**
     * 获取所有收费项目
     *
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getAllChargeItems() {
        return chargeItemsInfoMapper.selectAll();
    }

    /**
     * 根据ID获取收费项目
     *
     * @param id 收费项目ID
     * @return 收费项目对象
     */
    public ChargeItemsInfo getChargeItemById(int id) {
        return chargeItemsInfoMapper.selectById(id);
    }

    /**
     * 更新收费项目
     *
     * @param id              收费项目ID
     * @param chargeItemsInfo 更新后的收费项目对象
     */
    public void updateChargeItem(int id, ChargeItemsInfo chargeItemsInfo) {
        chargeItemsInfoMapper.updateInfo(id, chargeItemsInfo, "系统管理员");
    }

    /**
     * 根据ID删除收费项目
     *
     * @param id 收费项目ID
     */
    public void deleteChargeItem(int id) {
        chargeItemsInfoMapper.delete(id);
    }

    /**
     * 根据类型获取收费项目
     *
     * @param type 收费项目类型
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getChargeItemsByType(ChargeItemsInfo.ChargeItemType type) {
        return chargeItemsInfoMapper.selectByType(type);
    }

    /**
     * 根据部门ID获取收费项目
     *
     * @param departmentId 部门ID
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getChargeItemsByDepartment(int departmentId) {
        return chargeItemsInfoMapper.selectByDepartment(departmentId);
    }

    /**
     * 根据状态获取收费项目
     *
     * @param status 收费项目状态
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getChargeItemsByStatus(ChargeItemsInfo.ItemState status) {
        return chargeItemsInfoMapper.selectByStatus(status);
    }

    /**
     * 根据关键字搜索收费项目
     *
     * @param keyword 查询关键字
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> searchChargeItems(String keyword) {
        return chargeItemsInfoMapper.searchByKeyword(keyword);
    }

    /**
     * 获取余额低于指定阈值的收费项目
     *
     * @param threshold 余额阈值
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getChargeItemsWithLowBalance(int threshold) {
        return chargeItemsInfoMapper.selectWithLowBalance(threshold);
    }

    /**
     * 更新收费项目状态
     *
     * @param id     收费项目ID
     * @param status 新的状态
     */
    public void updateChargeItemStatus(int id, ChargeItemsInfo.ItemState status) {
        chargeItemsInfoMapper.updateStatus(id, status, "系统管理员");
    }

    /**
     * 更新收费项目的余额
     *
     * @param id    收费项目ID
     * @param change 变化的金额
     */
    public void updateChargeItemBalance(int id, int change) {
        chargeItemsInfoMapper.updateBalance(id, change, "系统管理员");
    }

    /**
     * 根据价格范围获取收费项目
     *
     * @param minPrice 最小价格
     * @param maxPrice 最大价格
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getChargeItemsByPriceRange(double minPrice, double maxPrice) {
        return chargeItemsInfoMapper.selectByPriceRange(minPrice, maxPrice);
    }

    /**
     * 获取所有收费项目类型
     *
     * @return 收费项目类型列表
     */
    public List<String> getAllChargeItemTypes() {
        return chargeItemsInfoMapper.selectAllTypes();
    }

    /**
     * 根据创建人获取收费项目
     *
     * @param creator 创建人
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getChargeItemsByCreator(String creator) {
        return chargeItemsInfoMapper.selectByCreator(creator);
    }

    /**
     * 根据最后修订者获取收费项目
     *
     * @param modifier 最后修订者
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getChargeItemsByModifier(String modifier) {
        return chargeItemsInfoMapper.selectByModifier(modifier);
    }

    /**
     * 获取在指定时间范围内创建的收费项目
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 收费项目列表
     */
    public List<ChargeItemsInfo> getChargeItemsCreatedBetween(Timestamp startDate, Timestamp endDate) {
        return chargeItemsInfoMapper.selectCreatedBetween(startDate, endDate);
    }

    /**
     * 获取收费项目统计信息
     *
     * @return 统计信息映射
     */
    public Map<String, Object> getChargeItemsStatistics() {
        return chargeItemsInfoMapper.selectStatistics();
    }
}