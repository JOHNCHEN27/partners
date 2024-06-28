package com.lncanswer.findingpartnersbackend.model.domain.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.lncanswer.findingpartnersbackend.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.compress.archivers.zip.X0017_StrongEncryptionHeader;

import java.util.Date;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/28 10:59
 * 队伍查询封装类
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeamQuery extends PageRequest {

    /**
     * id
     */
    private Long id;

    /**
     * 搜索关键词（同时对队伍名称和描述进行搜索）
     */
    private String searchText;

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
     * 用户id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;


}
