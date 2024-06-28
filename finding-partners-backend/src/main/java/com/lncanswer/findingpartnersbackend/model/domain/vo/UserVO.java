package com.lncanswer.findingpartnersbackend.model.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/28 14:15
 * 用户视图封装类
 */
@Data
public class UserVO {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户昵称
     */
    private String username;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 个人简介
     */
    private String profile;


    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态  0 - 正常
     */
    private Integer userStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     *
     */
    private Date updateTime;


    /**
     * 0 - 用户  1 - 管理员
     */
    private Integer userRole;

    /**
     * 编号
     */
    private String planetCode;

    /**
     * 标签
     */
    private String tags;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
