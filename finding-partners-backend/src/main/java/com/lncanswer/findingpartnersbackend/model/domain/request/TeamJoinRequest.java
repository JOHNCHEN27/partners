package com.lncanswer.findingpartnersbackend.model.domain.request;

import lombok.Data;

import java.util.Date;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/29 8:56
 * 用户加入队伍请求体
 */
@Data
public class TeamJoinRequest {
    /**
     * 队伍id
     */
    private Long teamId;

    /**
     * 密码
     */
    private String password;
}
