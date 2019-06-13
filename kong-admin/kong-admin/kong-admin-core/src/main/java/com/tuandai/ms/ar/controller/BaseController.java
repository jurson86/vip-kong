package com.tuandai.ms.ar.controller;

import com.tuandai.ms.ar.exception.BaseRuntimeException;
import org.springframework.validation.BindingResult;

/**
 * 控制器基类
 *
 * @author wanggang
 * @createTime 2018-08-21 14:06:00
 */
public abstract class BaseController {

    /**
     * 校验参数
     * @param formValid
     */
    public void checkForm(BindingResult formValid){
        if(null != formValid && formValid.hasErrors()){
            StringBuilder errors = new StringBuilder();
            formValid.getAllErrors().stream().forEach(error -> {
                errors.append(error.getDefaultMessage()).append("; ");
            });
            throw new BaseRuntimeException(errors.toString());
        }
    }

}
