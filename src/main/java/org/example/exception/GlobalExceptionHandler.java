package org.example.exception;

import org.example.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/*
全局异常捕获器
 */
@ControllerAdvice("com.sxt.controller")
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = Exception.class)
    @ResponseBody //将result对象转化成json的格式
    public Result error(Exception e) {
        log.error(e.getMessage());
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(value = CustomException.class)
    @ResponseBody //将result对象转化成json的格式
    public Result customerError(CustomException e) {
        log.error("自定义错误",e);
        return Result.error(e.getCode(),e.getMessage());
    }
}