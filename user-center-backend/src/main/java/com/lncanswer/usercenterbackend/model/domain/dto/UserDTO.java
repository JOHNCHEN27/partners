package com.lncanswer.usercenterbackend.model.domain.dto;

import lombok.Data;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/5 20:32
 * 用户DTO，用来在本地线程中存储用户信息
 */
@Data
public class UserDTO {
    /**
     * 用户id
     */
    private Long id;
    /**
     * 用户昵称
     */
    private String username;
    /**
     * 用户头像
     */
    private String avatarUrl;
    /**
     * 状态  0 - 正常
     */
    private Integer userStatus;
    /**
     * 0 - 用户  1 - 管理员
     */
    private Integer userRole;
}
