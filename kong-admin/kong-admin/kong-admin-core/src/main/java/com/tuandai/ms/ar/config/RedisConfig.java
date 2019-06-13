package com.tuandai.ms.ar.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisSentinelConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Set;

/**
 *
 *
 *
  #########################【redis】##################################
 redis.kong.database=8
 redis.kong.password=admin
 redis.kong.sentinel.master=mymaster
 redis.kong.sentinel.nodes='10.100.11.248:26379,10.100.11.249:26379,10.100.11.250:26379'
 redis.kong.pool.maxTotal=200
 redis.kong.pool.maxIdle=200
 redis.kong.pool.maxWaitMillis=-1
 redis.kong.pool.minIdle=200
 redis.kong.lock.timeout=300000
 redis.kong.lock.tryTime=20
 redis.kong.lock.expireTime=300000
 *
 */
@Configuration
@RefreshScope
public class RedisConfig {

    @Value("${redis.kong.sentinel.master}")
    private String sentinelMasterName = null;

    @Value("${redis.kong.sentinel.nodes}")
    private String hostAndPorts = null;


    public String getSentinelMasterName() {
        return this.sentinelMasterName;
    }

    public String getHostAndPorts() {
        return this.hostAndPorts;
    }

    @Bean("kongRedisPool")
    @ConfigurationProperties(prefix = "redis.kong.pool")
    public JedisPoolConfig kongRedisPool() {
        return new JedisPoolConfig();
    }
    
    @Bean("sentinelConnectionFactory")
    @ConfigurationProperties("redis.kong")
    public JedisConnectionFactory sentinelConnectionFactory(
            @Qualifier("kongRedisPool") JedisPoolConfig poolConfig) {
        Set<String> sentinelHostAndPorts = StringUtils.commaDelimitedListToSet(this.getHostAndPorts());
        //通过哨兵获取master
        RedisSentinelConfiguration sc = new RedisSentinelConfiguration(this.getSentinelMasterName(),
                sentinelHostAndPorts);
        return new JedisConnectionFactory(sc, poolConfig);
    }


    @Bean("redisTemplate")
    public StringRedisTemplate redisTemplate(
            @Qualifier("sentinelConnectionFactory") RedisConnectionFactory connectionFactory) {
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }

//
//    public static StringRedisTemplate redisTemplateNew(String hostAndPorts,String masterName) {
//        Set<String> sentinelHostAndPorts = StringUtils.commaDelimitedListToSet(hostAndPorts);
//        //通过哨兵获取master
//        RedisSentinelConfiguration sc = new RedisSentinelConfiguration(masterName,sentinelHostAndPorts);
//
//        StringRedisTemplate redisTemplate = new StringRedisTemplate();
//        redisTemplate.setConnectionFactory(new JedisConnectionFactory(sc, new JedisPoolConfig()));
//
//        return redisTemplate;
//    }

}