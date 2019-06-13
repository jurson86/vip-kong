package com.tuandai.ms.ar.dto.req;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotBlank;

/**
 * 登录请求参数
 *
 * @author wanggang
 * @createTime 2018-08-21 13:55:00
 */
@Setter
@Getter
public class LoginReq {

    @NotBlank(message = "用户名不能为空")
    private String username;

    @NotBlank(message = "密码不能为空")
    private String password;

    @NotBlank(message = "验证码不能为空")
    private String captcha;
}
