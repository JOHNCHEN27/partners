package com.lncanswer.usercenterbackend.constant;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/15 11:09
 * Minio 相关属性
 */
public interface MinioConstant {


    String endpoint = "http://47.113.185.5:9000";


    String bucket = "mediafiles";

    /**
     * 用户初始头像地址
     */
    String imageUrl = endpoint + "/" + bucket + "/" + "2024/05/14/4723dd021da83ed2ad80f4ddeabd5874.webp";
}
