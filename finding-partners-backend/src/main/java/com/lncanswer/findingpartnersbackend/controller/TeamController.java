package com.lncanswer.findingpartnersbackend.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lncanswer.findingpartnersbackend.common.BaseResponse;
import com.lncanswer.findingpartnersbackend.common.ErrorCode;
import com.lncanswer.findingpartnersbackend.common.ResultUtils;
import com.lncanswer.findingpartnersbackend.exception.BusinessException;
import com.lncanswer.findingpartnersbackend.model.domain.Team;
import com.lncanswer.findingpartnersbackend.model.domain.User;
import com.lncanswer.findingpartnersbackend.model.domain.UserTeam;
import com.lncanswer.findingpartnersbackend.model.domain.dto.TeamQuery;
import com.lncanswer.findingpartnersbackend.model.domain.dto.UserDTO;
import com.lncanswer.findingpartnersbackend.model.domain.request.*;
import com.lncanswer.findingpartnersbackend.model.domain.vo.TeamUserVO;
import com.lncanswer.findingpartnersbackend.service.TeamService;
import com.lncanswer.findingpartnersbackend.service.UserService;
import com.lncanswer.findingpartnersbackend.service.UserTeamService;
import com.lncanswer.findingpartnersbackend.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.builder.BuilderException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/28 10:33
 */
@RestController
@Slf4j
@RequestMapping("/team")
public class TeamController {

    @Resource
    private UserService userService;
    @Resource
    private TeamService teamService;
    @Resource
    private UserTeamService userTeamService;

    /**
     * 创建队伍
     * @param teamAddRequest 请求参数封装类
     * @return teamId Long
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest){
        if (teamAddRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest,team);
        Long teamId = teamService.addTeam(team);
        return ResultUtils.success(teamId);
    }

    /**
     * 删除队伍
     * @param   teamDeleteRequest
     * @return boolean
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody TeamDeleteRequest teamDeleteRequest){
        if (teamDeleteRequest == null || teamDeleteRequest.getId() <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isSuccess = teamService.deleteTeam( teamDeleteRequest.getId());
        if (!isSuccess){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍失败");
        }
        return ResultUtils.success(isSuccess);
    }

    /**
     * 更新队伍
     * @param teamUpdateRequest  请求参数对象
     * @param request HttpRequest
     * @return Boolean
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if (teamUpdateRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isSuccess = teamService.updateTeam(teamUpdateRequest,request);
        if (!isSuccess){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"更新失败");
        }
        return ResultUtils.success(isSuccess);
    }

    /**
     * 查询用户创建的队伍
     * @param teamQuery teamQuery
     * @return BaseResponse<Team>
     */
    @GetMapping("/list/my/create")
    public BaseResponse<List<TeamUserVO>> getTeamById(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<TeamUserVO> teamList = teamService.getMyCreateTeam(teamQuery);
        if (teamList == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"查询队伍失败");
        }

        return isJoinTeamAndTeamNum(teamList);
    }

    /**
     * 查询用户加入的队伍
     * @param teamQuery
     * @return
     */
    @GetMapping("/list/my/join")
    public BaseResponse<List<TeamUserVO>> listMyJoinTeams(TeamQuery teamQuery){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserDTO user = UserHolder.getUser();
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", user.getId());
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        // 取出不重复的队伍 id
        // teamId userId
        // 1, 2
        // 1, 3
        // 2, 3
        // result
        // 1 => 2, 3
        // 2 => 3
        Map<Long, List<UserTeam>> listMap = userTeamList.stream()
                .collect(Collectors.groupingBy(UserTeam::getTeamId));
        List<Long> idList = new ArrayList<>(listMap.keySet());
        teamQuery.setIdList(idList);
        List<TeamUserVO> teamList = teamService.listTeam(teamQuery, true);
        return isJoinTeamAndTeamNum(teamList);

    }


    /**
     * 根据队伍id查询队伍
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id){
        if (id <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        if (team == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"查询队伍失败");
        }
        return ResultUtils.success(team);
    }

    /**
     * 搜索队伍信息列表
     * @param teamQuery 请求参数封装类
     * @param request httpRequest
     * @return List<TeamUserVO>
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeams(TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断当前用户是否是管理员
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeam(teamQuery,isAdmin);
        //判断当前用户是否已加入队伍
        return isJoinTeamAndTeamNum(teamList);
    }

    /**
     * todo 查询分页
     * @param teamQuery teamQuery请求体对象
     * @return Page<Team>
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamsByPage(@RequestBody TeamQuery teamQuery){
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery, team);
        Page<Team> page = new Page<>(teamQuery.getPageNum(), teamQuery.getPageSize());
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(team);
        Page<Team> resultPage = teamService.page(page, queryWrapper);
        return ResultUtils.success(resultPage);

    }

    /**
     * 用户加入队伍
     * @param teamJoinRequest 请求参数封装对象
     * @return Boolean
     */
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody TeamJoinRequest teamJoinRequest){
        if (teamJoinRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.joinTeam(teamJoinRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户退出队伍
     * @param teamQuitRequest 用户退出队伍请求体参数
     * @return Boolean
     */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody TeamQuitRequest teamQuitRequest){
        if (teamQuitRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = teamService.quitTeam(teamQuitRequest);
        return ResultUtils.success(result);
    }

    /**
     * 判断用户是否已经加入队伍 以及每个队伍的人数
     * @param teamList  List<TeamUserVO>
     * @return BaseResponse<List<TeamUserVO>>
     */
    private BaseResponse<List<TeamUserVO>> isJoinTeamAndTeamNum(List<TeamUserVO> teamList) {
        //判断用户是否加入队伍
        final List<Long> teamIdList = teamList.stream().map(TeamUserVO::getId).collect(Collectors.toList());
        if (teamList.isEmpty() || teamIdList.size() == 0){
            //直接返回
            return ResultUtils.success(new ArrayList<>());
        }
        QueryWrapper<UserTeam> userTeamQueryWrapper = new QueryWrapper<>();
        try {
            UserDTO user = UserHolder.getUser();
            userTeamQueryWrapper.eq("userId",user.getId());
            userTeamQueryWrapper.in("teamId",teamIdList);
            List<UserTeam> userTeamList = userTeamService.list(userTeamQueryWrapper);
            //已加入队伍id集合
            Set<Long> hasJoinTeamIdSet = userTeamList.stream().map(UserTeam::getTeamId).collect(Collectors.toSet());
            teamList.forEach(team -> {
                boolean haiJoin = hasJoinTeamIdSet.contains(team.getId());
                team.setHasJoin(haiJoin);
            });
        } catch (Exception e){
        }
        //查询加入队伍的用户信息（人数）
        QueryWrapper<UserTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("teamId",teamIdList);
        List<UserTeam> userTeamList = userTeamService.list(queryWrapper);
        //队伍 id => 加入这个队伍的用户列表
        Map<Long, List<UserTeam>> teamIdUserTeamList = userTeamList.stream().collect(Collectors.groupingBy(UserTeam::getTeamId));
        teamList.forEach(team -> team.setHasJoinNum(teamIdUserTeamList.getOrDefault(team.getId(),new ArrayList<>()).size()));
        return ResultUtils.success(teamList);
    }
}
