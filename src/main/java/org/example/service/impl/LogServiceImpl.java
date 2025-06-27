package org.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.entity.LogInfo;
import org.example.mapper.LogInfoMapper;
import org.example.service.LogService;

public class LogServiceImpl extends ServiceImpl<LogInfoMapper, LogInfo> implements LogService {
    private LogInfoMapper logInfoMapper;
}
