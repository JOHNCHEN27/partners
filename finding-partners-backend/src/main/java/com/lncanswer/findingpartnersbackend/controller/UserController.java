package com.lncanswer.findingpartnersbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lncanswer.findingpartnersbackend.common.BaseResponse;
import com.lncanswer.findingpartnersbackend.common.ErrorCode;
import com.lncanswer.findingpartnersbackend.common.ResultUtils;
import com.lncanswer.findingpartnersbackend.exception.BusinessException;
import com.lncanswer.findingpartnersbackend.model.domain.User;
import com.lncanswer.findingpartnersbackend.model.domain.dto.UserDTO;
import com.lncanswer.findingpartnersbackend.model.domain.request.UserLoginRequest;
import com.lncanswer.findingpartnersbackend.model.domain.request.UserRegisterRequest;
import com.lncanswer.findingpartnersbackend.model.domain.vo.UserVO;
import com.lncanswer.findingpartnersbackend.service.UserService;
import com.lncanswer.findingpartnersbackend.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/25 15:51
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @param registerRequest 注册请求对象（封装接受的参数）
     * @return 返回统一响应数据
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest registerRequest){
        if (registerRequest == null){
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();
        String planetCode = registerRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        long userId = userService.registerUser(userAccount, userPassword, checkPassword,planetCode);
        return new BaseResponse<>(0,userId,"ok");
    }

    /**
     * 用户登录
     * @param loginRequest 登录请求对象（封装登录接受的参数）
     * @param request httpRequest对象 --用来处理session
     * @return 返回统一响应数据
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request, HttpServletResponse response){
        if (loginRequest == null){
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();
        String checkCode = loginRequest.getCheckCode();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user  = userService.login(userAccount, userPassword, checkCode, request,response);
        return new BaseResponse<>(0,user,"ok");
    }

    /**
     * 用户注销
     * @param request httpRequest对象 --处理session
     * @return  返回统一响应数据
     */
    @PostMapping("/logout")
    public BaseResponse userLogout(HttpServletRequest request){

        userService.userLogout(request);
        return ResultUtils.success();
    }

    /**
     * 获取当前用户
     * @param request httpRequest请求 -- 获取session
     * @return 返回统一响应数据
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        if (request == null){
            return ResultUtils.error(ErrorCode.NULL_ERROR);
        }
        User currentUser = userService.getCurrentUser(request);
        return ResultUtils.success(currentUser);
    }

    /**
     * 获取用户列表
     * @param username 用户昵称
     * @param request  httpRequest
     * @return 返回统一响应数据
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> searchUsers(String username,HttpServletRequest request){
        System.out.println("username = " + username);
        List<User> userList = userService.queryUserList(username, request);
        return ResultUtils.success(userList);
    }

    /**
     * 根据标签列表查询用户列表返回
     * @param tagNameList 标签列表
     * @return 用户列表
     */
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUsersByTags(tagNameList);
        return ResultUtils.success(userList);
    }


    /**
     * 获取推荐用户
     * @param request HttpServletRequest
     * @return  BaseResponse<List<User>>
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(long pageSize, long pageNum,HttpServletRequest request){
        Page<User> recommendUsers = userService.getRecommendUsers(pageSize,pageNum,request);
        return ResultUtils.success(recommendUsers);

    }


    /**
     * 更新用户信息
     * @param userDTO userDto
     * @param request HttpServletRequest
     * @return 统一响应对象 BaseResponse<Boolean>
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserDTO userDTO,HttpServletRequest request){
        //1、校验参数是否为空
        if (userDTO == null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //2、校验权限  todo 如果用户没有传递新的值，则直接报错
        //3、触发更新
        Boolean isSuccess = userService.updateUser(userDTO, request);
        return ResultUtils.success(isSuccess);

    }


    /**
     * 删除用户
     * @param id  用户id
     * @param request httpRequest
     * @return 返回统一响应数据
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id,HttpServletRequest request){
        boolean b = userService.deleteById(id, request);
        return ResultUtils.success(b);
    }


    /**
     * 判断用户是否已经登录
     * @param request httpRequest对象
     * @return 返回token
     */
    @GetMapping("/islogin")
    public BaseResponse<String> islogin(HttpServletRequest request,HttpServletResponse response){
        //判断是否有token 有则已登录
        String token = request.getHeader("authorization");
        log.info("token = {}",token);
        if (token.isEmpty()){
            return null;
        }
        //返回token
        response.setHeader("authorization",token);
        return  ResultUtils.success(token);
    }

    /**
     *  获取最匹配的用户
     * @param num
     * @return
     */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num){
        if (num <=0 || num > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserDTO user = UserHolder.getUser();
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return ResultUtils.success(userService.matchUsers(num,user));
    }
}
