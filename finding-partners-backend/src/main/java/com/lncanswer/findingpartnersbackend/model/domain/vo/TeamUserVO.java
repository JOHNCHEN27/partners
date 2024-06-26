package com.lncanswer.findingpartnersbackend.model.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.lncanswer.findingpartnersbackend.model.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/28 14:13
 * VO -- 视图 返回给前端的对象
 * 队伍和用户信息封装类
 */
@Data
public class TeamUserVO implements Serializable {

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建人用户信息
     */
   UserVO userList;

    /**
     * 是否已加入队伍
     */
    private boolean hasJoin = false;

    /**
     * 已加入的用户数
     */
    private Integer hasJoinNum;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
