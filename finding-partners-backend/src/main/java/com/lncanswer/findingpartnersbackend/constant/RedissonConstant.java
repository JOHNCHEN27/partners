package com.lncanswer.findingpartnersbackend.constant;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/27 19:10
 */
public interface RedissonConstant {

    /**
     * 主页定时任务锁key
     */
    String MAIN_RECOMMEND_DOCACHE_KEY = "main:recommend:docache:key";

    /**
     * 加入队伍分布式锁
     */
    String JOIN_TEAM_LOCK = "join:team:lock";
}
