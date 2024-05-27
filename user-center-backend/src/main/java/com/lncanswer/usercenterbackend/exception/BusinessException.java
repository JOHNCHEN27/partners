package com.lncanswer.usercenterbackend.exception;

import com.lncanswer.usercenterbackend.common.ErrorCode;
import lombok.Getter;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/29 11:43
 * 业务异常处理
 */
@Getter
public class BusinessException extends RuntimeException{

    //扩展异常信息属性
    private final int code;
    private final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
    }

    public BusinessException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code =errorCode.getCode();
        this.description = description;
    }
}
