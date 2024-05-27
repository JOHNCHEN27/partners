package com.lncanswer.usercenterbackend.exception;

import com.lncanswer.usercenterbackend.common.BaseResponse;
import com.lncanswer.usercenterbackend.common.ErrorCode;
import com.lncanswer.usercenterbackend.common.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/29 18:03
 * 全局异常处理
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 业务异常处理 -- 监听自定义异常，放异常发生时返回响应对象
     * @param e 异常对象
     * @return 返回统一响应对象
     */
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException: " +  e.getMessage(),e);
        return ResultUtils.error(e.getCode(),e.getMessage(),e.getDescription());
    }

    /**
     * 系统运行异常处理 -- 监听运行异常，当发生的时候，发出警告
     * @param e 异常对象
     * @return 返回响应对象
     */
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.info("runtimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
