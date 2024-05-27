package com.lncanswer.usercenterbackend.constant;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/5 21:28
 * Redis相关常量
 */
public interface RedisConstant {

    /**
     * 用户登录key前缀
     */
    String LOGIN_USER_KEY ="userLoginToken:";

    Long LOGIN_USER_TTL = 36000L;
}
