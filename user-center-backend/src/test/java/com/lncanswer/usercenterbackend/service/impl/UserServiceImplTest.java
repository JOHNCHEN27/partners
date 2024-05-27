package com.lncanswer.usercenterbackend.service.impl;

import com.lncanswer.usercenterbackend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/25 14:20
 */
@SpringBootTest
class UserServiceImplTest {

    @Resource
    UserService userService;

    @Test
    void testRegisterUser(){
       // long id = userService.registerUser("lncanswer", "12345678", "12345678","001");
      //  System.out.println(id);
    }
}