package com.tuandai.ms.ar.exception;


import com.tuandai.ms.ar.dto.ResponseDTO;
import com.tuandai.ms.ar.model.enums.ResCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 异常处理器
 *
 * @author 王刚
 * @date 2018-08-21 10:39:00
 */
@RestControllerAdvice
public class TdExceptionHandler {

    private Logger logger = LoggerFactory.getLogger(TdExceptionHandler.class);

    @ExceptionHandler(BaseRuntimeException.class)
    public ResponseDTO handleBaseRuntimeException(BaseRuntimeException e) {
        logger.error(e.getMessage(), e);
        ResponseDTO response = ResponseDTO.error();
        if (e.getCode() == 0) {
            response.setCode(ResCodeEnum.FAILURE.getCode());
        } else {
            response.setCode(e.getCode());
        }
        response.setMessage(e.getMessage());
        return response;
    }


    @ExceptionHandler(Exception.class)
    public ResponseDTO handleException(Exception e) {
        logger.error(e.getMessage(), e);
        ResponseDTO response = ResponseDTO.error();
        response.setMessage("系统异常，请稍后重试");
        return response;
    }
}
