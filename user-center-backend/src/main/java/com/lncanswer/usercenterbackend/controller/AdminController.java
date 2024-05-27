package com.lncanswer.usercenterbackend.controller;

import com.lncanswer.usercenterbackend.common.BaseResponse;
import com.lncanswer.usercenterbackend.common.ErrorCode;
import com.lncanswer.usercenterbackend.common.ResultUtils;
import com.lncanswer.usercenterbackend.exception.BusinessException;
import com.lncanswer.usercenterbackend.model.domain.User;
import com.lncanswer.usercenterbackend.service.AdminService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/5 13:59
 * 管理员 TODO 暂未校验当前用户是否为管理员
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Resource
    private AdminService adminService;

    /**
     * 创建用户
     * @param user 用户参数
     * @return 用户id
     */
    @PostMapping("/create")
    public BaseResponse<Long> createUser(@RequestBody User user){
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"用户名或密码为空");
        }
        Long userId = adminService.createUser(user);
        return ResultUtils.success(userId);
    }

    /**
     * 更新用户
     * @param user 用户参数
     * @return  boolean
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody User user){
        Long userId = user.getId();
        if (userId == null || userId == 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户id不能为空");
        }

        boolean result = adminService.updateUser(user);
        if (!result){
            ResultUtils.error(ErrorCode.SYSTEM_ERROR,"更新用户失败");
        }
        return ResultUtils.success();
    }

    /**
     * 根据用户id删除用户
     * @param user 用户参数
     * @return boolean
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUserById(@RequestBody User user){
        Long userId = user.getId();
        if (userId ==null || userId == 0){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"用户不存在");
        }
        boolean result = adminService.deleteUserById(userId);
        if (!result){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"删除用户失败");
        }
        return ResultUtils.success();
    }


    /**
     * 多条件查询用户
     * @param user 用户参数
     * @return 用户列表
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> conditionSelectUser(@RequestBody User user){

        List<User> userList = adminService.conditionQuery(user);
        if (userList.isEmpty()){
            return ResultUtils.error(ErrorCode.SELECT_ERROR,"用户列表为空");
        }
        return ResultUtils.success(userList);
    }
}
