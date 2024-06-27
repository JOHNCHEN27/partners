package com.lncanswer.findingpartnersbackend.constant;

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

    /**
     * 用户推荐缓存key前缀
     */
    String PARTNERS_RECOMMEND_KEY = "partners:user:recommend:";

    Long PARTNERS_RECOMMEND_KEY_TTL = 150000L;

    /**
     * 主页默认推荐内容key
     */
    String PARTNERS_RECOMMEND_DEFAULT_KEY = "partners:recommend:default:key";
}
