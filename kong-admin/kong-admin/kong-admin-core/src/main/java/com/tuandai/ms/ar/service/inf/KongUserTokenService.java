package com.tuandai.ms.ar.service.inf;

import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserToken;

/**
 * 用户 token 相关接口
 *
 *
 * @date 2017-06-06 8:49
 */
public interface KongUserTokenService {

    KongUserToken queryByToken(String token);

    /**
     * 更新用户 token
     * @param user
     * @return
     */
    String updateToken(KongUser user);

}
