package com.lncanswer.usercenterbackend.model.domain.dto;
import com.lncanswer.usercenterbackend.model.domain.MediaFiles;
import lombok.Data;
/**
 * @author LNC
 * @version 1.0
 * @description  上传普通文件相应结果
 * @date 2023/10/1 11:45
 */
@Data
public class UploadFileResultDto extends MediaFiles {

    /**
     * 头像地址 -- minio地址 + bucket名称 + 上传的图片名称组成一个完整的 Url
     */
    private String imageUrl;
}
