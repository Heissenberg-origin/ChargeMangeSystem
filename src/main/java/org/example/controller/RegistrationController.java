package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.entity.RegistrationInfo;
import org.example.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@Tag(name = "挂号管理", description = "挂号信息的创建、查询、更新和删除等操作")
@RequestMapping("/registration")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;
    public RegistrationController(RegistrationService registrationService) {this.registrationService = registrationService;}
    @PostMapping
    @Operation(summary = "创建挂号信息")
    public void createRegistration(@RequestBody RegistrationInfo registrationInfo) {
        registrationService.createRegistration(registrationInfo);
        System.out.println("创建成功");
    }

    @GetMapping("/querybyId/{regId}")
    @Operation(summary = "根据门诊号查询挂号信息")
    public Result getRegistrationById(
            @PathVariable @Parameter(description = "门诊号") int regId) {
        Result result = new Result("200","success",registrationService.getRegistrationById(regId));
        return result.success(result.getData());
    }

    @GetMapping("/findall")
    @Operation(summary = "获取所有挂号信息")
    public Result getall() {
        Result result = new Result("200","success",registrationService.getall());
        return result.success(result.getData());
    }

    @GetMapping("/querybyhcard/{regHcardId}")
    @Operation(summary = "根据就诊卡号查询挂号信息列表")
    public Result getRegistrationsByHealthCardId(
            @PathVariable @Parameter(description = "就诊卡号") int regHcardId) {
        Result result = new Result("200","success",registrationService.getRegistrationsByHealthCardId(regHcardId));
        return result.success(result.getData());
    }

    @GetMapping("/querybypname")
    @Operation(summary = "根据患者姓名查询挂号信息列表")
    public Result getRegistrationsBypname(
            @Parameter(description = "患者姓名") String regpname) {
        Result result = new Result("200","success",registrationService.getRegistrationsByPatientname(regpname));
        return result.success(result.getData());
    }

    @GetMapping("/querybypId")
    @Operation(summary = "根据患者证件号查询挂号信息列表")
    public Result getRegistrationsBypId(
            @Parameter(description = "患者证件号") String pId) {
        Result result = new Result("200","success",registrationService.getRegistrationsByPatientId(pId));
        return result.success(result.getData());
    }

    @GetMapping("/querybydocname")
    @Operation(summary = "根据医生姓名查询挂号信息列表")
    public Result getRegistrationsBydocname(
            @RequestParam @Parameter(description = "医生姓名") String docname) {
        Result result = new Result("200","success",registrationService.getRegistrationsByDoctorName(docname));
        return result.success(result.getData());
    }

    @GetMapping("/querybydepname")
    @Operation(summary = "根据部门名称查询挂号信息列表")
    public Result getRegistrationsBydepname(
            @RequestParam @Parameter(description = "部门名称") String depname) {
        Result result = new Result("200","success",registrationService.getRegistrationsByDepartmentName(depname));
        return result.success(result.getData());
    }

    @GetMapping("/querybyarrange/{regArrangeId}")
    @Operation(summary = "根据排班号查询挂号信息列表")
    public Result getRegistrationsByArrangeId(
            @RequestParam @Parameter(description = "排班号") int regArrangeId) {
        Result result = new Result("200","success",registrationService.getRegistrationsByArrangeId(regArrangeId));
        return result.success(result.getData());
    }

    @PutMapping("/update")
    @Operation(summary = "更新挂号信息")
    public void updateRegistration(
            @RequestBody RegistrationInfo registrationInfo,
            @Parameter(description = "门诊号") int regId
            ) {
        registrationService.updateRegistration(registrationInfo,regId);
    }

    @DeleteMapping("/deletebyId/{regId}")
    @Operation(summary = "删除挂号信息")
    public void deleteRegistration(
            @PathVariable @Parameter(description = "门诊号") int regId) {
        registrationService.deleteRegistration(regId);
    }

    @GetMapping("/querybystate")
    @Operation(summary = "根据状态查询挂号信息列表")
    public Result getRegistrationsByState(
            @RequestParam @Parameter(description = "挂号状态") RegistrationInfo.RegistrationState state) {
        Result result = new Result("200","success",registrationService.getRegistrationsByState(state));
        return result.success(result.getData());
    }

    @PutMapping("/updatestate")
    @Operation(summary = "更新挂号状态")
    public void updateRegistrationState(
            @Parameter(description = "门诊号") int regId,
            @Parameter(description = "新状态") RegistrationInfo.RegistrationState state) {
         registrationService.updateRegistrationState(regId, state);
    }

    @GetMapping("/querybytimerange")
    @Operation(summary = "根据时间范围查询挂号信息")
    public Result getRegistrationsByTimeRange(
            @RequestParam @Parameter(description = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @Parameter(description = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        Result result = new Result("200","success",registrationService.getRegistrationsByTimeRange(startTime, endTime));
        return result.success(result.getData());
    }

    @PutMapping("/handlepayment")
    @Operation(summary = "处理挂号支付")
    public void processRegistrationPayment(
            @RequestParam @Parameter(description = "门诊号") int regId,
            @RequestParam @Parameter(description = "收费员ID") int dealerId,
            @RequestParam @Parameter(description = "支付方式") RegistrationInfo.PaymentType paymentType) {
       registrationService.processRegistrationPayment(regId, dealerId, paymentType);
    }

//    @GetMapping("/statistics")
//    @Operation(summary = "挂号信息统计")
//    public Result getRegistrationStatistics(
//            @RequestParam(required = false) @Parameter(description = "开始日期") @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
//            @RequestParam(required = false) @Parameter(description = "结束日期") @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
//        Result result = new Result("200","success",registrationService.getRegistrationStatistics(startDate, endDate));
//        return result.success(result.getData());
//    }

    @GetMapping("/querytype")
    @Operation(summary = "根据挂号类型查询")
    public Result getRegistrationsByType(
            @RequestParam @Parameter(description = "挂号类型") RegistrationInfo.RegistrationType type) {
        Result result = new Result("200","success",registrationService.getRegistrationsByType(type));
        return result.success(result.getData());
    }

    @PutMapping("/handlecancel")
    @Operation(summary = "取消挂号")
    public void cancelRegistration(
            @RequestParam@Parameter(description = "门诊号") int regId) {
        registrationService.cancelRegistration(regId);
    }

}
