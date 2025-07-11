package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.entity.RegistrationInfo;
import org.example.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.Map;

@RestController
@Tag(name = "挂号管理", description = "挂号信息的创建、查询、更新和删除等操作")
@RequestMapping("/registration")
public class RegistrationController {
    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public RegistrationController(RegistrationService registrationService) {this.registrationService = registrationService;}
    @PostMapping
    @Operation(summary = "创建挂号信息")
    public void createRegistration(@RequestBody RegistrationInfo registrationInfo) {
        registrationService.createRegistration(registrationInfo);
        System.out.println("创建成功");
    }

    @GetMapping("/getGenderStatsByDate")
    @Operation(summary = "查询特定日期的性别统计",
            description = "获取指定日期的男性和女性挂号人数统计")
    public Result getGenderStatsByDate(
            @RequestParam @Parameter(description = "日期，格式为yyyy-MM-dd") String date) {
        try {
            // 验证日期格式
            LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            return new Result("400", "日期格式不正确，请使用yyyy-MM-dd格式", null);
        }

        Map<String, Integer> stats = registrationService.getGenderStatsByDate(date);
        return new Result("200", "success", stats);
    }

    @GetMapping("/querybyId/{regId}")
    @Operation(summary = "根据门诊号查询挂号信息")
    public Result getRegistrationById(
            @PathVariable @Parameter(description = "门诊号") int regId) {
        return Result.success(registrationService.getRegistrationById(regId));
    }

    @GetMapping("/querybyneed/{docId}/{date}/{state}")
    @Operation(summary = "根据医生id、日期、状态查询挂号信息")
    public Result getRegistrationByNeed(
            @PathVariable @Parameter(description = "医生id") int docId,
            @PathVariable @Parameter(description = "日期(yyyy-MM-dd)") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @PathVariable @Parameter(description = "状态") RegistrationInfo.RegistrationState state) {

        // Convert LocalDate to java.sql.Date for MyBatis
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);

        return Result.success(registrationService.getRegistrationByneed(docId, sqlDate, state));
    }

    @GetMapping("/findall")
    @Operation(summary = "获取所有挂号信息")
    public Result getall() {
        return Result.success(registrationService.getall());
    }

    @GetMapping("/querybyhcard/{regHcardId}")
    @Operation(summary = "根据就诊卡号查询挂号信息列表")
    public Result getRegistrationsByHealthCardId(
            @PathVariable @Parameter(description = "就诊卡号") int regHcardId) {
        return Result.success(registrationService.getRegistrationsByHealthCardId(regHcardId));
    }

    @GetMapping("/querybypname")
    @Operation(summary = "根据患者姓名查询挂号信息列表")
    public Result getRegistrationsBypname(
            @Parameter(description = "患者姓名") String regpname) {
        return Result.success(registrationService.getRegistrationsByPatientname(regpname));
    }

    @GetMapping("/querybypId")
    @Operation(summary = "根据患者证件号查询挂号信息列表")
    public Result getRegistrationsBypId(
            @Parameter(description = "患者证件号") String pId) {
        return Result.success(registrationService.getRegistrationsByPatientId(pId));
    }

    @GetMapping("/querybydocname")
    @Operation(summary = "根据医生姓名查询挂号信息列表")
    public Result getRegistrationsBydocname(
            @RequestParam @Parameter(description = "医生姓名") String docname) {
        return Result.success(registrationService.getRegistrationsByDoctorName(docname));
    }

    @GetMapping("/querybydepname")
    @Operation(summary = "根据部门名称查询挂号信息列表")
    public Result getRegistrationsBydepname(
            @RequestParam @Parameter(description = "部门名称") String depname) {
        return Result.success(registrationService.getRegistrationsByDepartmentName(depname));
    }

    @GetMapping("/querybyarrange/{regArrangeId}")
    @Operation(summary = "根据排班号查询挂号信息列表")
    public Result getRegistrationsByArrangeId(
            @RequestParam @Parameter(description = "排班号") int regArrangeId) {
        return Result.success(registrationService.getRegistrationsByArrangeId(regArrangeId));
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
        return Result.success(registrationService.getRegistrationsByState(state));
    }

    @PutMapping("/updatestate/{regId}/{state}")
    @Operation(summary = "更新挂号状态")
    public void updateRegistrationState(
            @PathVariable @Parameter(description = "门诊号") int regId,
            @PathVariable @Parameter(description = "新状态") RegistrationInfo.RegistrationState state) {
        registrationService.updateRegistrationState(regId, state);
    }

    @GetMapping("/querybytimerange")
    @Operation(summary = "根据时间范围查询挂号信息")
    public Result getRegistrationsByTimeRange(
            @RequestParam @Parameter(description = "开始时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam @Parameter(description = "结束时间") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime) {
        return Result.success(registrationService.getRegistrationsByTimeRange(startTime, endTime));
    }

    @PutMapping("/handlepayment/{regId}/{dealerId}/{paymentType}")
    @Operation(summary = "处理挂号支付")
    public void processRegistrationPayment(
            @PathVariable @Parameter(description = "门诊号") int regId,
            @PathVariable @Parameter(description = "收费员ID") int dealerId,
            @PathVariable @Parameter(description = "支付方式") RegistrationInfo.PaymentType paymentType) {
        registrationService.processRegistrationPayment(regId, dealerId, paymentType);
    }

    @GetMapping("/querytype")
    @Operation(summary = "根据挂号类型查询")
    public Result getRegistrationsByType(
            @RequestParam @Parameter(description = "挂号类型") RegistrationInfo.RegistrationType type) {
        return Result.success(registrationService.getRegistrationsByType(type));
    }

    @PutMapping("/handlecancel/{regId}")
    @Operation(summary = "取消挂号")
    public void cancelRegistration(
            @Parameter(description = "门诊号")
            @PathVariable int regId) {
        registrationService.cancelRegistration(regId);
    }

}
