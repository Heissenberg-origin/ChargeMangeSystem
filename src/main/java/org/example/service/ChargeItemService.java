package org.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.entity.ChargeItemsInfo;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;


public interface ChargeItemService extends IService<ChargeItemsInfo> {
    void createChargeItem(ChargeItemsInfo chargeItemsInfo);
    List<ChargeItemsInfo> getAllChargeItems();
    ChargeItemsInfo getChargeItemById(int id);
    void updateChargeItem(int id, ChargeItemsInfo chargeItemsInfo);
    void deleteChargeItem(int id);
    List<ChargeItemsInfo> getChargeItemsByType(ChargeItemsInfo.ChargeItemType type);
    List<ChargeItemsInfo> getChargeItemsByDepartment(int departmentId);
    List<ChargeItemsInfo> getChargeItemsByStatus(ChargeItemsInfo.ItemState status);
    List<ChargeItemsInfo> searchChargeItems(String keyword);
    List<ChargeItemsInfo> getChargeItemsWithLowBalance(int threshold);
    void updateChargeItemStatus(int id, ChargeItemsInfo.ItemState status);
    void updateChargeItemBalance(int id, int change);
    List<ChargeItemsInfo> getChargeItemsByPriceRange(double minPrice, double maxPrice);
    List<String> getAllChargeItemTypes();
    List<ChargeItemsInfo> getChargeItemsByCreator(String creator);
    List<ChargeItemsInfo> getChargeItemsByModifier(String modifier);
    List<ChargeItemsInfo> getChargeItemsCreatedBetween(Timestamp startDate, Timestamp endDate);
    Map<String, Object> getChargeItemsStatistics();
}
