package com.lncanswer.findingpartnersbackend.model.domain.request;


import lombok.Data;

import java.util.Date;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/28 13:22
 * 添加队伍请求体
 */
@Data
public class TeamAddRequest {

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;


}
