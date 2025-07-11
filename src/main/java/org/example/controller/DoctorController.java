package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.entity.DoctorInfo;
import org.example.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/doctor")
@Tag(name = "医生管理", description = "医生信息查询接口")
public class DoctorController {
    @Autowired
    private final DoctorService doctorService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    @GetMapping("/searchById")
    @Operation(summary = "根据ID查询医生")
    public Result getDoctorById(
            @Parameter(description = "医生ID", required = true, example = "1001")
            @RequestParam int docId) {
        Result result= new Result("200","success",doctorService.getDoctorById(docId));
        return result.success(doctorService.getDoctorById(docId));
    }

    @GetMapping("/getBydepid/{depId}")
    @Operation(summary = "查询部门所有医生")
    public Result getDoctorBydep(
            @PathVariable @Parameter(description = "部门ID", required = true, example = "1001")
            int depId) {
        Result result = new Result("200", "success", doctorService.getDoctorBydepId(depId));
        return result.success(doctorService.getDoctorBydepId(depId));
    }

    @GetMapping("/findall")
    @Operation(summary = "查询所有医生")
    public Result getallDoctor() {
        Result result= new Result("200","success",doctorService.getalldoc());
        return result.success(doctorService.getalldoc());
    }


    @GetMapping("/search")
    @Operation(summary = "根据姓名模糊查询医生")
    public Result searchDoctorsByName(
            @Parameter(description = "医生姓名(支持模糊查询)", example = "张")
            @RequestParam String name) {
        Result result= new Result("200","success",doctorService.searchDoctorsByName(name));
        return result.success(doctorService.searchDoctorsByName(name));
    }

    @PostMapping("/add")
    @Operation(summary = "添加新医生")
    public void addDoctor(
            @Parameter(description = "医生信息", required = true)
            @RequestBody DoctorInfo doctorInfo) {
        doctorService.addDoctor(doctorInfo);
        System.out.println("已完成添加");
    }

    @PutMapping("/update/{docId}")
    @Operation(summary = "更新医生信息")
    public void updateDoctor(
            @Parameter(description = "医生ID", required = true, example = "1001")
            @PathVariable int docId,
            @Parameter(description = "更新后的医生信息", required = true)
            @RequestBody DoctorInfo doctorInfo) {
        doctorService.updateDoctor(docId, doctorInfo);
        System.out.println("已完成对"+docId+"的更新");
    }

    @DeleteMapping("/delete/{docId}")
    @Operation(summary = "删除医生")
    public void deleteDoctor(
            @Parameter(description = "医生ID", required = true, example = "1001")
            @PathVariable int docId) {
        doctorService.deleteDoctor(docId);
        System.out.println("已完成对"+docId+"的删除");
    }
}