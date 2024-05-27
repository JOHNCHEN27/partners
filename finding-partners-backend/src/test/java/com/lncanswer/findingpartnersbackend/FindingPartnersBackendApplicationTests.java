package com.lncanswer.findingpartnersbackend;

import com.lncanswer.findingpartnersbackend.mapper.UserMapper;
import com.lncanswer.findingpartnersbackend.model.domain.User;
import com.lncanswer.findingpartnersbackend.service.AdminService;
import com.lncanswer.findingpartnersbackend.service.UserService;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class FindingPartnersBackendApplicationTests {

    @Resource
    private UserMapper userMapper;

    @Resource
    private AdminService adminService;

    @Resource
    private UserService userService;

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


    /**
     * 测试 -- 根据标签查询用户
     */
    @Test
    void searchUsersByTags(){
        List<String> list = new ArrayList<>();
        list.add("java");
        List<User> users = userService.searchUsersByTags(list);
        System.out.println("users = " + users);
        Assertions.assertNotNull(users);
    }



}
