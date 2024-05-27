package com.lncanswer.usercenterbackend.model.domain.dto;

import lombok.Data;

/**
 * @author LNC
 * @version 1.0
 * @description 上传普通文件请求参数
 * @date 2023/10/1 12:29
 */
@Data
public class UploadFileParamsDto {

    /**
     * 文件名称
     */
    private String filename;


    /**
     * 文件类型（文档 0，音频 1，视频 2,图片 3）
     */
    private String fileType;
    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 标签
     */
    private String tags;

    /**
     * 上传人
     */
    private String username;

    /**
     * 备注
     */
    private String remark;



}
