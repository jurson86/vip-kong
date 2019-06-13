package com.tuandai.ms.ar.service.impl;


import com.tuandai.ms.ar.config.DataSourceConfig;
import com.tuandai.ms.ar.dao.KongUserTokenMapper;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserToken;
import com.tuandai.ms.ar.oauth2.ShiroTokenGenerator;
import com.tuandai.ms.ar.service.inf.KongUserTokenService;
import com.tuandai.ms.common.ds.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class KongUserTokenServiceImpl implements KongUserTokenService {

    //12小时后过期
    private final static int EXPIRE = 3600 * 12;

    @Autowired
    KongUserTokenMapper userTokenMapper;

    @Override
    @DataSource(DataSourceConfig.DATASOURCE_READ_KEY)
    public KongUserToken queryByToken(String token) {
        return userTokenMapper.queryByToken(token);
    }

    @Override
    public String updateToken(KongUser user) {
        KongUserToken userToken = userTokenMapper.selectByPrimaryKey(user.getUserId());
        //生成token
        String token = ShiroTokenGenerator.generateValue();

        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + EXPIRE * 1000);

        //判断是否存在记录，不存在新增，存在更新
        if(null == userToken){
            userToken = new KongUserToken();
            userToken.setUserId(user.getUserId());
            userToken.setUpdateTime(now);
            userToken.setExpireTime(expireTime);
            userToken.setToken(token);
            userTokenMapper.insert(userToken);
        }else{
            userToken.setUpdateTime(now);
            userToken.setExpireTime(expireTime);
            userToken.setToken(token);
            userTokenMapper.updateByPrimaryKey(userToken);
        }
        return token;
    }

}
