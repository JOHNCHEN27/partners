package com.lncanswer.usercenterbackend.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lncanswer.usercenterbackend.common.BaseResponse;
import com.lncanswer.usercenterbackend.model.domain.User;

import java.util.List;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/5 14:16
 */
public interface AdminService extends IService<User> {

    /**
     * 创建用户
     * @param user 用户参数
     * @return  返回用户id
     */
    Long createUser(User user);

    /**
     * 更新用户信息
     * @param user 用户参数
     * @return boolean
     */
    boolean updateUser(User user);

    /**
     * 根据用户 id 删除用户
     * @param userId 用户id
     * @return boolean
     */
    boolean deleteUserById(Long userId);

    /**
     * 多条件查询用户
     * @param user 用户参数
     * @return 用户列表
     */
    List<User> conditionQuery(User user);
}
