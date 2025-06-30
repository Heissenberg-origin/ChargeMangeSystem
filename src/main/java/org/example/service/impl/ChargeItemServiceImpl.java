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

    public void createChargeItem(ChargeItemsInfo chargeItemsInfo){
        chargeItemsInfoMapper.insertInfo(chargeItemsInfo,"系统管理员");
    }
    public List<ChargeItemsInfo> getAllChargeItems(){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectAll();
        return list;
    }
    public ChargeItemsInfo getChargeItemById(int id){
        ChargeItemsInfo info=chargeItemsInfoMapper.selectById(id);
        return info;
    }
    public void updateChargeItem(int id, ChargeItemsInfo chargeItemsInfo){
        chargeItemsInfoMapper.updateInfo(id,chargeItemsInfo,"系统管理员");
    }
    public void deleteChargeItem(int id){
        chargeItemsInfoMapper.delete(id);
    }
    public List<ChargeItemsInfo> getChargeItemsByType(ChargeItemsInfo.ChargeItemType type){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectByType(type);
        return list;
    }
    public List<ChargeItemsInfo> getChargeItemsByDepartment(int departmentId){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectByDepartment(departmentId);
        return list;
    }
    public List<ChargeItemsInfo> getChargeItemsByStatus(ChargeItemsInfo.ItemState status) {
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectByStatus(status);
        return list;
    }
    public List<ChargeItemsInfo> searchChargeItems(String keyword){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.searchByKeyword(keyword);
        return list;
    }
    public List<ChargeItemsInfo> getChargeItemsWithLowBalance(int threshold){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectWithLowBalance(threshold);
        return list;
    }
    public void updateChargeItemStatus(int id, ChargeItemsInfo.ItemState status){
        chargeItemsInfoMapper.updateStatus(id,status,"系统管理员");
    }
    public void updateChargeItemBalance(int id, int change){
        chargeItemsInfoMapper.updateBalance(id,change,"系统管理员");
    }
    public List<ChargeItemsInfo> getChargeItemsByPriceRange(double minPrice, double maxPrice){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectByPriceRange(minPrice,maxPrice);
        return list;
    }
    public List<String> getAllChargeItemTypes(){
        List<String> list=chargeItemsInfoMapper.selectAllTypes();
        return list;
    }
    public List<ChargeItemsInfo> getChargeItemsByCreator(String creator){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectByCreator(creator);
        return list;
    }
    public List<ChargeItemsInfo> getChargeItemsByModifier(String modifier){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectByModifier(modifier);
        return list;
    }
    public List<ChargeItemsInfo> getChargeItemsCreatedBetween(Timestamp startDate, Timestamp endDate){
        List<ChargeItemsInfo> list=chargeItemsInfoMapper.selectCreatedBetween(startDate,endDate);
        return list;
    }
    public Map<String, Object> getChargeItemsStatistics(){
        Map<String, Object> map=chargeItemsInfoMapper.selectStatistics();
        return map;
    }
}
