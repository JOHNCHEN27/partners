package com.lncanswer.usercenterbackend.service;

import com.lncanswer.usercenterbackend.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @author JohnChen
* @description 针对表【user(用户)】的数据库操作Service
* @createDate 2024-04-25 13:01:41
*/
public interface UserService extends IService<User> {

    /**
     * 用户注册
     * @param userAccount 用户账户
     * @param password    用户密码
     * @param checkPassword 用户校验密码（二次输入的密码）
     * @return 新用户id
     */
    long registerUser(String userAccount, String password, String checkPassword,String planetCode);

    /**
     * 用户登录
     * @param userAccount 用户账户
     * @param password    用户密码
     * @param checkCode   校验码(可扩展)
     * @param request    servletRequest请求 --前端发送过来的请求
     * @return 返回用户脱敏之后的信息
     */
    User login(String userAccount, String password, String checkCode, HttpServletRequest request, HttpServletResponse response);

    /**
     * 查询用户泪飙
     * @param username 用户名称
     * @return 返回一个用户列表
     */
    List<User> queryUserList(String username,HttpServletRequest request);

    /**
     * 根据id删除用户
     * @param id   用户id
     * @param request http请求
     * @return 返回true or false
     */
    boolean deleteById(long id,HttpServletRequest request);

    /**
     * 获取当前登录用户信息
     * @param request httpRequest请求
     * @return 返回用户最新信息
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 用户注销
     * @param request httpRequest请求对象
     */
    void userLogout(HttpServletRequest request);
}
