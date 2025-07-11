package org.example.service.impl;

import org.example.entity.LoginInfo;
import org.example.mapper.LoginInfoMapper;
import org.example.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginInfoMapper loginInfoMapper;



    /**
     * 用户登录验证
     *
     * @param account 账户名
     * @param password 密码
     * @return 登录信息对象，如果验证失败则返回 null
     */
    @Override
    public LoginInfo login(String account, String password) {
        return loginInfoMapper.checkLogin(account, password);
    }

    /**
     * 更改用户密码
     *
     * @param account     账户名
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 如果旧密码正确且密码更新成功则返回 true，否则返回 false
     */
    @Override
    @Transactional
    public boolean changePassword(String account, String oldPassword, String newPassword) {
        // 验证旧密码是否正确
        LoginInfo user = loginInfoMapper.checkLogin(account, oldPassword);
        if (user == null) {
            return false; // 旧密码不正确
        }

        // 更新密码
        return loginInfoMapper.updatePassword(account, oldPassword, newPassword) > 0;
    }
}