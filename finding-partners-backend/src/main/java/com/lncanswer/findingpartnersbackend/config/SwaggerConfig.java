package com.lncanswer.findingpartnersbackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import springfox.documentation.RequestHandler;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;


import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;


/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/24 19:40
 * 自定义Swagger 接口文档的配置
 */
@Configuration
@EnableSwagger2
@Profile("dev")
public class SwaggerConfig {

    /**
     * 需要将拦截器配置关闭，否证会被拦截无法访问 UI界面
     * 访问官方文档 UI界面：http://localhost:8080/api/swagger-ui.html
     * json接口文档页面：http://localhost:8080/api/api-docs
     */

    @Bean
    public Docket createRestApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                //标注控制器Controller的位置
                .apis(RequestHandlerSelectors.basePackage("com.lncanswer.findingpartnersbackend.controller"))
                .paths(PathSelectors.any())
                .build();
    }

    /**
     * api信息
     * @return
     */
    private ApiInfo apiInfo(){
        return new ApiInfoBuilder()
                .title("伙伴匹配系统")
                .description("伙伴匹配系统接口文档")
                .termsOfServiceUrl("https://github.com/JOHNCHEN27")
                .contact(new Contact("lnc","ttps://github.com/JOHNCHEN27",""))
                .version("1.0")
                .build();
    }
}
