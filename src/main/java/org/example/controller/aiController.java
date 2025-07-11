package org.example.controller;

import io.github.briqt.spark4j.SparkClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.example.common.Result;
import org.example.service.aiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@Tag(name = "ai对话", description = "进行简单的ai对话用于帮助挂号与收费")
@RestController
@RequestMapping("/aichat")
public class aiController {
    @Autowired
    private aiService aiService;
    @PostMapping("/aiProject")
    @Operation(summary = "对话",description = "与ai对话咨询挂号科室")
    public Result aichat(@Parameter(description = "输入内容",required = true) @RequestBody String inputContent){
        Result result = new Result("200","success",aiService.sendHttpTOSpark(inputContent));
        return result.success(aiService.sendHttpTOSpark(inputContent));
    }


    }
