package com.tuandai.ms.ar.dto;

import com.tuandai.ms.ar.model.enums.ResCodeEnum;

import java.io.Serializable;

/**
 * @author Gus Jiang
 * @date 2018/5/30  17:27
 */
public class ResponseDTO<T> implements Serializable{

    /**
     * 返回编码
     */
    private int    code;
    /**
     * 返回编码
     */
    private String message;
    /**
     * 返回数据
     */
    private T data;


    public ResponseDTO() {
        this.code = 0;
        this.message = "";
        this.data = null;
    }

    public ResponseDTO(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public ResponseDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public ResponseDTO setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ResponseDTO<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public ResponseDTO<T> setData(T data) {
        this.data = data;
        return this;
    }

    public static ResponseDTO error(){
        return ResponseDTO.error(ResCodeEnum.FAILURE);
    }

    public static ResponseDTO error(String msg){
        return new ResponseDTO(ResCodeEnum.FAILURE.getCode(),msg);
    }

    public static <T> ResponseDTO<T> success(){
        return new ResponseDTO(ResCodeEnum.SUCCESS.getCode(),ResCodeEnum.SUCCESS.getMsg());
    }

    public static <T> ResponseDTO<T> success(T data){
        return new ResponseDTO(ResCodeEnum.SUCCESS.getCode(),ResCodeEnum.SUCCESS.getMsg(),data);
    }

    public static ResponseDTO error(ResCodeEnum error){
        return new ResponseDTO(error.getCode(),error.getMsg());
    }

    @Override
    public String toString() {
        return "ResponseDTO{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }

}
