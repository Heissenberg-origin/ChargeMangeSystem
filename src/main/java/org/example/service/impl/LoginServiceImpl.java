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

    @Override
    public LoginInfo login(String account, String password) {
        return loginInfoMapper.checkLogin(account, password);
    }

    @Override
    @Transactional
    public boolean changePassword(String account, String oldPassword, String newPassword) {
        // 验证旧密码是否正确
        LoginInfo user = loginInfoMapper.checkLogin(account, oldPassword);
        if (user == null) {
            return false;
        }

        // 更新密码
        return loginInfoMapper.updatePassword(account, oldPassword, newPassword) > 0;
    }
}
