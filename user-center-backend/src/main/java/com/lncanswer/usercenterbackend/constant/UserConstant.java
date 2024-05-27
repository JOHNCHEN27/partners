package com.lncanswer.usercenterbackend.constant;

import org.springframework.beans.factory.annotation.Value;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/25 17:11
 */
public interface UserConstant {

    //管理员权限
    int ADMIN_ROLE = 1;

    //默认权限
    int DEFAULT_ROLE = 0;


    /**
     * 盐值加密 -- 混淆密码
     */
      String SALT = "sin";


}
