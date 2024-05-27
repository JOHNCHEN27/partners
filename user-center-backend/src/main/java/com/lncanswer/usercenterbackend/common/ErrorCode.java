package com.lncanswer.usercenterbackend.common;

import lombok.Getter;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/29 11:25
 * 错误码
 */
@Getter
public enum ErrorCode {

    SUCCESS(0,"ok",""),
    PARAMS_ERROR(40000,"请求参数错误",""),
    NULL_ERROR(40001,"请求数据为空",""),
    OBJECT_NOT_NULL_ERROR(40002,"对象不为空",""),
    NOT_LOGIN(40100,"未登录",""),
    NO_AUTH(40101,"无权限", ""),
    SYSTEM_ERROR(50000,"系统内部异常",""),
    SELECT_ERROR(40002,"查询错误",""),
    UPDATE_ERROR(40003,"更新错误","");

    /**
     * 状态码信息
     */
    private final int code;
    /**
     * 状态码信息简要描述
     */
    private final String message;
    /**
     * 状态码信息详细描述
     */
    private final String description;

    ErrorCode(int code, String message, String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }


}
