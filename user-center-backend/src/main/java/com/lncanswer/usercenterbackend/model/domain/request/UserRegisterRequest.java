package com.lncanswer.usercenterbackend.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @author LNC
 * @version 1.0
 * @date 2024/4/25 15:56
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    private String userAccount;

    private String userPassword;

    private String checkPassword;
    private String planetCode;
}
