package org.example.service;

import org.example.entity.LoginInfo;

public interface LoginService {
    // 登录验证
    LoginInfo login(String account, String password);

    // 修改密码
    boolean changePassword(String account, String oldPassword, String newPassword);
}
