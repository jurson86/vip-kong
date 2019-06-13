package com.tuandai.ms.ar.dao;

import com.tuandai.ms.ar.model.user.KongUserToken;

public interface KongUserTokenMapper {
    int deleteByPrimaryKey(Long userId);

    int insert(KongUserToken record);

    int insertSelective(KongUserToken record);

    KongUserToken selectByPrimaryKey(Long userId);

    int updateByPrimaryKeySelective(KongUserToken record);

    int updateByPrimaryKey(KongUserToken record);

    KongUserToken queryByToken(String token);
}