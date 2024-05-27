package com.lncanswer.usercenterbackend.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/29 11:05
 * 通用返回类
 */
@Data
public class BaseResponse<T> implements Serializable {

    //业务状态码
    private int code;
    //数据
    private T data;
    //返回信息
    private String message;
    //详细信息
    private String description;

    public BaseResponse(int code, T data, String message,String description){
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data,String message){
        this(code,data,message,"");
    }
    public BaseResponse(int code, T data){
        this(code,data,"","");
    }

    public BaseResponse(ErrorCode errorCode){
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }


}
