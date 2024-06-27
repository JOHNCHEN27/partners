package com.lncanswer.findingpartnersbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@MapperScan("com.lncanswer.findingpartnersbackend.mapper")
@EnableSwagger2
@EnableScheduling //开启springboot自带的定时任务功能
public class FindingPartnersBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindingPartnersBackendApplication.class, args);
    }

}
