package com.tuandai.ms.ar.model.enums;

import lombok.Getter;

/**
 * 返回码定义
 *
 * @author yangjiajia
 * @create 2018-03-05 14:56
 */
@Getter
public enum ResCodeEnum {

    SUCCESS(0, "success"),
    FAILURE(-1, "failure"),
    PARAM_ERROR(-2,"参数错误"),

    USER_LOGIN_ERROR(101, "用户名或密码错误"),
    USER_LOGIN_CAPTCHA_ERROR(102,"验证码错误"),
    USER_STATUS_INVALID(103,"账号已被禁用,请联系管理员"),
    USER_EXIST(104,"用户名称重复"),
    USER_NOT_EXIST(105,"用户不存在"),
    USER_EXIST_IN_GROUP(106,"用户已经在该组中"),
    ;

    private final Integer code;

    private final String msg;


    ResCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

}
