package com.tuandai.ms.ar.exception;

import com.tuandai.ms.ar.model.enums.ResCodeEnum;

/**
 * 自定义异常
 *
 * @author 胡俊飞
 * @date 2018-03-05  16:38:40
 */
public class BaseRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private int code;

    private String msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public BaseRuntimeException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public BaseRuntimeException(String msg, Exception e) {
        super(msg, e);
        this.msg = msg;
    }

    public BaseRuntimeException(int code, String msg) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public BaseRuntimeException(ResCodeEnum error) {
        super(error.getMsg());
        this.msg = error.getMsg();
        this.code = error.getCode();
    }

    public BaseRuntimeException(int code, String msg, Exception e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }

}
