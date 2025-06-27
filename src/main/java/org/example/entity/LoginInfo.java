package org.example.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@TableName("login_info")
@Schema(description = "登录信息实体")
public class LoginInfo {
    // ==================== 枚举定义 ====================
    @Schema(description = "用户权限等级")
    public enum UserRank {
        @Schema(description = "管理员") administrator,
        @Schema(description = "操作员") operator
    }

    // ==================== 字段声明 ====================
    @Schema(description = "用户ID", example = "1001")
    private int id;

    @Schema(description = "登录密码", example = "encryptedPassword123")
    private String password;

    @Schema(description = "权限等级",
            allowableValues = {"administrator", "operator"})
    private UserRank rank;  // 改为枚举类型

    // ==================== Getter/Setter ====================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UserRank getRank() {
        return rank;
    }

    public void setRank(UserRank rank) {
        this.rank = rank;
    }

    // 支持String类型输入的setter
    public void setRank(String rank) {
        try {
            this.rank = UserRank.valueOf(rank);
        } catch (IllegalArgumentException e) {
            this.rank = null;
        }
    }
}
