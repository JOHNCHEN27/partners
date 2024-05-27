package com.lncanswer.usercenterbackend.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lncanswer.usercenterbackend.common.ErrorCode;
import com.lncanswer.usercenterbackend.constant.MinioConstant;
import com.lncanswer.usercenterbackend.exception.BusinessException;
import com.lncanswer.usercenterbackend.mapper.AdminMapper;
import com.lncanswer.usercenterbackend.model.domain.User;
import com.lncanswer.usercenterbackend.service.AdminService;
import com.lncanswer.usercenterbackend.utils.RegexUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;


import javax.annotation.Resource;
import java.util.List;

import static com.lncanswer.usercenterbackend.constant.UserConstant.SALT;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/5/5 14:16
 */
@Slf4j
@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, User> implements AdminService {

    @Resource
    private AdminMapper adminMapper;

    /**
     * 创建用户
     * @param user 用户参数
     * @return  返回用户id
     */
    @Override
    public Long createUser(User user) {
        String userAccount = user.getUserAccount();
        String userPassword = user.getUserPassword();

        //账号不能包含特殊字符
       // String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        //Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (RegexUtils.isAccountInvalid(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }

        //账号长度不小于4位
        if (userAccount.length() <4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于四位");
        }

        //密码长度不小于八位
        if (userPassword.length() <8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于八位");
        }

        //判断用户是否已经存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        long count = this.count(queryWrapper);
        if (count != 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已经存在");
        }

        //给用户设置默认值
        //对用户输入的密码进行加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        user.setUserPassword(encryptPassword);
        String userName = RandomUtil.randomString(8);
        user.setUsername(userName);
        //改用minio分布式文件系统中的初始头像
        user.setAvatarUrl(MinioConstant.imageUrl);

        boolean save = this.save(user);
        if (!save){
            throw new RuntimeException("保存用户失败");
        }
        Long userId = user.getId();
        return userId;
    }

    /**
     * 更新用户信息
     * @param user 用户参数
     * @return boolean
     */
    @Override
    public boolean updateUser(User user) {
        String userPassword = user.getUserPassword();
        if (StringUtils.isAnyBlank(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码不能为空");
        }

        if (userPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码的长度不能小于八位");
        }

        //密码需要进行加密存储
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        user.setUserPassword(encryptPassword);

        //验证手机号格式
        String phone = user.getPhone();
        //isNotBlank判断字符串不为空且不为null
        if (StringUtils.isNotBlank(phone)){
            //验证手机号格式
            //"^1[3-9]\\d{9}$|"
            // 匹配以1开头的11位数字手机号格式如：13185217412
            //"^1[3-9]\\d{1}[-\\s]\\d{4}[-\\s]\\d{4}$|"
            // 匹配以1开头的带区号的手机号，格式如：131 8521 7412 或 131-8521-7412
            //"^\\(1[3-9]\\d{1}\\)\\d{4}-\\d{4}$|"
            // 匹配以1开头的带区号的手机号，格式如：(131) 8521-7412
            //"^(?:\\(\\+\\d{2}\\)|\\+\\d{2})(\\d{11})$|"
            // 匹配国际格式的手机号，如：(+86)13645678906 或 +8613645678906
            //"^0\\d{3}-\\d{7}$|"
            // 匹配以0开头的带四位区号的座机号，格式如：0755-1234567
            //"^0\\d{2}-\\d{8}$"
            // 匹配以0开头的带三位区号的座机号，格式如：010-12345678

//            boolean matches = Pattern.matches("^1[3-9]\\d{9}$|" +
//                    "^1[3-9]\\d{1}[-\\s]\\d{4}[-\\s]\\d{4}$|" +
//                    "^\\(1[3-9]\\d{1}\\)\\d{4}-\\d{4}$|" +
//                    "^(?:\\(\\+\\d{2}\\)|\\+\\d{2})(\\d{11})$|" +
//                    "^0\\d{3}-\\d{7}$|" +
//                    "^0\\d{2}-\\d{8}$", phone);

            //改为工具类判断手机号正则
            if (RegexUtils.isPhoneInvalid(phone)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"手机号格式有误");
            }
        }

        //验证邮箱格式
        String email = user.getEmail();
        if (StringUtils.isNotBlank(email)) {
           // String validEmailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
           // boolean matches = Pattern.compile(validEmailPattern).matcher(email).matches();

            //改为工具类判断邮箱正则
            if (RegexUtils.isEmailInvalid(email)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "邮箱格式错误");
            }
        }
        //更新用户到数据库
        int result = adminMapper.updateById(user);
        if (result == 0){
            log.info("更新用户到数据失败： {}",user);
            return false;
        }
        return true;
    }

    /**
     * 根据用户 id 删除用户
     * @param userId 用户id
     * @return boolean
     */
    @Override
    public boolean deleteUserById(Long userId) {
        int result = adminMapper.deleteById(userId);
        if (result == 0){
            return false;
        }
        return true;
    }

    /**
     * 多条件查询用户
     * @param user 用户参数
     * @return 用户列表
     */
    @Override
    public List<User> conditionQuery(User user) {
        //构建查询条件
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        //不为空则添加到QueryWrapper中
        queryWrapper.like(StringUtils.isNotBlank(user.getUsername()),User::getUsername,user.getUsername());
        queryWrapper.eq(StringUtils.isNotBlank(user.getUserAccount()),User::getUserAccount,user.getUserAccount());
        queryWrapper.eq(StringUtils.isNotBlank(user.getAvatarUrl()),User::getAvatarUrl,user.getAvatarUrl());
        queryWrapper.eq(user.getGender() != null,User::getGender,user.getGender());
        queryWrapper.eq(StringUtils.isNotBlank(user.getPhone()),User::getPhone,user.getPhone());
        queryWrapper.eq(StringUtils.isNotBlank(user.getEmail()),User::getEmail,user.getEmail());
        queryWrapper.eq(user.getUserStatus() != null,User::getUserStatus,user.getUserStatus());
        queryWrapper.eq(StringUtils.isNotBlank(user.getPlanetCode()),User::getPlanetCode,user.getPlanetCode());
        queryWrapper.eq(user.getUserRole() != null,User::getUserRole,user.getUserRole());
        queryWrapper.eq(user.getCreateTime()!= null, User::getCreateTime,user.getCreateTime());


        List<User> userList = adminMapper.selectList(queryWrapper);

        return userList;
    }
}
