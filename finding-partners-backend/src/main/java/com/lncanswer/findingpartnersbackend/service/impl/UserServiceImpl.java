package com.lncanswer.findingpartnersbackend.service.impl;
import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lncanswer.findingpartnersbackend.common.ErrorCode;
import com.lncanswer.findingpartnersbackend.constant.MinioConstant;
import com.lncanswer.findingpartnersbackend.constant.RedisConstant;
import com.lncanswer.findingpartnersbackend.exception.BusinessException;
import com.lncanswer.findingpartnersbackend.model.domain.User;
import com.lncanswer.findingpartnersbackend.model.domain.dto.UserDTO;
import com.lncanswer.findingpartnersbackend.service.UserService;
import com.lncanswer.findingpartnersbackend.mapper.UserMapper;
import com.lncanswer.findingpartnersbackend.utils.RegexUtils;
import com.lncanswer.findingpartnersbackend.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.lncanswer.findingpartnersbackend.constant.RedisConstant.LOGIN_USER_KEY;
import static com.lncanswer.findingpartnersbackend.constant.UserConstant.ADMIN_ROLE;
import static com.lncanswer.findingpartnersbackend.constant.UserConstant.SALT;

/**
* @author JohnChen
* @description 针对表【user(用户)】的数据库操作Service实现
* @createDate 2024-04-25 13:01:41
*/
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
    implements UserService{

//    /**
//     * 盐值加密 -- 混淆密码
//     */
//    private static final String SALT = "sin";

    /**
     * 用户登录状态键值
     */
    //private static final String USER_LOGIN_STATE = "userLoginState";
    @Resource
    private UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     *
     * @param userAccount 用户账户
     * @param password    用户密码
     * @param checkPassword 用户校验密码（二次输入的密码）
     * @return 新用户id
     */
    @Override
    public long registerUser(String userAccount, String password, String checkPassword,String planetCode) {
        //1、校验是否为空 利用commons lang包下的StringUtils
        if (StringUtils.isAnyBlank(userAccount,password,checkPassword)){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4");
        }

        //账户不能包含特殊字符
//        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
//        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);

        //利用工具类来校验账号正则
        if (RegexUtils.isAccountInvalid(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号不能包含特殊字符");
        }


        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user != null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号已经存在");
        }

        if (password.length() < 8 || checkPassword.length() < 8){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码长度小于八位");
        }

        if (!password.equals(checkPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"两次输入的密码不一致");
        }

//        if (planetCode.length() > 5){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号长度大于5");
//        }

        //查询编号是否重复
//        LambdaQueryWrapper<User> queryWrapper1 = new LambdaQueryWrapper<User>();
//        queryWrapper1.eq(User::getPlanetCode,planetCode);
//        long count = this.count(queryWrapper1);
//        if (count >0){
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"编号重复");
//        }

        // 对密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        log.info("加密之后的密码：{}",encryptPassword);

        //创建用户存储到数据库
        User newUser = new User();
        String username = RandomUtil.randomString(8);
        newUser.setUsername(username);
        newUser.setUserAccount(userAccount);
        newUser.setPlanetCode(planetCode);
        //使用默认头像 改用minio中的默认头像
        newUser.setAvatarUrl(MinioConstant.imageUrl);
        newUser.setUserPassword(encryptPassword);

        boolean saveResult = this.save(newUser);
        if (!saveResult){
            throw new RuntimeException("保存用户失败");
        }
        return newUser.getId();
    }

    /**
     *
     * @param userAccount 用户账户
     * @param password    用户密码
     * @param checkCode   校验码(可扩展)
     * @param request   servletRequest
     * @return 返回用户脱敏之后的信息
     */
    @Override
    public User login(String userAccount, String password, String checkCode, HttpServletRequest request, HttpServletResponse response) {
        //1、判断是否为空
        if (StringUtils.isAnyBlank(userAccount,password)){
            throw new  BusinessException(ErrorCode.PARAMS_ERROR,"输入的账号密码不能为空");
        }

        if (userAccount.length() < 4){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号长度小于4");
        }

        //2、账户不能包含特殊字符
       // String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
       // Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);

        //改用工具类判断账号是否符合要求
        if (RegexUtils.isAccountInvalid(userAccount)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号包含特殊字符");
        }
        //判断用户是否注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount,userAccount);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号未注册");
        }
        if (password.length() < 8 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号密码小于8");
        }

        //3、校验密码是否和数据库中存储的密文密码一致
        String enterPassword = DigestUtils.md5DigestAsHex((SALT + password).getBytes());
        String userPassword = user.getUserPassword();
        if (!enterPassword.equals(userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"输入的密码有误");
        }
        log.info("enterPassword = {}, userPassword = {}",enterPassword,userPassword);

        //4、用户数据脱敏 返回用户信息
        User safetyUser = safetyUser(user);


        //5、记录用户登录状态
       // request.getSession().setAttribute(USER_LOGIN_STATE,safetyUser);

        //用redis记录用户登录状态
        //生成一个token 作为登录令牌 TODO 后续可以优化 用JWT令牌
        String token = UUID.randomUUID().toString();
        //存储在redis中的用户信息简化，用UserDTO
        UserDTO userDTO = BeanUtil.copyProperties(safetyUser, UserDTO.class);
        //将UserDto对象所有字段类型转化为String 类型，并且存储在HashMap中
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((filedName, filedValue) ->
                               filedValue != null  ? filedValue.toString() : ""));
        //存储到redis中 Key前缀加token组成完整的key
        stringRedisTemplate.opsForHash().putAll(LOGIN_USER_KEY+token,userMap);
        //设置过期时间
        stringRedisTemplate.expire(LOGIN_USER_KEY+token, RedisConstant.LOGIN_USER_TTL, TimeUnit.SECONDS);

        //将当前token存储到请求头中返回，Header
        request.getSession().setAttribute("token",token);
        //response.setHeader("authorization",token);
        log.info("service token = {}",token);

        //返回脱敏用户
        return safetyUser;
    }

    /**
     * 查询用户列表
     * @param username 用户名称
     * @return 返回用户列表
     */
    @Override
    public List<User> queryUserList(String username,HttpServletRequest request) {
        //仅管理员可查询
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"仅管理员可查询");
        }

        LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();

        if (StringUtils.isNotBlank(username)){
            lambdaQueryWrapper.like(User::getUsername,username);
        }

        List<User> list = this.list(lambdaQueryWrapper);
        if (list == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"查询用户不存在");
        }
        return list.stream().map(this::safetyUser).collect(Collectors.toList());
    }

    /**
     * 删除用户
     * @param id   用户id
     * @param request http请求
     * @return true or false
     */
    @Override
    public boolean deleteById(long id, HttpServletRequest request) {
        //判断当前用户是否为管理员
        if (!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH,"仅管理员可查看");
        }

        if ( id <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户id小于0");
        }
        return this.removeById(id);
    }

    /**
     * 获取当前登录用户信息
     * @param request httpRequest请求
     * @return 返回用户信息
     */
    @Override
    public User getCurrentUser(HttpServletRequest request) {
//        Object userOjb = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User user = (User) userOjb;

        //从本地线程中获取当前用户 TODO 后续改进前端
//        UserDTO user = UserHolder.getUser();
//        if (user == null){
//            return null;
//        }

        Object attribute = request.getSession().getAttribute("token");
        if (attribute == null){
            return null;
        }
        String token = (String) attribute;
        if (token.isEmpty()){
            //token不存在直接返回null
            return null;
        }
        log.info("getToken = {}",token);
        //从redis中获取用户信息
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(LOGIN_USER_KEY + token);
        if (userMap.isEmpty()){
            return null;
        }
        //转化为userDto
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);

        //获取当前登录的用户状态之后，再去数据库查询一次 是为了保证用户的信息是及时的
        Long userId = userDTO.getId();
        User newUser = this.getById(userId);
        return safetyUser(newUser);
    }

    /**
     * 用户注销
     * @param request httpRequest请求对象
     * @return
     */
    @Override
    public void userLogout(HttpServletRequest request) {
       // request.getSession().removeAttribute(USER_LOGIN_STATE);

        //改用redis
        String token = (String) request.getSession().getAttribute("token");
        stringRedisTemplate.delete(LOGIN_USER_KEY + token);
        //移除session
        request.getSession().removeAttribute("token");

        //在拦截器统一移除本地线程用户 暂时不需要
//        UserHolder.removeUser();
    }

    /**
     * 是否为管理员
     * @param request
     * @return true or false
     */
    private boolean isAdmin(HttpServletRequest request){
        //User user = (User)request.getSession().getAttribute(USER_LOGIN_STATE);

        //从UserHolder本地线程获取当前用户，判断是否为管理员
        UserDTO user = UserHolder.getUser();
        if (user == null || user.getUserRole() != ADMIN_ROLE){
            return false;
        }
        return true;
    }

    /**
     * 根据标签列表搜索用户
     * @param tagNameList 用户所拥有的标签
     * @return
     */
    @Override
    public List<User> searchUsersByTags(List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
//
//        //根据标签查询用户
//        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
//
//        for (String tagName : tagNameList) {
//            queryWrapper = queryWrapper.like(User::getTags,tagName);
//        }
//
//        List<User> userList = userMapper.selectList(queryWrapper);

        // 基于内存查询 -- 首先查询所有用户
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = userMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //在内存中判断是否包含要求的标签
        return userList.stream().filter(user -> {
            String tagsStr = user.getTags();
            if (StringUtils.isBlank(tagsStr)){
                return false;
            }
            //利用gson 将字符串转化为json对象
            Set<String> tagsNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>(){}.getType());
            //Optional.ofNullable 判断对象是否为空， 如果为空，给一个默认值
            tagsNameSet = Optional.ofNullable(tagsNameSet).orElse(new HashSet<>());
            //反序列化
            //gson.toJson(tagsNameList);
            for (String tagName : tagNameList){
                if (!tagsNameSet.contains(tagName)){
                    return false;
                }
            }
            return true;
        }).map(this::safetyUser).collect(Collectors.toList());

      //  return userList.stream().map(this::safetyUser).collect(Collectors.toList());
    }

    /**
     * 更新用户信息
     * @param userDTO userDto
     * @return 返回Boolean
     */
    @Override
    public Boolean updateUser(UserDTO userDTO,HttpServletRequest request) {
        //仅管理员和自己可修改
        //获取当前登录用户
        User currentUser = getCurrentUser(request);
        if (currentUser == null ){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        if (Objects.equals(currentUser.getId(), userDTO.getId()) || currentUser.getUserRole() == 1){
            //更新数据
            User newUser = new User();
            BeanUtil.copyProperties(userDTO,newUser);
            boolean result = updateById(newUser);

            return result;
        } else {
            throw new BusinessException("不允许修改其他用户信息",40110,"");
        }

    }

    /**
     * 获取推荐用户
     * @param request httprequest
     * @return List<User>
     */
    @Override
    public List<User> getRecommendUsers(HttpServletRequest request) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        List<User> userList = list(queryWrapper);
        List<User> list = userList.stream().map(this::safetyUser).collect(Collectors.toList());
        return list;
    }

    /**
     * 用户脱敏
     * @param user 用户信息
     * @return 安全用户信息
     */
    private User safetyUser(User user){
        if (user == null){
            return null;
        }
        User safetyUser = new User();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPlanetCode(user.getPlanetCode());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setUserRole(user.getUserRole());
        return safetyUser;
    }
}




