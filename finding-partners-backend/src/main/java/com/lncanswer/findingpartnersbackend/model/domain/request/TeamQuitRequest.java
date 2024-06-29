package com.lncanswer.findingpartnersbackend.model.domain.request;

import lombok.Data;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/6/29 10:04
 * 退出队伍请求体
 */
@Data
public class TeamQuitRequest {
    /**
     * 队伍id
     */
    private Long teamId;


}
