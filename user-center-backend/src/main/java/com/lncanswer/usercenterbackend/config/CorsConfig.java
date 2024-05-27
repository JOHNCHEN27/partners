package com.lncanswer.usercenterbackend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/28 18:17
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //允许跨域的路径
       registry.addMapping("/**")
               //允许发送cookie
               .allowCredentials(true)
               //设置允许跨域请求的域名
               //当** Credentials为true时，Origin不能为星号，需要为具体的ip地址
               .allowedOrigins("http://47.113.185.5:8080","http://localhost:8000")
               //允许跨域的方法
               .allowedMethods("*")
               //跨域允许时间
               .maxAge(3600);
    }
}
