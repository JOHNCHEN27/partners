package com.lncanswer.usercenterbackend;

import com.lncanswer.usercenterbackend.mapper.UserMapper;
import com.lncanswer.usercenterbackend.model.domain.User;
import com.lncanswer.usercenterbackend.service.AdminService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
class UserCenterBackendApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AdminService adminService;

    @Test
    void contextLoads() {
        List<User> userList = userMapper.selectList(null);
        Assert.assertEquals(5,userList.size());
        userList.forEach(System.out::println);
    }


    /**
     * 测试管理员创建用户
     */
    @Test
    void testCreateUser(){
        User user = new User();
        String userAccount = "qwer";
        String userPassword = "qwer1234";
        user.setUserAccount(userAccount);
        user.setUserPassword(userPassword);
        Long result = adminService.createUser(user);
        //断言来进行单元测试
        Assertions.assertEquals(null,result);
    }



}
