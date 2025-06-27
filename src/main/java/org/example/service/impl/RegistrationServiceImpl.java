package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.RegistrationInfo;
import org.example.mapper.RegistrationInfoMapper;
import org.example.service.RegistrationService;

public class RegistrationServiceImpl extends ServiceImpl <RegistrationInfoMapper, RegistrationInfo> implements RegistrationService {
    private RegistrationInfoMapper registrationInfoMapper;
}
