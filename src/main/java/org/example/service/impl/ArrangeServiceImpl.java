package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.ArrangeInfo;
import org.example.mapper.ArrangeInfoMapper;
import org.example.service.ArrangeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ArrangeServiceImpl extends ServiceImpl<ArrangeInfoMapper, ArrangeInfo> implements ArrangeService {

    @Autowired
    private ArrangeInfoMapper arrangeInfoMapper;

    /**
     * 根据ID获取排班信息
     *
     * @param id 排班ID
     * @return 排班信息对象
     */
    public ArrangeInfo getArrangeById(int id) {
        return arrangeInfoMapper.selectArrangeById(id);
    }

    /**
     * 更新排班信息
     *
     * @param arrangeInfo 排班信息对象
     * @param id          排班ID
     */
    public void setArrangeByid(ArrangeInfo arrangeInfo, int id) {
        String timezone = arrangeInfo.arrangetimezone.displayValue;
        arrangeInfoMapper.updateArrangeInfo(arrangeInfo, timezone, id);
    }

    /**
     * 获取所有排班信息
     *
     * @return 排班信息列表
     */
    public List<ArrangeInfo> listall() {
        return arrangeInfoMapper.listallInfo();
    }

    /**
     * 根据ID删除排班信息
     *
     * @param id 排班ID
     */
    public void deleteById(int id) {
        arrangeInfoMapper.deleteArrangeById(id);
    }

    /**
     * 根据医生ID获取排班信息
     *
     * @param docid 医生ID
     * @return 排班信息列表
     */
    public List<ArrangeInfo> getArrangeInfosByDocId(int docid) {
        return arrangeInfoMapper.selectBydocId(docid);
    }

    /**
     * 根据日期获取排班信息
     *
     * @param date1 查询日期
     * @return 排班信息列表
     */
    public List<ArrangeInfo> getArrangeInfosByDate(Date date1) {
        return arrangeInfoMapper.selectBydate(date1);
    }

    /**
     * 根据日期范围获取排班信息
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 排班信息列表
     */
    public List<ArrangeInfo> getArrangeInfosByDateRange(Date startDate, Date endDate) {
        return arrangeInfoMapper.selectByDaterange(startDate, endDate);
    }

    /**
     * 获取医生在指定日期的可用时间段
     *
     * @param docid 医生ID
     * @param date  查询日期
     * @return 可用时间段列表
     */
    public List<ArrangeInfo.TimeSlot> getAvailableSlotsAsEnum(int docid, Date date) {
        List<String> slots = arrangeInfoMapper.getAvailableSlots(docid, date);
        return slots.stream()
                .map(ArrangeInfo.TimeSlot::fromDisplayValue)
                .collect(Collectors.toList());
    }

    /**
     * 根据医生ID和日期获取详细排班信息
     *
     * @param doctorId 医生ID
     * @param date     查询日期
     * @return 排班信息列表
     */
    public List<ArrangeInfo> getArrangeInfosByDoctorAndDate(int doctorId, Date date) {
        return arrangeInfoMapper.getdetailinfo(doctorId, date);
    }

    /**
     * 获取有剩余的排班信息
     *
     * @return 排班信息列表
     */
    public List<ArrangeInfo> getArrangeInfosWithRemaining() {
        return arrangeInfoMapper.getremainingInfo();
    }

    /**
     * 更新排班余额
     *
     * @param id      排班ID
     * @param balance 新余额
     */
    public void updateArrangeBalance(int id, int balance) {
        arrangeInfoMapper.updateBalance(id, balance);
    }

    /**
     * 减少排班余额
     *
     * @param id     排班ID
     * @param amount 减少的金额
     */
    public void decreaseArrangeBalance(int id, int amount) {
        arrangeInfoMapper.decreaseBalance(id, amount);
    }

    /**
     * 增加排班余额
     *
     * @param id     排班ID
     * @param amount 增加的金额
     */
    public void increaseArrangeBalance(int id, int amount) {
        arrangeInfoMapper.increaseBalance(id, amount);
    }

    /**
     * 根据医生ID统计排班数量
     *
     * @param doctorId 医生ID
     * @return 排班数量
     */
    public long countArrangeInfosByDoctorId(int doctorId) {
        return arrangeInfoMapper.countbydocid(doctorId);
    }

    /**
     * 根据医生ID统计可用排班数量
     *
     * @param doctorId 医生ID
     * @return 可用排班数量
     */
    public long countRemainingArrangeInfosByDoctorId(int doctorId) {
        return arrangeInfoMapper.countremainingbydocid(doctorId);
    }
}