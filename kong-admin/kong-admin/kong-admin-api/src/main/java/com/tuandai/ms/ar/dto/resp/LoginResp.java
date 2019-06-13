package com.tuandai.ms.ar.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 登录响应
 *
 * @author wanggang
 * @createTime 2018-08-21 14:33:00
 */
@Setter
@Getter
@NoArgsConstructor
public class LoginResp {

    private String token;

    private String username;

    private String realName;

    public LoginResp(String token,String username,String realName){
        this.token = token;
        this.username = username;
        this.realName = realName;
    }
}
