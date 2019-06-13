package com.tuandai.ms.ar.service.base;

import com.tuandai.ms.ar.config.RedisConfig;
import com.tuandai.ms.ar.service.inf.DistributedMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @author Gus Jiang
 * @date 2018/5/24  10:13
 */
@Service
public class DistributedMasterImpl implements DistributedMaster {

    private static Logger logger = LoggerFactory.getLogger(DistributedMasterImpl.class);

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedisConfig redisConfig;


    /**
     * 分布式Master的键值
     */
    private ConcurrentHashMap<String,Boolean> lockKeyMap  =  new ConcurrentHashMap<String,Boolean>() ;

    /**
     * Master死亡超时，失效时间重新竞争
     */
    private int expireSeconds  = 60;


    @Override
    public ConcurrentHashMap<String,Boolean> getLockKeyState() {
        return lockKeyMap;
    }


    @Override
    public synchronized boolean acquire(String lockKey,String hostId) {
        try {

            //直接竞争获取到Master
            if (redisTemplate.opsForValue().setIfAbsent(lockKey, hostId)) {
                redisTemplate.expire(lockKey, expireSeconds, TimeUnit.SECONDS);
                // lock acquired
                lockKeyMap.put(lockKey,true);
                return true;
            }

            //redis里的时间
            String currentValueStr = redisTemplate.opsForValue().get(lockKey);
            if (currentValueStr != null && hostId.equals(currentValueStr)) {
                redisTemplate.expire(lockKey, expireSeconds, TimeUnit.SECONDS);
                lockKeyMap.put(lockKey,true);
                return true;
            }
        } catch (Exception e) {
            logger.error("acquire lock due to error",e);
        }
        return false;
    }


}