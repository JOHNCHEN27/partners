package com.lncanswer.usercenterbackend.utils;

import com.lncanswer.usercenterbackend.model.domain.dto.UserDTO;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/5 20:27
 * 本地线程工具类
 */
public class UserHolder {

    /**
     * 创建一个本地线程 存储用户登录信息
     */
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<UserDTO>();

    /**
     * 在本地线程中设置用户信息
     * @param user
     */
    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    /**
     * 在本地线程中获取用户信息
     * @return
     */
    public static UserDTO getUser(){
        return tl.get();
    }

    /**
     * 移除本地线程中的用户信息
     */
    public static void removeUser(){
        tl.remove();
    }
}
