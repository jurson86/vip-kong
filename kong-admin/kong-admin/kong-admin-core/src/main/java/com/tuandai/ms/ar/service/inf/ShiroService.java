package com.tuandai.ms.ar.service.inf;

import com.tuandai.ms.ar.dto.req.AddUserReq;
import com.tuandai.ms.ar.dto.req.QueryUserReq;
import com.tuandai.ms.ar.dto.resp.KongUserDTO;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.pagehelper.util.Page;

import java.util.Map;

/**
 * shiro相关接口
 *
 *
 * @date 2017-06-06 8:49
 */
public interface ShiroService {

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    KongUser queryByUserId(Long userId);

}
