package com.lncanswer.findingpartnersbackend.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/27 17:59
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String host;
    private String port;
    private int database;

    @Bean
    public RedissonClient redissonClient() throws IOException {
        //创建配置
        Config config = new Config();
        String address = String.format("redis://%s:%s",host,port);
        config.useSingleServer().setAddress(address).setDatabase(database);
        //创建实例
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }
}
