package com.lncanswer.findingpartnersbackend.model.domain.request;

import lombok.Data;

import java.util.Date;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/28 17:59
 */
@Data
public class TeamUpdateRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 队伍名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 过期时间
     */
    private Date expireTime;


    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;

    /**
     * 密码
     */
    private String password;
}
