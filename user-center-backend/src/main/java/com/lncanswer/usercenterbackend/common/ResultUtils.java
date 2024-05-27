package com.lncanswer.usercenterbackend.common;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/29 11:15
 * 返回工具类 构造统一返回信息
 */
public class ResultUtils {

    /**
     * 成功
     * @param data 返回数据
     * @return 返回统一相应对象
     * @param <T> 参数类型
     */
    public static <T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0,data,"ok");
    }

    /**
     * 成功
     * @return 返回统一响应对象
     * @param <T>
     */
    public static <T> BaseResponse<T> success(){
        return new BaseResponse<>(0,null,"ok");
    }

    /**
     * 失败
     * @param errorCode 错误码对象
     * @return 返回统一相应对象
     * @param <T> 参数类型
     */
    public static <T>BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);
    }

    /**
     * 失败
     * @param Code
     * @param message
     * @param description
     * @return  返回统一响应对象
     * @param <T>
     */
    public static <T>BaseResponse<T> error(int Code,String message,String description){
        return new BaseResponse(Code,null,message,description);
    }

    /**
     * 失败
     * @param errorCode
     * @param message
     * @param description
     * @return  返回统一响应对象
     * @param <T>
     */
    public static <T>BaseResponse<T> error(ErrorCode errorCode,String message,String description){
        return new BaseResponse(errorCode.getCode(),null,message,description);
    }

    /**
     * 失败
     * @param errorCode
     * @param description
     * @return 返回统一响应对象
     * @param <T>
     */
    public static <T>BaseResponse<T> error(ErrorCode errorCode,String description){
        return new BaseResponse(errorCode.getCode(),null,errorCode.getMessage(),description);
    }
}
