package com.lncanswer.usercenterbackend.service;

import com.lncanswer.usercenterbackend.model.domain.MediaFiles;
import com.lncanswer.usercenterbackend.model.domain.dto.UploadFileParamsDto;
import com.lncanswer.usercenterbackend.model.domain.dto.UploadFileResultDto;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/14 13:34
 *
 * minio文件系统实现接口 --媒资文件管理
 */
public interface MediaFileService {

    /**
     * 上传文件
     * @param uploadFileParamsDto 请求参数封装对象
     * @param localFilePath 文件磁盘路径
     * @param objectName 存储桶名称
     * @return 上传结果对象
     */
     UploadFileResultDto uploadFile( UploadFileParamsDto uploadFileParamsDto, String localFilePath, String objectName);

    /**
     * 添加文件到数据库
     * @param fileMd5 文件md5值， 主键
     * @param uploadFileParamsDto 请求参数对象
     * @param bucketFiles 文件桶
     * @param objectName 文件名称
     * @return 文件
     */
     MediaFiles addMediaFilesToDb( String fileMd5, UploadFileParamsDto uploadFileParamsDto, String bucketFiles, String objectName);

}
