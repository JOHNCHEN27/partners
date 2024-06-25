package com.lncanswer.findingpartnersbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;


/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/24 20:14
 */
//@Configuration
//@EnableSwagger2WebMvc
public class Knife4Config {
//
//    @Bean(value = "adminDocket")
//    public Docket Docket() {
//        Docket docket = new Docket(DocumentationType.SWAGGER_2)
//                .apiInfo(new ApiInfoBuilder()
//                        .title("管理员接口文档")        // 设置当前文档的标题
//                        .description("用于测试学生端的所有接口的文档")        //自定义文档简介
//                        .termsOfServiceUrl("写学生端人员的服务地址URL")    //写这个模块功能的程序员相关的URL
//                        .contact("写学生端人员的联系方式(邮箱)")        //写这个模块功能的程序员的email邮箱
//                        .version("1.0")    //指定当前文档的版本
//                        .build())
//                //分组名称
//                .groupName("学生端")        //设置当前组名称
//                .select()
//                //这里指定Controller扫描包路径，"com.example.controller.student"是一个放Controller的包
//                .apis(RequestHandlerSelectors.basePackage("com.lncanswer.findingpartnersbackend.controller"))
//                .paths(PathSelectors.any())
//                .build();
//        return docket;
//    }
}
