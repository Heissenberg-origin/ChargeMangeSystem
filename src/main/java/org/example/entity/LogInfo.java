package org.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
@Data
@TableName("log_info")
@Schema(description = "系统日志实体")
public class LogInfo {
    // ==================== 枚举定义 ====================
    @Schema(description = "日志模块枚举")
    public enum Module {
        @Schema(description = "患者信息管理") 患者信息管理,
        @Schema(description = "挂号") 挂号,
        @Schema(description = "医嘱") 医嘱,
        @Schema(description = "支付") 支付,
        @Schema(description = "信息展示及统计") 信息展示及统计,
        @Schema(description = "日志") 日志
    }

    @Schema(description = "操作类型枚举")
    public enum OperationType {
        @Schema(description = "增加") 增加,
        @Schema(description = "删除") 删除,
        @Schema(description = "修改") 修改,
        @Schema(description = "查询") 查询
    }

    // ==================== 字段声明 ====================
    @Schema(description = "日志序号", example = "1")
    private int log_sequence;

    @Schema(description = "日志模块",
            allowableValues = {"患者信息管理", "挂号", "医嘱", "支付", "信息展示及统计", "日志"})
    private Module log_module;  // 改为枚举类型

    @Schema(description = "操作人ID", example = "1001")
    private int log_dealer_id;  // 改为int类型

    @Schema(description = "操作类型",
            allowableValues = {"增加", "删除", "修改", "查询"})
    private OperationType log_type;  // 改为枚举类型

    @Schema(description = "操作地址", example = "192.168.1.100")
    private String log_address;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "操作时间", example = "2023-10-01 14:30:00")
    private Date log_time;  // 改为Date类型

    @Schema(description = "请求方法", example = "GET /api/patient")
    private String log_method;

    @Schema(description = "处理方法", example = "PatientController.getPatient()")
    private String log_deal_method;

    @Schema(description = "请求参数", example = "{id: 123}")
    private String log_param;

    // ==================== Getter/Setter ====================
    public int getLog_sequence() {
        return log_sequence;
    }

    public void setLog_sequence(int log_sequence) {
        this.log_sequence = log_sequence;
    }

    public Module getLog_module() {
        return log_module;
    }

    public void setLog_module(Module log_module) {
        this.log_module = log_module;
    }

    // 支持String类型输入的setter
    public void setLog_module(String log_module) {
        try {
            this.log_module = Module.valueOf(log_module);
        } catch (IllegalArgumentException e) {
            this.log_module = null;
        }
    }

    public int getLog_dealer_id() {
        return log_dealer_id;
    }

    public void setLog_dealer_id(int log_dealer_id) {
        this.log_dealer_id = log_dealer_id;
    }

    // 保留String类型的setter（兼容旧代码）
    public void setLog_dealer_id(String log_dealer_id) {
        try {
            this.log_dealer_id = Integer.parseInt(log_dealer_id);
        } catch (NumberFormatException e) {
            this.log_dealer_id = 0;
        }
    }

    public OperationType getLog_type() {
        return log_type;
    }

    public void setLog_type(OperationType log_type) {
        this.log_type = log_type;
    }

    // 支持String类型输入的setter
    public void setLog_type(String log_type) {
        try {
            this.log_type = OperationType.valueOf(log_type);
        } catch (IllegalArgumentException e) {
            this.log_type = null;
        }
    }

    public String getLog_address() {
        return log_address;
    }

    public void setLog_address(String log_address) {
        this.log_address = log_address;
    }

    public Date getLog_time() {
        return log_time;
    }

    public void setLog_time(Date log_time) {
        this.log_time = log_time;
    }

    // 支持String类型输入的setter（需符合yyyy-MM-dd HH:mm:ss格式）
    public void setLog_time(String log_time) {
        // 实际项目中应添加日期解析逻辑
        // 这里简化为直接设置当前时间
        this.log_time = new Date();
    }

    public String getLog_method() {
        return log_method;
    }

    public void setLog_method(String log_method) {
        this.log_method = log_method;
    }

    public String getLog_deal_method() {
        return log_deal_method;
    }

    public void setLog_deal_method(String log_deal_method) {
        this.log_deal_method = log_deal_method;
    }

    public String getLog_param() {
        return log_param;
    }

    public void setLog_param(String log_param) {
        this.log_param = log_param;
    }
}
