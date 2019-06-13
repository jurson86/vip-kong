package com.tuandai.ms.ar.config;

import com.netflix.discovery.EurekaClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @author Gus Jiang
 * @date 2018/5/23  17:46
 */
@Component
public class ConfigBeans extends WebMvcConfigurerAdapter {
    @Bean
    protected RestTemplate restTemplate() {
        return new RestTemplate();
    }


    /**
     * 访问kong服务器请求设置跨域
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedOrigins("*").allowedMethods("*");
    }
}
