package com.tuandai.ms.ar;

import com.tuandai.ms.ar.config.DataSourceConfig;
import com.tuandai.ms.ar.config.DruidConfig;
import com.tuandai.ms.ar.config.SettingSupport;
import com.tuandai.ms.ar.service.inf.KongRegisterService;
import com.tuandai.ms.common.ds.MultipleDataSourceConfiguration;
import com.tuandai.ms.common.spring.BaseConfiguration;
import com.tuandai.ms.common.spring.ServiceClientConfiguration;
import com.tuandai.ms.common.spring.WebApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;

import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;


/**
 * 启动类
 * @author
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableEurekaClient
@EnableScheduling
@Import({BaseConfiguration.class,DruidConfig.class, DataSourceConfig.class, MultipleDataSourceConfiguration.class})
public class AdminApplication {

    public static void main(String[] args) {
        ApplicationContext  applicationContext = SpringApplication.run(AdminApplication.class, args);
        initail(applicationContext);
	}

    /**
     * 初始化基础配置数据
     * @param applicationContext
     */
	private static void initail(ApplicationContext applicationContext){
        RedisTemplate stringRedisTemplate = (RedisTemplate)applicationContext.getBean("redisTemplate");
        RedisConnectionFactory vv = stringRedisTemplate.getConnectionFactory();

    }
}
