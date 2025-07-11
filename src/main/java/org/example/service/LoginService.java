package org.example.service;

import org.example.entity.LoginInfo;

public interface LoginService {

    /**
     * 登录验证
     *
     * @param account  用户账号
     * @param password 用户密码
     * @return 登录信息对象，如果验证成功则返回用户信息，否则返回 null
     */
    LoginInfo login(String account, String password);

    /**
     * 修改密码
     *
     * @param account     用户账号
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 是否修改成功
     */
    boolean changePassword(String account, String oldPassword, String newPassword);
}