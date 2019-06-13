package com.tuandai.ms.ar.config;

import com.tuandai.ms.ar.service.base.DistributedMasterImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * @author Gus Jiang
 * @date 2018/5/23  17:46
 */
@Component
public class SettingSupport {
    private static Logger logger = LoggerFactory.getLogger(SettingSupport.class);

    public String getKongUrl() {
        return kongUrl;
    }

    public void setKongUrl(String kongUrl) {
        this.kongUrl = kongUrl;
    }

    public String getServiceIpPort(){
        try {
           String host = InetAddress.getLocalHost().getHostAddress();
           return host + ":" + port;
        } catch (UnknownHostException e) {
            logger.error("get server host Exception e:", e);
        }
        return null;
    }



    /**
     * 当前 PORT
     */
    @Value("${server.port:8080}")
    private String port;

    /**
     * 当前kong 服务地址
     */
   // @Value("${kong.url:http://kong.service:8001}")
    private String kongUrl;



}
