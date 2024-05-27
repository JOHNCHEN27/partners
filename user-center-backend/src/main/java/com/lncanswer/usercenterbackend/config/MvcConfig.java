package com.lncanswer.usercenterbackend.config;

import com.lncanswer.usercenterbackend.utils.LoginInterceptor;
import com.lncanswer.usercenterbackend.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/10 21:37
 *  Mvc拦截器配置 --实现访问系统时拦截请求
 */
@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 配置MVC拦截器
     * 一个拦截器拦截所有请求 目的是刷新令牌时间
     * 一个拦截器拦截部分请求 目的是防止用户未登录访问关键资源
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //拦截部分请求  登录拦截器
        registry.addInterceptor(new LoginInterceptor())
                //根路径/api不需要添加到排除路径中，排除路径是相对于根路径而言的
                .excludePathPatterns("/user/register",
                                     "/user/login",
                                     "/user/logout",
                                     "/user/current",
                        "/files/*"
                                    ).order(1);
        //order指定哪个拦截器先执行， order权值越大执行顺序越低
        //拦截所有请求 Token刷新拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).order(0);
    }

}
