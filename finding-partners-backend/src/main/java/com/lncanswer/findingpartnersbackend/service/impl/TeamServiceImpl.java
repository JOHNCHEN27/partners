package com.lncanswer.findingpartnersbackend.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lncanswer.findingpartnersbackend.common.ErrorCode;
import com.lncanswer.findingpartnersbackend.exception.BusinessException;
import com.lncanswer.findingpartnersbackend.model.domain.Team;
import com.lncanswer.findingpartnersbackend.model.domain.User;
import com.lncanswer.findingpartnersbackend.model.domain.UserTeam;
import com.lncanswer.findingpartnersbackend.model.domain.dto.TeamQuery;
import com.lncanswer.findingpartnersbackend.model.domain.dto.UserDTO;
import com.lncanswer.findingpartnersbackend.model.domain.enums.TeamStatusEnum;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamUpdateRequest;
import com.lncanswer.findingpartnersbackend.model.domain.vo.TeamUserVO;
import com.lncanswer.findingpartnersbackend.model.domain.vo.UserVO;
import com.lncanswer.findingpartnersbackend.service.TeamService;
import com.lncanswer.findingpartnersbackend.mapper.TeamMapper;
import com.lncanswer.findingpartnersbackend.service.UserService;
import com.lncanswer.findingpartnersbackend.service.UserTeamService;
import com.lncanswer.findingpartnersbackend.utils.UserHolder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.annotation.Resource;
import javax.print.attribute.standard.MediaSize;
import javax.servlet.http.HttpServletRequest;
import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
* @author JohnChen
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2024-06-28 10:35:54
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;

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
     * @return
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

        //根据状态判断 管理员可查询加密和非公开的房间
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValues(status);
        if (statusEnum == null){
            statusEnum = TeamStatusEnum.PUBLIC;
        }
        if (!isAdmin && !statusEnum.equals(TeamStatusEnum.PUBLIC)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        LambdaQueryWrapper<Team> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(status != null && status >-1,Team::getStatus,status);
        lambdaQueryWrapper.like(StringUtils.isNotBlank(teamName),Team::getName,teamName);
        lambdaQueryWrapper.eq(maxNum > 0,Team::getMaxNum,maxNum);
        lambdaQueryWrapper.eq(teamId != null && teamId > 0,Team::getId,teamId);
        lambdaQueryWrapper.like(StringUtils.isNotBlank(description),Team::getDescription,description);
        lambdaQueryWrapper.eq(userId != null && userId > 0, Team::getUserId,userId);

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
            if (StringUtils.isNotBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间必须设置密码");
            }
        }
        Team updateTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,updateTeam);
        boolean result = this.updateById(updateTeam);
        return result;
    }
}




