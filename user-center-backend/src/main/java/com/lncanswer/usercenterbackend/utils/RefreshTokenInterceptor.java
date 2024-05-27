package com.lncanswer.usercenterbackend.utils;

import cn.hutool.core.bean.BeanUtil;
import com.lncanswer.usercenterbackend.constant.RedisConstant;
import com.lncanswer.usercenterbackend.model.domain.dto.UserDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/10 21:19
 * 刷新令牌拦截器 -- 拦截所有
 */
@Slf4j
public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    /**
     * 构造函数 -- 用于给redis对象赋初始值
     * @param stringRedisTemplate redis对象
     */
    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    /**
     * 请求进入系统拦截 --验证逻辑通过之后释放请求 允许访问并且重新刷新令牌有效期
     * @param request httpRequest对象
     * @param response httpResponse对象
     * @param handler  处理者
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.取出请求头中携带的token  用于读取redis存储的用户（redis实现单点登录）
        Object attribute = request.getSession().getAttribute("token");
        if (attribute == null){
            return true;
        }
        String token = (String)attribute;
        log.info("token = {}",token);
        //1.1 token为空释放请求 进入第二个拦截器
        if (StringUtils.isBlank(token)){
            return true;
        }

        //2 基于token及公共key前缀从redis中获取用户
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(RedisConstant.LOGIN_USER_KEY + token);
        //3 判断用户是否存在
        if (userMap.isEmpty()){
            //不存在 放行到第二拦截器
            return true;
        }

        //4 将查询到的userMap转化为UserDto对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        //5 存储信息到本地线程 ThreadLocal
        UserHolder.saveUser(userDTO);

        //6 刷新令牌，刷新token的有效期
        stringRedisTemplate.expire(RedisConstant.LOGIN_USER_KEY +token,RedisConstant.LOGIN_USER_TTL, TimeUnit.SECONDS);

        //7 放行
        return true;

    }

    /**
     * 请求完成之后，response响应返回时 释放本地线程中的用户
     * @param request httpRequest对象
     * @param response httpResponse对象
     * @param handler handler
     * @param ex 异常对象
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        // 移除本地线程用户
        UserHolder.removeUser();
    }
}
