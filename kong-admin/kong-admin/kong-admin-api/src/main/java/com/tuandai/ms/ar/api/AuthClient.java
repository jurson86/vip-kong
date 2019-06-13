package com.tuandai.ms.ar.api;

import com.tuandai.ms.ar.dto.AuthInfo;
import com.tuandai.ms.ar.dto.ResponseDTO;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;
import java.awt.*;

/**
 * @author Gus Jiang
 * @date 2018/5/30  17:27
 */
public interface AuthClient {

    /**
     * common api prefix
     */
    String GENERATE_TOKEN = "/token/generate";

    String REMOVE_TOKEN = "/token/remove";

    /**
     * 通过登录信息，生成token
     * @param info
     * @return
     */
    @RequestMapping(value = GENERATE_TOKEN , method = RequestMethod.POST)
    ResponseDTO<String> generateToken(@Valid @RequestBody AuthInfo info);

    /**
     * 登出清理token
     * @param token
     * @return
     */
    @RequestMapping(value = REMOVE_TOKEN , method = RequestMethod.POST)
    ResponseDTO<String> removeToken(@Valid @RequestBody String token);



}
