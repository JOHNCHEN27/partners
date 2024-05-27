package com.lncanswer.usercenterbackend.utils;

import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/10 21:34
 * 登录请求拦截器 -- 判断用户是否登录
 */
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //此拦截器只需要判断是否当前用户已经登录
        if (UserHolder.getUser() == null){
            //用户未登录 直接拦截
            response.setStatus(401);
            return false;
        }
        //用户信息存在，放行
        return true;
    }
}
