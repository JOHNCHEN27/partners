package com.lncanswer.findingpartnersbackend.controller;

import com.lncanswer.findingpartnersbackend.common.BaseResponse;
import com.lncanswer.findingpartnersbackend.common.ErrorCode;
import com.lncanswer.findingpartnersbackend.common.ResultUtils;
import com.lncanswer.findingpartnersbackend.exception.BusinessException;
import com.lncanswer.findingpartnersbackend.model.domain.Team;
import com.lncanswer.findingpartnersbackend.model.domain.dto.TeamQuery;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamAddRequest;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamUpdateRequest;
import com.lncanswer.findingpartnersbackend.model.domain.vo.TeamUserVO;
import com.lncanswer.findingpartnersbackend.service.TeamService;
import com.lncanswer.findingpartnersbackend.service.UserService;
import com.lncanswer.findingpartnersbackend.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.builder.BuilderException;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody long teamId){
        if (teamId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean isSuccess = teamService.removeById(teamId);
        if (!isSuccess){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"删除队伍失败");
        }
        return ResultUtils.success(isSuccess);
    }

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

    @GetMapping("/query")
    public BaseResponse<Team> getTeamById(long teamId){
        if (teamId <=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(teamId);
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
    public BaseResponse<List<TeamUserVO>> listTeams(@RequestBody TeamQuery teamQuery, HttpServletRequest request){
        if (teamQuery == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //判断当前用户是否是管理员
        boolean isAdmin = userService.isAdmin(request);
        List<TeamUserVO> teamList = teamService.listTeam(teamQuery,isAdmin);
        return  ResultUtils.success(teamList);
    }

    @GetMapping("/list/page")
    public BaseResponse<List<Team>> listTeamsByPage(@RequestBody TeamQuery teamQuery){
        return  null;
    }
}
