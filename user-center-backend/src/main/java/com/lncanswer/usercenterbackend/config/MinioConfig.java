package com.lncanswer.usercenterbackend.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/14 13:19
 * minio配置类 -- 分布式文件系统
 */
@Configuration
public class MinioConfig {

    //使用Value指定配置文件，进行属性注入，需要保证属性名一致
    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.accessKey}")
    private String accessKey;

    @Value("${minio.secretKey}")
    private String secretKey;


    //将minio客户端注册为bean
    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient = MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
        return minioClient;
    }
}
