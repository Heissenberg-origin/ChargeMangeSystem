package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.common.Constants;
import org.example.entity.LoginInfo;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@RestController
@Tag(name = "登录管理", description = "登陆账号的创建、查询、更新和删除等操作")
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public ResponseEntity<String> showLoginPage() {
        return ResponseEntity.ok("请使用POST方法提交登录表单");
        // 实际项目中可以返回登录页面HTML或重定向到前端登录页
    }

    // 登录接口
    @PostMapping("/login")
    public ResponseEntity<?> doLogin(
            @RequestParam int id,
            @RequestParam String password,
            @RequestParam(required = false) String redirect,
            HttpSession session) {

        LoginInfo user = loginService.login(id, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("用户名或密码错误");
        }

        session.setAttribute(Constants.USER_SESSION_KEY, user);

        if (redirect != null && !redirect.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", redirect)
                    .build();
        }

        return ResponseEntity.ok(user);
    }

    // 登出接口
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpSession session) {
        // 使session失效
        session.invalidate();
        return ResponseEntity.ok("登出成功");
    }

    // 修改密码接口
    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestParam int id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            HttpSession session) {

        // 检查用户是否登录
        LoginInfo currentUser = (LoginInfo) session.getAttribute(Constants.USER_SESSION_KEY);
        if (currentUser == null || currentUser.getId() != id) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("请先登录");
        }

        if (newPassword == null || newPassword.length() < 4) {
            return ResponseEntity.badRequest()
                    .body("新密码长度不能少于4位");
        }

        boolean success = loginService.changePassword(id, oldPassword, newPassword);

        if (success) {
            return ResponseEntity.ok("密码修改成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("密码修改失败，请检查当前密码是否正确");
        }
    }
}