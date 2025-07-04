package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.entity.PatientInfo;
import org.example.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@Tag(name = "病人信息管理", description = "收费项目患者信息的创建、查询、更新和删除等操作")
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    /**
     * 接收前端提交的病人信息并保存到数据库
     * @param patientInfo 前端传递的JSON数据（自动转换为Patient_info对象）
     * @return 保存成功返回true，失败返回false
     */
    @PostMapping("/register")
    @Operation(summary = "创建病人信息", description = "注册新的病人")
    public Result registerPatient(@RequestBody PatientInfo patientInfo) {
        Result result=new Result("200","success",patientService.mysave(patientInfo));
        return result;
    }


    @DeleteMapping("/delete/{healthcardId}")
    @Operation(summary = "注销病人信息", description = "注销患者信息")
    public void deletePatient(@PathVariable int healthcardId) {
        patientService.removeById(healthcardId);
    }



    @GetMapping("/querybyId/{healthcardId}")
    @Operation(summary = "就诊卡查询病人信息", description = "查询病人")
    public Result queryPatients(
            @PathVariable int healthcardId) {  //  正确，使用 @PathVariable
        Result result = new Result("200", "success", patientService.getById(healthcardId));
        return result.success(result.getData());
    }
    @PutMapping("/updateByHealthcard/{healthcardId}")
    @Operation(summary = "更新病人信息", description = "更新信息")
    public void updateByHealthcardId(
            @PathVariable int healthcardId,
            @RequestBody PatientInfo patientInfo) {
        patientService.updateByHealthcardId(healthcardId, patientInfo);
    }
    @GetMapping("/listall")
    @Operation(summary = "展示所有病人信息", description = "展示信息")
    public Result listallPatients() {
        Result result =new Result("200","success",patientService.list());
        return result.success(result.getData());
    }
    @PostMapping("/recharge/{healthcardId}/{amount}")
    @Operation(summary = "就诊卡充值", description = "为指定就诊卡充值金额")
    public void recharge(

            @Parameter(description = "就诊卡号", required = true, example = "C123456789")
            @PathVariable int healthcardId,

            @Parameter(description = "充值金额(元)", required = true, example = "100.50")
            @PathVariable float amount) {
        patientService.recharge(healthcardId, amount);

    }

    @PutMapping("/settlement/{healthcardId}")
    @Operation(summary = "结算", description = "就诊卡余额结算")
    public void settlebalance(
            @Parameter(description = "就诊卡号",required = true,example = "111")
            @PathVariable int healthcardId) {
        patientService.settlehcard(healthcardId);
    }
}
