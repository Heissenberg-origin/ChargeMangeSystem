package org.example.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.entity.LoginInfo;
import org.example.service.LoginService;
import org.example.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Tag(name = "登录管理", description = "登陆账号的创建、查询、更新和删除等操作")
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(
            @RequestParam String account,
            @RequestParam String password) {

        LoginInfo user = loginService.login(account, password);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("用户名或密码错误");
        }

        // 生成JWT token
        String token = jwtTokenUtil.generateToken(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/password")
    public ResponseEntity<String> changePassword(
            @RequestParam String account,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestHeader("Authorization") String authHeader) {

        // 验证token
        String token = authHeader.substring(7); // 去掉"Bearer "前缀
        LoginInfo currentUser = loginService.login(account, oldPassword);

        if (currentUser == null || !jwtTokenUtil.validateToken(token, currentUser)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("认证失败，请重新登录");
        }

        if (newPassword == null || newPassword.length() < 4) {
            return ResponseEntity.badRequest()
                    .body("新密码长度不能少于4位");
        }

        boolean success = loginService.changePassword(account, oldPassword, newPassword);

        if (success) {
            return ResponseEntity.ok("密码修改成功");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("密码修改失败，请检查当前密码是否正确");
        }
    }
}