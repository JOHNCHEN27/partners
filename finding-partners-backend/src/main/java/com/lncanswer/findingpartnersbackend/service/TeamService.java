package com.lncanswer.findingpartnersbackend.service;

import com.lncanswer.findingpartnersbackend.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lncanswer.findingpartnersbackend.model.domain.dto.TeamQuery;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamJoinRequest;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamQuitRequest;
import com.lncanswer.findingpartnersbackend.model.domain.request.TeamUpdateRequest;
import com.lncanswer.findingpartnersbackend.model.domain.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author JohnChen
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2024-06-28 10:35:54
*/
public interface TeamService extends IService<Team> {

    /**
     * 添加队伍
     * @param team team
     * @return Long id
     */
    Long addTeam(Team team);

    /**
     * 搜索队伍
     * @param teamQuery 请求参数
     * @return  List<TeamUserVO>
     */
    List<TeamUserVO> listTeam(TeamQuery teamQuery,boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest 请求参数封装类
     * @return boolean
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request);

    /**
     * 加入队伍
     * @param teamJoinRequest  teamJoinRequest请求体
     * @return boolean
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest);

    /**
     * 退出队伍
     * @param teamQuitRequest  teamQuitRequest请求体
     * @return boolean
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest);

    /**
     * 删除队伍
     * @param teamId 队伍id
     * @return boolean
     */
    boolean deleteTeam(long teamId);

    /**
     * 查询用户创建的队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> getMyCreateTeam(TeamQuery teamQuery);


}
