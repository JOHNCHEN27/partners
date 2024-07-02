package com.lncanswer.findingpartnersbackend.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lncanswer.findingpartnersbackend.common.ErrorCode;
import com.lncanswer.findingpartnersbackend.constant.RedissonConstant;
import com.lncanswer.findingpartnersbackend.exception.BusinessException;
import com.lncanswer.findingpartnersbackend.model.domain.Team;
import com.lncanswer.findingpartnersbackend.model.domain.User;
import com.lncanswer.findingpartnersbackend.model.domain.UserTeam;
import com.lncanswer.findingpartnersbackend.model.domain.dto.TeamQuery;
import com.lncanswer.findingpartnersbackend.model.domain.dto.UserDTO;
import com.lncanswer.findingpartnersbackend.model.domain.enums.TeamStatusEnum;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamJoinRequest;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamQuitRequest;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamUpdateRequest;
import com.lncanswer.findingpartnersbackend.model.domain.vo.TeamUserVO;
import com.lncanswer.findingpartnersbackend.model.domain.vo.UserVO;
import com.lncanswer.findingpartnersbackend.service.TeamService;
import com.lncanswer.findingpartnersbackend.mapper.TeamMapper;
import com.lncanswer.findingpartnersbackend.service.UserService;
import com.lncanswer.findingpartnersbackend.service.UserTeamService;
import com.lncanswer.findingpartnersbackend.utils.UserHolder;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;


import javax.annotation.Resource;

import javax.servlet.http.HttpServletRequest;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
* @author JohnChen
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-06-28 10:35:54
*/
@Service
@Slf4j
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;
    @Resource
    private RedissonClient redissonClient;

    /**
     * 添加队伍
     * @param team team
     * @return Long id
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long addTeam(Team team) {
        if (team == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断当前用户是否登录
        UserDTO user = UserHolder.getUser();
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }

        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        //校验队伍信息 -- 队伍人数、队伍标题、描述、是否公开、是否有密码、最多能创建几个队伍
        if (maxNum <1 || maxNum> 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不满足条件");
        }
        if (StringUtils.isBlank(team.getName()) || team.getName().length() > 20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名称不满足要求");
        }
        if (StringUtils.isNotBlank(team.getPassword()) && team.getDescription().length() > 512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍描述过长");
        }

        //判断队伍状态 是否为加密 -- 利用枚举类
        int status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValues(status);
        if (statusEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不满足要求");
        }
        //如果是加密状态，一定要有密码，并且密码长度<=32
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password)|| password.length() > 32)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码设置不正确");
        }
        //超时时间 > 当前时间
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍已过期");
        }

        //用户最多创建五个队伍 todo 优化
        LambdaQueryWrapper<UserTeam> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserTeam::getUserId,user.getId());
        long teamNumber = userTeamService.count(queryWrapper);
        if (teamNumber >= 5){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"最多创建5个队伍");
        }

        //插入数据到队伍表
        team.setUserId(user.getId());
        boolean save = save(team);
        if (!save){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"添加队伍失败");
        }

        //插入数据到队伍关系表中
        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(team.getId());
        userTeam.setUserId(user.getId());
        userTeam.setJoinTime(new Date());
        boolean isSaved = userTeamService.save(userTeam);
        if (!isSaved){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"保存用户队伍关系数据失败");
        }

        return team.getId();

    }

    /**
     *  搜索队伍
     * @param teamQuery 请求参数
     * @return  List<TeamUserVO>
     */
    @Override
    public List<TeamUserVO> listTeam(TeamQuery teamQuery, boolean isAdmin) {
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //根据队伍名称、最大人数等条件搜索队伍
        String teamName = teamQuery.getName();
        Integer maxNum = teamQuery.getMaxNum();
        Long teamId = teamQuery.getId();
        String description = teamQuery.getDescription();
        Integer status = teamQuery.getStatus();
        Long userId = teamQuery.getUserId();
        List<Long> idList = teamQuery.getIdList();

        //根据状态判断 管理员可查询加密和非公开的房间
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValues(status);
        if (statusEnum == null){
            statusEnum = TeamStatusEnum.PUBLIC;
        }
        if (!isAdmin && statusEnum.equals(TeamStatusEnum.PRIVATE)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        LambdaQueryWrapper<Team> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(status != null && status >-1,Team::getStatus,status);
        lambdaQueryWrapper.like(StringUtils.isNotBlank(teamName),Team::getName,teamName);
        lambdaQueryWrapper.eq(maxNum != null && maxNum > 0,Team::getMaxNum,maxNum);
        lambdaQueryWrapper.eq(teamId != null && teamId > 0,Team::getId,teamId);
        lambdaQueryWrapper.like(StringUtils.isNotBlank(description),Team::getDescription,description);
        lambdaQueryWrapper.eq(userId != null && userId > 0, Team::getUserId,userId);
        lambdaQueryWrapper.in(idList != null && !idList.isEmpty(),Team::getId,idList);

        //不展示已过期的队伍信息
        lambdaQueryWrapper.and(qw -> qw.gt(Team::getExpireTime, new Date()).or().isNull(Team::getExpireTime));

        String searchText = teamQuery.getSearchText();
        if (StringUtils.isNotBlank(searchText)){
            lambdaQueryWrapper.and(qw -> qw.like(Team::getName,searchText).or().like(Team::getDescription,searchText));
        }



        List<Team> teamList = this.list(lambdaQueryWrapper);
        if (CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }

        ArrayList<TeamUserVO> teamUserVoList = new ArrayList<>();
        //关联查询创建人用户信息
        for (Team team : teamList) {
            Long userId1 = team.getUserId();
            if (userId1 == null){
                continue;
            }
            User user = userService.getById(userId1);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            //脱敏用户信息
            if (user != null){
                UserVO userVO = new UserVO();
                BeanUtils.copyProperties(user,userVO);
                teamUserVO.setUserList(userVO);
            }
            teamUserVoList.add(teamUserVO);
        }

        return teamUserVoList;
    }

    /**
     * 更新队伍信息
     * @param teamUpdateRequest 请求参数封装类
     * @return boolean
     */
    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
         if (teamUpdateRequest ==null){
             throw new BusinessException(ErrorCode.PARAMS_ERROR);
         }
        Long id= teamUpdateRequest.getId();
         if (id == null || id < 0){
             throw new BusinessException(ErrorCode.PARAMS_ERROR);
         }
        Team oldTeam = this.getById(id);
         if (oldTeam == null){
             throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
         }

        //判断新值是否和旧值一致 ，如果完全一致不需要更新
        if (teamUpdateRequest.getName().equals(oldTeam.getName()) && teamUpdateRequest.getDescription().equals(oldTeam.getDescription())
                && teamUpdateRequest.getPassword().equals(oldTeam.getPassword()) && teamUpdateRequest.getExpireTime() == oldTeam.getExpireTime()
                && teamUpdateRequest.getStatus().equals(oldTeam.getStatus())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有要更新的值");
        }

         //只有管理原和用户自己可以修改
        UserDTO user = UserHolder.getUser();
        if (!user.getId().equals(oldTeam.getUserId()) && !userService.isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValues(teamUpdateRequest.getStatus());
        if (statusEnum.equals(TeamStatusEnum.SECRET)){
            if (StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        return this.updateById(updateTeam);
    }

    /**
     * 用户加入队伍
     * @param teamJoinRequest 请求参数对象
     * @return boolean
     */
    @Override
    public boolean joinTeam(TeamJoinRequest teamJoinRequest) {
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍必须存在，只能加入未满、未过期的队伍
        Long teamId = teamJoinRequest.getTeamId();
        if (teamId == null || teamId <= 0  ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.SELECT_ERROR,"队伍不存在");
        }
        //判断队伍是否已满
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",teamId);
        long count = userTeamService.count(queryWrapper);
        if (count >= team.getMaxNum()){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍已满");
        }

        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.SELECT_ERROR,"队伍已过期");
        }
        //禁止加入私有的队伍
        Integer status = team.getStatus();
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValues(status);
        if (TeamStatusEnum.PRIVATE.equals(statusEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH,"不允许加入私有队伍");
        }
        //如果是加密队伍 进行密码匹配
        if (TeamStatusEnum.SECRET.equals(statusEnum)){
            String password = teamJoinRequest.getPassword();
            if (StringUtils.isBlank(password) || !password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"密码错误");
            }
        }

        //最多加入五个队伍
        UserDTO user = UserHolder.getUser();
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = user.getId();
        //只有一个线程获取锁
        RLock lock = redissonClient.getLock(RedissonConstant.JOIN_TEAM_LOCK);
        try {
            while (true){
            //抢到锁并执行
            if (lock.tryLock(0,-1, TimeUnit.MILLISECONDS)) {
                LambdaQueryWrapper<UserTeam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(UserTeam::getUserId, userId);
                List<UserTeam> userTeamList = userTeamService.list(lambdaQueryWrapper);
                if (userTeamList.size() >= 5) {
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR, "加入队伍上限");
                }
                for (UserTeam userTeam : userTeamList) {
                    if (userTeam.getTeamId().equals(teamId)) {
                        throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不允许重复加入");
                    }
                }

                //新增队伍用户关系信息
                UserTeam userTeam = new UserTeam();
                BeanUtils.copyProperties(teamJoinRequest, userTeam);
                userTeam.setUserId(userId);
                userTeam.setJoinTime(new Date());
                return userTeamService.save(userTeam);
             }
            }
        } catch (Exception e){
            log.error("joinTeam error", e);
            return false;
        }finally {
            if (lock.isHeldByCurrentThread()){
                //判断是当前线程获取的锁，保证只释放自己的锁
                log.info("unLock: " + Thread.currentThread().getId());
                lock.unlock();
            }
        }
    }

    /**
     * 用户退出队伍
     * @param teamQuitRequest  teamQuitRequest请求体
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean quitTeam(TeamQuitRequest teamQuitRequest) {
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long teamId = teamQuitRequest.getTeamId();
        if (teamId == null || teamId <= 0 ){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.SELECT_ERROR,"队伍不存在");
        }
        //是否已经加入队伍
        UserDTO user = UserHolder.getUser();
        Long userId = user.getId();
        LambdaQueryWrapper<UserTeam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(userId != null && userId >0,UserTeam::getUserId,userId);
        lambdaQueryWrapper.eq(UserTeam::getTeamId,teamId);
        long count = userTeamService.count(lambdaQueryWrapper);
        if (count == 0){
            throw new BusinessException(ErrorCode.NULL_ERROR,"未加入该队伍");
        }

        //退出队伍
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teamId",queryWrapper);
        //限制只取两条
        queryWrapper.last("order by id asc limit 2");
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        if (CollectionUtils.isEmpty(userTeamList)){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"队伍不存在");
        }
        if (userTeamList.size() == 1){
            //队伍只剩一人，解散队伍
           userTeamService.removeById(teamId);
            return removeById(teamId);
        }
        //判断是不是队长退出
        if (team.getUserId().equals(userId)){
            //是队长退出，将队伍权限转移给第二位最早加入队伍的用户（顺位）
            UserTeam userTeam = userTeamList.get(1);
            //更新队伍信息
            team.setUserId(userTeam.getUserId());
            team.setUpdateTime(new Date());
            updateById(team);
            //删除队长与队伍的关系
            return userTeamService.remove(lambdaQueryWrapper);

        }else {
            //不是队长退出。直接退出队伍
            boolean result = userTeamService.remove(lambdaQueryWrapper);
            if (!result){
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除用户队伍关系错误");
            }
            return result;
        }

    }

    /**
     * 队长删除队伍
     * @param teamId 队伍id
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteTeam(long teamId) {
        if (teamId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //队伍是否存在
        Team team = getById(teamId);
        if (team == null){
            throw new BusinessException(ErrorCode.SELECT_ERROR,"队伍不存在");
        }
        //只有队长能删除
        UserDTO user = UserHolder.getUser();
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if (!team.getUserId().equals(user.getId())){
            throw new BusinessException(ErrorCode.NO_AUTH,"不允许删除");
        }
        //移除队伍用户关联信息
        LambdaQueryWrapper<UserTeam> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserTeam::getTeamId,teamId);
        boolean result = userTeamService.remove(lambdaQueryWrapper);
        if (!result){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"移除队伍失败");
        }
        //删除队伍
        return removeById(teamId);
    }

    /**
     * 查询用户创建的队伍 TODO 可以优化
     * @param teamQuery
     * @return List<TeamUserVO>
     */
    @Override
    public List<TeamUserVO> getMyCreateTeam(TeamQuery teamQuery) {
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserDTO user = UserHolder.getUser();
        if (user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        teamQuery.setUserId(user.getId());
        List<TeamUserVO> teamList = listTeam(teamQuery, true);
        return teamList;
    }
}




