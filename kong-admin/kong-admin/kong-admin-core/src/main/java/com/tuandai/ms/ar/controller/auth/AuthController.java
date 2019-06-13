package com.tuandai.ms.ar.controller.auth;

import javax.validation.Valid;

import com.tuandai.ms.ar.api.AuthClient;
import com.tuandai.ms.ar.dto.AuthInfo;
import com.tuandai.ms.ar.dto.ResponseDTO;
import com.tuandai.ms.ar.model.TokenConstants;
import com.tuandai.ms.ar.utils.TokenGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;


import io.swagger.annotations.ApiOperation;

import java.util.concurrent.TimeUnit;


/**
 * @author Gus Jiang
 * @date 2018/5/30  14:18
 */
@RestController
public class AuthController implements AuthClient {

    @Autowired
    RedisTemplate redisTemplate;

    @Override
    @ResponseBody
    @RequestMapping(value=AuthClient.GENERATE_TOKEN, method = RequestMethod.POST)
    public ResponseDTO<String> generateToken(@Valid @RequestBody AuthInfo info) {
        ResponseDTO<String> resp = new ResponseDTO<String>();
        String token = TokenGenerator.generateValue();
        //写入redis
        redisTemplate.opsForValue().set(token, info.toString(), TokenConstants.TOKEN_EXPIER_DAY, TimeUnit.DAYS);
        resp.setData(token);
        return resp;
    }

    @Override
    @ResponseBody
    @RequestMapping(value=AuthClient.REMOVE_TOKEN, method = RequestMethod.POST)
    public ResponseDTO<String> removeToken(String token) {
        ResponseDTO<String> resp = new ResponseDTO<String>();
        redisTemplate.delete(token);
        resp.setData(null);
        return resp;
    }

}
