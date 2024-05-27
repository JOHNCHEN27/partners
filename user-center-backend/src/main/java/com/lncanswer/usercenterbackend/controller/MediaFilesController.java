package com.lncanswer.usercenterbackend.controller;

import com.lncanswer.usercenterbackend.model.domain.dto.UploadFileParamsDto;
import com.lncanswer.usercenterbackend.model.domain.dto.UploadFileResultDto;
import com.lncanswer.usercenterbackend.service.MediaFileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/14 14:32
 * 媒资文件管理 -- 存储头像、视频等信息到分布式文件系统（Minio）
 */
@RestController
@RequestMapping("/files")
@Slf4j
public class MediaFilesController {


    @Resource
    MediaFileService mediaFileService;

    /**
     * 文件上传 （图片、视频等）
     * @param file 文件  MultipartFile类型需要上传
     * @param objectName 对象名称
     * @return  上传结果对象
     * @throws IOException io异常
     */
    @PostMapping("/upload")
    public UploadFileResultDto uploadFiles(@RequestPart("fileData") MultipartFile file, @RequestParam(value = "objectName",required = false) String objectName) throws IOException{
        UploadFileParamsDto uploadFileParamsDto = new UploadFileParamsDto();
        //封装参数信息
        uploadFileParamsDto.setFileSize(file.getSize());
        uploadFileParamsDto.setFileType("3"); //文件类型图片
        //文件名称 完整的文件名称 如 imges1.png
        uploadFileParamsDto.setFilename(file.getOriginalFilename());
        //创建临时文件
        File tempFile = File.createTempFile("minio","temp");
        //上传的文件拷贝到临时文件
        file.transferTo(tempFile);
        //文件路径 获取临时文件的目录
        String absolutePath = tempFile.getAbsolutePath();
        log.info("absolutePath = {}",absolutePath);
        //上传文件
        return mediaFileService.uploadFile( uploadFileParamsDto, absolutePath,objectName);
    }

}
