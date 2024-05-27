package com.lncanswer.usercenterbackend.service;
import java.util.Date;


import com.lncanswer.usercenterbackend.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;



/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/25 13:10
 */
@SpringBootTest
class UserServiceTest {

    @Resource
    private UserService userService;

    @Test
    void testUserService(){
        User user = new User();
        user.setUsername("zhangsan");
        user.setUserAccount("lnc");
        user.setAvatarUrl("https://b0.bdstatic.com/2609910f2091c0743d82fd09ff76c400.jpg@h_1280");
        user.setGender(0);
        user.setUserPassword("123456");
        user.setPhone("");
        user.setEmail("");

        boolean save = userService.save(user);


    }

}