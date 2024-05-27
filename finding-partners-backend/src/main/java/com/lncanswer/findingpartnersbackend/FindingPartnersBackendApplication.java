package com.lncanswer.findingpartnersbackend;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lncanswer.findingpartnersbackend.mapper")
public class FindingPartnersBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindingPartnersBackendApplication.class, args);
    }

}
