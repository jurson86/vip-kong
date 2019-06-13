package com.tuandai.ms.ar.service.base;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.ms.ar.dao.AppPluginRsaKey;
import com.tuandai.ms.ar.service.inf.KongPluginGlobalCachService;
import com.tuandai.ms.ar.utils.Conceal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Gus Jiang
 * @date 2018/6/6  9:45
 */
@Service
public class KongPluginGlobalCachServiceImpl implements KongPluginGlobalCachService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public String setAppPluginRsaKey(AppPluginRsaKey rsaKey) {
        String keyStr = rsaKey.toString();
        redisTemplate.opsForValue().set(APP_PLUGIN_RSA_KEY, keyStr);
        return keyStr;
    }

    @Override
    public String getAppPluginRsaKey() {
        String keyStr = redisTemplate.opsForValue().get(APP_PLUGIN_RSA_KEY);
        AppPluginRsaKey rsaKey = JSONObject.parseObject(keyStr,AppPluginRsaKey.class);
        rsaKey.setPriServerKey(Conceal.toConceal(rsaKey.getPriServerKey()));
        rsaKey.setPubServerKey(Conceal.toConceal(rsaKey.getPubServerKey()));
        rsaKey.setPriClientKey(Conceal.toConceal(rsaKey.getPriClientKey()));
        rsaKey.setPubClientKey(Conceal.toConceal(rsaKey.getPubClientKey()));
        return rsaKey.toString();
    }

}
