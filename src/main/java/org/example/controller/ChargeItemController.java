package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Result;
import org.example.entity.ChargeItemsInfo;
import org.example.service.ArrangeService;
import org.example.service.ChargeItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Tag(name = "收费项目管理", description = "收费项目的创建、查询、更新和删除等操作")
@RestController
@RequestMapping("/chargeitem")
public class ChargeItemController {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;


    @Autowired
    private ChargeItemService chargeItemService;
    public ChargeItemController(ChargeItemService chargeItemService) {
        this.chargeItemService = chargeItemService;
    }

    // ==================== 基础CRUD接口 ====================

    @Operation(summary = "创建收费项目", description = "添加新的收费项目信息")
    @PostMapping("/create")
    public void createChargeItem(
            @Parameter(description = "收费项目信息", required = true)
            @RequestBody ChargeItemsInfo chargeItemsInfo) {
        chargeItemService.createChargeItem(chargeItemsInfo);
        System.out.println("已完成创建");
    }

    @Operation(summary = "获取所有收费项目", description = "获取系统中所有收费项目列表")
    @GetMapping("/findall")
    public Result getAllChargeItems() {
        Result result = new Result("200","success",chargeItemService.getAllChargeItems());
        return result.success(chargeItemService.getAllChargeItems());
    }

    @Operation(summary = "根据ID获取收费项目", description = "根据项目ID获取详细的收费项目信息")
    @GetMapping("/queryById/{id}")
    public Result getChargeItemById(
            @Parameter(description = "收费项目ID", required = true)
            @PathVariable int id) {
        Result result = new Result("200","success",chargeItemService.getChargeItemById(id));
        return result.success(chargeItemService.getChargeItemById(id));
    }

    @Operation(summary = "更新收费项目", description = "根据项目ID更新收费项目信息")
    @PutMapping("/updateById/{id}")
    public void updateChargeItem(
            @Parameter(description = "收费项目ID", required = true)
            @PathVariable int id,
            @Parameter(description = "更新后的收费项目信息", required = true)
            @RequestBody ChargeItemsInfo chargeItemsInfo) {
        chargeItemService.updateChargeItem(id, chargeItemsInfo);
        System.out.println("已完成更新：");
    }

    @Operation(summary = "删除收费项目", description = "根据项目ID删除收费项目")
    @DeleteMapping("/deleteById/{id}")
    public void deleteChargeItem(
            @Parameter(description = "收费项目ID", required = true)
            @PathVariable int id) {
        chargeItemService.deleteChargeItem(id);
        System.out.println("已完成删除：" + id);
    }

    // ==================== 业务查询接口 ====================

    @Operation(summary = "根据类型查询收费项目", description = "获取指定类型的所有收费项目")
    @GetMapping("/queryBytype/{type}")
    public Result getChargeItemsByType(
            @Parameter(description = "项目类型(检查/检验/处方等)", required = true)
            @PathVariable ChargeItemsInfo.ChargeItemType type) {
        Result result = new Result("200","success",chargeItemService.getChargeItemsByType(type));
        return result.success(chargeItemService.getChargeItemsByType(type));
    }

    @Operation(summary = "根据执行部门查询收费项目", description = "获取指定执行部门的所有收费项目")
    @GetMapping("/queryBydep/{departmentId}")
    public Result getChargeItemsByDepartment(
            @Parameter(description = "部门ID", required = true)
            @PathVariable int departmentId) {
        Result result = new Result("200","success",chargeItemService.getChargeItemsByDepartment(departmentId));
        return result.success(chargeItemService.getChargeItemsByDepartment(departmentId));
    }

    @Operation(summary = "根据状态查询收费项目", description = "获取指定状态(启用/禁用)的所有收费项目")
    @GetMapping("/queryBystatus/{status}")
    public Result getChargeItemsByStatus(
            @Parameter(description = "项目状态(启用/禁用)", required = true)
            @PathVariable ChargeItemsInfo.ItemState status) {
        Result result = new Result("200","success",chargeItemService.getChargeItemsByStatus(status));
        return result.success(chargeItemService.getChargeItemsByStatus(status));
    }

    @Operation(summary = "搜索收费项目", description = "根据名称或拼音码模糊搜索收费项目")
    @GetMapping("/queryBysearch")
    public Result searchChargeItems(
            @Parameter(description = "搜索关键词(名称或拼音码)", required = true)
            @RequestParam String keyword) {
        Result result = new Result("200","success",chargeItemService.searchChargeItems(keyword));
        return result.success(chargeItemService.searchChargeItems(keyword));
    }

    @Operation(summary = "查询低余量收费项目", description = "获取余量低于指定阈值的收费项目")
    @GetMapping("/querylowbalance")
    public Result getChargeItemsWithLowBalance(
            @Parameter(description = "余量阈值", required = true)
            @RequestParam int threshold) {
        Result result = new Result("200","success",chargeItemService.getChargeItemsWithLowBalance(threshold));
        return result.success(chargeItemService.getChargeItemsWithLowBalance(threshold));
    }

    @Operation(summary = "根据价格范围查询收费项目", description = "获取价格在指定范围内的收费项目")
    @GetMapping("/queryBypricerange")
    public Result getChargeItemsByPriceRange(
            @Parameter(description = "最低价格", required = true)
            @RequestParam double minPrice,
            @Parameter(description = "最高价格", required = true)
            @RequestParam double maxPrice) {
        Result result = new Result("200","success",chargeItemService.getChargeItemsByPriceRange(minPrice, maxPrice));
        return result.success(chargeItemService.getChargeItemsByPriceRange(minPrice, maxPrice));
    }

    // ==================== 业务操作接口 ====================

    @Operation(summary = "更新项目状态", description = "启用或禁用收费项目")
    @PatchMapping("/updatestatus/{id}")
    public void updateChargeItemStatus(
            @Parameter(description = "收费项目ID", required = true)
            @PathVariable int id,
            @Parameter(description = "状态对象(包含status字段)", required = true)
            @RequestParam ChargeItemsInfo.ItemState status) {
        chargeItemService.updateChargeItemStatus(id, status);
        System.out.println("已完成更新：");
    }

    @Operation(summary = "更新项目余量", description = "增加或减少收费项目的余量")
    @PatchMapping("/updatebalance/{id}")
    public void updateChargeItemBalance(
            @Parameter(description = "收费项目ID", required = true)
            @PathVariable int id,
            @Parameter(description = "新的余量)", required = true)
            @RequestParam int change) {
        chargeItemService.updateChargeItemBalance(id, change);
        System.out.println("已完成更新：");
    }

    // ==================== 统计查询接口 ====================

    @Operation(summary = "获取所有项目类型", description = "获取系统中所有收费项目的类型列表")
    @GetMapping("/getalltypes")
    public Result getAllChargeItemTypes() {
        Result result = new Result("200","success",chargeItemService.getAllChargeItemTypes());
        return result.success(chargeItemService.getAllChargeItemTypes());
    }

    @Operation(summary = "根据创建者查询收费项目", description = "获取指定用户创建的所有收费项目")
    @GetMapping("/queryBycreator/{creator}")
    public Result getChargeItemsByCreator(
            @Parameter(description = "创建者用户名", required = true)
            @PathVariable String creator) {
        Result result = new Result("200","success",chargeItemService.getChargeItemsByCreator(creator));
        return result.success(chargeItemService.getChargeItemsByCreator(creator));
    }

    @Operation(summary = "根据修改者查询收费项目", description = "获取指定用户最后修改的所有收费项目")
    @GetMapping("/queryBymodifier/{modifier}")
    public Result getChargeItemsByModifier(
            @Parameter(description = "修改者用户名", required = true)
            @PathVariable String modifier) {
        Result result = new Result("200","success",chargeItemService.getChargeItemsByModifier(modifier));
        return result.success(chargeItemService.getChargeItemsByModifier(modifier));
    }

    @Operation(summary = "根据创建时间范围查询收费项目", description = "获取在指定时间范围内创建的所有收费项目")
    @GetMapping("/queryBycreatedrange")
    public Result getChargeItemsCreatedBetween(
            @Parameter(description = "开始日期(yyyy-MM-dd hh:mm:ss)", required = true)
            @RequestParam Timestamp startDate,
            @Parameter(description = "结束日期(yyyy-MM-dd hh:mm:ss)", required = true)
            @RequestParam Timestamp endDate) {
        Result result = new Result("200","success",chargeItemService.getChargeItemsCreatedBetween(startDate, endDate));
        return result.success(chargeItemService.getChargeItemsCreatedBetween(startDate, endDate));
    }

    @Operation(summary = "获取收费项目统计信息", description = "获取收费项目的各类统计信息")
    @GetMapping("/getstatistics")
    public Result getChargeItemsStatistics() {
        Result result = new Result("200","success",chargeItemService.getChargeItemsStatistics());
        return result.success(chargeItemService.getChargeItemsStatistics());
    }
}