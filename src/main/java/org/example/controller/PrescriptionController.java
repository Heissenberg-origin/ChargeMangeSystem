package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.common.Result;
import org.example.entity.PrescriptionInfo;
import org.example.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/prescription")
@Tag(name = "处方管理", description = "处方的创建、查询、更新和状态变更等操作")
public class PrescriptionController {

    @Autowired
    private PrescriptionService prescriptionService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    // ==================== 基础CRUD接口 ====================

    @Operation(summary = "批量创建处方", description = "批量添加新的处方信息")
    @PostMapping("/create")
    public void createPrescription(
            @Parameter(description = "处方信息", required = true)
            @RequestBody List<PrescriptionInfo> prescriptionInfos) {
        prescriptionService.createPrescription(prescriptionInfos);
        System.out.println("处方创建成功");
    }
    
    @Operation(summary = "获取所有处方", description = "获取系统中所有处方列表")
    @GetMapping("/findall")
    public Result getAllPrescriptions() {
        return Result.success(prescriptionService.getAllPrescriptions());
    }

    @Operation(summary = "根据sequence获取处方", description = "根据处方序号获取详细的处方信息")
    @GetMapping("/queryById/{sequence}")
    public Result getPrescriptionBySequence(
            @Parameter(description = "处方序号", required = true)
            @PathVariable int sequence) {
        return Result.success(prescriptionService.getPrescriptionBySequence(sequence));
    }

    @Operation(summary = "删除处方", description = "根据处方序号删除处方")
    @DeleteMapping("/deleteById/{sequence}")
    public void deletePrescription(
            @Parameter(description = "处方序号", required = true)
            @PathVariable int sequence) {
        prescriptionService.deletePrescription(sequence);
        System.out.println("已完成删除："+sequence);
    }

    // ==================== 业务查询接口 ====================

    @Operation(summary = "根据处方号查询", description = "获取指定处方号的所有处方项")
    @GetMapping("/queryByPrescriptionId/{prescriptionId}")
    public Result getPrescriptionsByPrescriptionId(
            @Parameter(description = "处方号", required = true)
            @PathVariable int prescriptionId) {
        return Result.success(prescriptionService.getPrescriptionsByPrescriptionId(prescriptionId));
    }

    @Operation(summary = "根据门诊号查询", description = "获取指定门诊号的所有处方")
    @GetMapping("/queryByRegistrationId/{registrationId}")
    public Result getPrescriptionsByRegistrationId(
            @Parameter(description = "门诊号", required = true)
            @PathVariable int registrationId) {
        return Result.success(prescriptionService.getPrescriptionsByRegistrationId(registrationId));
    }

    @Operation(summary = "根据就诊卡号查询", description = "获取指定就诊号的所有处方")
    @GetMapping("/queryByhcardId/{hcardId}")
    public Result getPrescriptionsByhcardId(
            @Parameter(description = "就诊卡号", required = true)
            @PathVariable int hcardId) {
        return Result.success(prescriptionService.getPrescriptionsByhcardId(hcardId));
    }

    @Operation(summary = "根据证件号查询", description = "获取指定证件号的所有处方")
    @GetMapping("/queryByIdf/{Id}")
    public Result getPrescriptionsById(
            @Parameter(description = "证件类型", required = true)
            @RequestParam String type,
            @Parameter(description = "证件号", required = true)
            @PathVariable String Id) {
        return Result.success(prescriptionService.getPrescriptionsByID(type,Id));
    }

    @Operation(summary = "根据医生查询", description = "获取指定医生的所有处方")
    @GetMapping("/queryBydoc")
    public Result getPrescriptionsBydoc(
            @Parameter(description = "医生姓名", required = true)
            @RequestParam String docname) {
        return Result.success(prescriptionService.getPrescriptionsBydocname(docname));
    }

    @Operation(summary = "根据部门查询", description = "获取指定部门的所有处方")
    @GetMapping("/queryBydep")
    public Result getPrescriptionsBydep(
            @Parameter(description = "部门名", required = true)
            @RequestParam String depname) {
        return Result.success(prescriptionService.getPrescriptionsBydepname(depname));
    }

    @Operation(summary = "根据状态查询", description = "获取指定状态的所有处方")
    @GetMapping("/queryByStatus/{status}")
    public Result getPrescriptionsByState(
            @Parameter(description = "处方状态", required = true)
            @PathVariable PrescriptionInfo.PrescriptionState status) {
        return Result.success(prescriptionService.getPrescriptionsByState(status));
    }

    @Operation(summary = "根据收费员查询", description = "获取指定收费员处理的所有处方")
    @GetMapping("/queryByDealerId/{dealerId}")
    public Result getPrescriptionsByDealerId(
            @Parameter(description = "收费员ID", required = true)
            @PathVariable int dealerId) {
        return Result.success(prescriptionService.getPrescriptionsByDealerId(dealerId));
    }

    @Operation(summary = "根据开方时间范围查询", description = "获取指定时间范围内开方的所有处方")
    @GetMapping("/queryByCreatedRange")
    public Result getPrescriptionsByCreateTimeRange(
            @Parameter(description = "开始时间(yyyy-MM-dd HH:mm:ss)", required = true)
            @RequestParam Timestamp startDate,
            @Parameter(description = "结束时间(yyyy-MM-dd HH:mm:ss)", required = true)
            @RequestParam Timestamp endDate) {
        return Result.success(prescriptionService.getPrescriptionsByCreateTimeRange(startDate, endDate));
    }

    @Operation(summary = "根据收费时间范围查询", description = "获取指定时间范围内收费的所有处方")
    @GetMapping("/queryByPaidRange")
    public Result getPrescriptionsByPaidTimeRange(
            @Parameter(description = "开始时间(yyyy-MM-dd HH:mm:ss)", required = true)
            @RequestParam Timestamp startDate,
            @Parameter(description = "结束时间(yyyy-MM-dd HH:mm:ss)", required = true)
            @RequestParam Timestamp endDate) {
        return Result.success(prescriptionService.getPrescriptionsByPaidTimeRange(startDate, endDate));
    }

    @Operation(summary = "根据支付类型查询", description = "获取指定支付类型的所有处方")
    @GetMapping("/queryByPaymentType/{paymentType}")
    public Result getPrescriptionsByPaymentType(
            @Parameter(description = "支付类型", required = true)
            @PathVariable PrescriptionInfo.PaymentType paymentType) {
        return Result.success(prescriptionService.getPrescriptionsByPaymentType(paymentType));
    }

    @Operation(summary = "根据项目ID查询", description = "获取包含指定收费项目的所有处方")
    @GetMapping("/queryByChargeItemId/{chargeItemId}")
    public Result getPrescriptionsByChargeItemId(
            @Parameter(description = "收费项目ID", required = true)
            @PathVariable int chargeItemId) {
        return Result.success(prescriptionService.getPrescriptionsByChargeItemId(chargeItemId));
    }

    // ==================== 业务操作接口 ====================

    @Operation(summary = "更新处方状态", description = "更新指定处方的状态")
    @PatchMapping("/updateStatus/{sequence}")
    public void updatePrescriptionState(
            @Parameter(description = "处方序号", required = true)
            @PathVariable int sequence,
            @Parameter(description = "收费员ID", required = true)
            @RequestParam int dealerId,
            @Parameter(description = "处方状态", required = true)
            @RequestParam PrescriptionInfo.PrescriptionState state) {
            prescriptionService.updatePrescriptionState(sequence, dealerId,state);
            System.out.println("已将"+sequence+"的状态改为"+state);
    }

    @Operation(summary = "收费操作", description = "对处方进行收费操作")
    @PutMapping("/pay/{sequence}/{dealerId}/{paymentType}")
    public void payPrescription(
            @Parameter(description = "处方序号", required = true)
            @PathVariable int sequence,
            @Parameter(description = "收费员ID", required = true)
            @PathVariable int dealerId,
            @Parameter(description = "支付类型", required = true)
            @PathVariable PrescriptionInfo.PaymentType paymentType) {
        prescriptionService.payPrescription(sequence, dealerId, paymentType);
        System.out.println("已完成收费："+sequence);
    }

    @Operation(summary = "退费操作", description = "对处方进行退费操作")
    @PutMapping("/refund/{sequence}/{dealerId}")
    public void refundPrescription(
            @Parameter(description = "处方序号", required = true)
            @PathVariable int sequence,
            @Parameter(description = "操作员ID", required = true)
            @PathVariable int dealerId) {
        prescriptionService.refundPrescription(sequence, dealerId);
    }

    // ==================== 统计接口 ====================

    @Operation(summary = "获取处方统计", description = "获取处方数量、金额等统计信息")
    @GetMapping("/getStatistics")
    public Result getPrescriptionStatistics() {
        return Result.success(prescriptionService.getPrescriptionStatistics());
    }

    @Operation(summary = "按状态统计", description = "按状态分组统计处方数量")
    @GetMapping("/getStatisticsByState")
    public Result getStatisticsByState() {
        return Result.success(prescriptionService.getStatisticsByState());
    }

    @Operation(summary = "按支付类型统计", description = "按支付类型分组统计当日处方数量和金额")
    @GetMapping("/getStatisticsByPaymentType")
    public Result getStatisticsByPaymentType(
            @Parameter(description = "日期(yyyy-MM-dd)", required = true, example = "2025-06-30")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date date) {
        return Result.success(prescriptionService.getStatisticsByPaymentType(date));
    }

    @Operation(summary = "按时间段统计", description = "按时间段统计处方数量和金额")
    @GetMapping("/getStatisticsByTimeRange")
    public Result getStatisticsByTimeRange(
            @Parameter(description = "开始时间(yyyy-MM-dd HH:mm:ss)", required = true)
            @RequestParam Timestamp startDate,
            @Parameter(description = "结束时间(yyyy-MM-dd HH:mm:ss)", required = true)
            @RequestParam Timestamp endDate) {
        return Result.success(prescriptionService.getStatisticsByTimeRange(startDate, endDate));
    }
}