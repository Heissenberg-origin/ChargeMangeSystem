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
    public void registerPatient(@RequestBody PatientInfo patientInfo) {
        patientService.save(patientInfo);
    }
    @DeleteMapping("/delete/{healthcardId}")
    @Operation(summary = "注销病人信息", description = "注销患者信息")
    public void deletePatient(@PathVariable int healthcardId) {
        patientService.removeById(healthcardId);
    }

    @GetMapping("/getgendernum")
    @Operation(summary = "查询性别人数", description = "根据性别查看人数")
    public Result getgenderamount(
            @RequestParam@Parameter PatientInfo.Gender gender){
        Result result = new Result("200","success",patientService.getgendernum(gender));
        return result;
    }

    @GetMapping("/query")
    @Operation(summary = "查询病人信息", description = "查询病人")
    public Result queryPatients(
            @RequestParam(required = false) int healthcardId,
            @RequestParam(required = false) String identificationId,
            @RequestParam(required = false) String name) {

        if (healthcardId == 0 && identificationId == null && name == null) {
            throw new IllegalArgumentException("至少需要提供一个查询参数（就诊卡号、证件号或姓名）");
        }

        Result result =new Result("200","success",patientService.queryPatients(healthcardId, identificationId, name));
        return result.success(result.getData());
    }
    @PutMapping("/updateByHealthcard/{healthcardId}")
    @Operation(summary = "更新病人信息", description = "更新信息")
    public void updateByHealthcardId(
            @PathVariable int healthcardId,
            @RequestBody PatientInfo patientInfo) {
        patientService.updateByHealthcardId(healthcardId, patientInfo);
    }
    @PutMapping("/listall")
    @Operation(summary = "展示所有病人信息", description = "展示信息")
    public Result listallPatients() {
        Result result =new Result("200","success",patientService.list());
        return result.success(result.getData());
    }
    @PostMapping("/recharge")
    @Operation(summary = "就诊卡充值", description = "为指定就诊卡充值金额")
    public void recharge(
            @Parameter(description = "就诊卡号", required = true, example = "C123456789")
            @RequestParam int healthcardId,

            @Parameter(description = "充值金额(元)", required = true, example = "100.50")
            @RequestParam float amount) {
            patientService.recharge(healthcardId, amount);
    }
}
