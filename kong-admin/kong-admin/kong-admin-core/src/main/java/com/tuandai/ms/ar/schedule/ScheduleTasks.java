package com.tuandai.ms.ar.schedule;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tuandai.ms.ar.config.SettingSupport;
import com.tuandai.ms.ar.constants.KongAdminConstants;
import com.tuandai.ms.ar.service.inf.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * @author Gus Jiang
 * @date 2018/5/24  10:15
 */
@Component
public class ScheduleTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduleTasks.class);

    @Autowired
    UpstreamAnalyzeService upstreamAnalyzeService;

    @Autowired
    BindEurekaService bindEurekaService;

    @Autowired
    DistributedMaster distributedLock;

    @Autowired
    SettingSupport settingSupport;

    @Autowired
    KongRegisterService kongRegisterService;

    @Autowired
    KongUserGroupService kongUserGroupService;

    private final  String DISTRIBUTED_LOCK_KEY = "UPDATE_TARGET_FROM_EUREKA_LOCK";

    /**
     * 监控代理服务器的文件数量
     */
    @Scheduled(cron="0/30 * * * * ? ")
    public void updateTargetFromEurekaTask() {
        if(distributedLock.acquire(DISTRIBUTED_LOCK_KEY,settingSupport.getServiceIpPort())){
            updateTargetFromEureka();
        }
    }

    private void updateTargetFromEureka(){
        //获取所有 kong 服务器
        Set<String> servers = kongUserGroupService.getKongList();
        if(CollectionUtils.isEmpty(servers)){
           return;
        }
        for(String server : servers){
            logger.debug("开始同步服务器: {}",server);
            String kongUrl = KongAdminConstants.HTTP_PREFIX + server;
            JSONObject upstreams = upstreamAnalyzeService.ListKongUpstreams(kongUrl);
            if(null == upstreams || upstreams.getInteger("total") == 0){
                continue;
            }

            JSONArray jaUpstreams = upstreams.getJSONArray("data");
            for(int i = 0;i < jaUpstreams.size(); i++){
                String upstreamName = jaUpstreams.getJSONObject(i).getString("name");
                logger.debug("开始同步服务: {}", upstreamName);
                bindEurekaService.updateUpstreamTarget(kongUrl,upstreamName);
                logger.debug("结束同步服务: {}", upstreamName);
            }
            logger.debug("结束同步服务器: {}",server);
        }
    }

}
