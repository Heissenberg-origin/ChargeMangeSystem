package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.ChargeItemsInfo;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public interface ChargeItemService extends IService<ChargeItemsInfo> {

    /**
     * 创建新的收费项目
     *
     * @param chargeItemsInfo 收费项目对象
     */
    void createChargeItem(ChargeItemsInfo chargeItemsInfo);

    /**
     * 获取所有收费项目
     *
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getAllChargeItems();

    /**
     * 根据ID获取收费项目
     *
     * @param id 收费项目ID
     * @return 收费项目对象
     */
    ChargeItemsInfo getChargeItemById(int id);

    /**
     * 更新收费项目信息
     *
     * @param id              收费项目ID
     * @param chargeItemsInfo 更新后的收费项目对象
     */
    void updateChargeItem(int id, ChargeItemsInfo chargeItemsInfo);

    /**
     * 根据ID删除收费项目
     *
     * @param id 收费项目ID
     */
    void deleteChargeItem(int id);

    /**
     * 根据类型获取收费项目
     *
     * @param type 收费项目类型
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getChargeItemsByType(ChargeItemsInfo.ChargeItemType type);

    /**
     * 根据部门ID获取收费项目
     *
     * @param departmentId 部门ID
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getChargeItemsByDepartment(int departmentId);

    /**
     * 根据状态获取收费项目
     *
     * @param status 收费项目状态
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getChargeItemsByStatus(ChargeItemsInfo.ItemState status);

    /**
     * 根据关键字搜索收费项目
     *
     * @param keyword 查询关键字
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> searchChargeItems(String keyword);

    /**
     * 获取余额低于指定阈值的收费项目
     *
     * @param threshold 余额阈值
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getChargeItemsWithLowBalance(int threshold);

    /**
     * 更新收费项目状态
     *
     * @param id     收费项目ID
     * @param status 新的状态
     */
    void updateChargeItemStatus(int id, ChargeItemsInfo.ItemState status);

    /**
     * 更新收费项目的余额
     *
     * @param id    收费项目ID
     * @param change 变化的金额
     */
    void updateChargeItemBalance(int id, int change);

    /**
     * 根据价格区间获取收费项目
     *
     * @param minPrice 最小价格
     * @param maxPrice 最大价格
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getChargeItemsByPriceRange(double minPrice, double maxPrice);

    /**
     * 获取所有收费项目的类型
     *
     * @return 收费项目类型列表
     */
    List<String> getAllChargeItemTypes();

    /**
     * 根据创建人获取收费项目
     *
     * @param creator 创建人
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getChargeItemsByCreator(String creator);

    /**
     * 根据最后修订者获取收费项目
     *
     * @param modifier 最后修订者
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getChargeItemsByModifier(String modifier);

    /**
     * 获取在指定时间范围内创建的收费项目
     *
     * @param startDate 开始时间
     * @param endDate   结束时间
     * @return 收费项目列表
     */
    List<ChargeItemsInfo> getChargeItemsCreatedBetween(Timestamp startDate, Timestamp endDate);

    /**
     * 获取收费项目统计信息
     *
     * @return 统计信息
     */
    Map<String, Object> getChargeItemsStatistics();
}