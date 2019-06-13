package com.tuandai.ms.ar.service.base;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.ms.apiutils.Constants;
import com.tuandai.ms.ar.config.SettingSupport;
import com.tuandai.ms.ar.constants.KongAdminConstants;
import com.tuandai.ms.ar.constants.KongServiceUrls;
import com.tuandai.ms.ar.dto.resp.KongServerDTO;
import com.tuandai.ms.ar.service.inf.KongRegisterService;
import com.tuandai.ms.ar.service.inf.KongUserGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gus Jiang
 * @date 2018/6/7  9:58
 */
@Service
public class KongRegisterServiceImpl implements KongRegisterService {


    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SettingSupport settingSupport;

    @Autowired
    @Qualifier(Constants.RAW_REST_TEMPLATE)
    RestTemplate restTemplate;

    @Autowired
    KongUserGroupService groupService;

    @Override
    public String setCurrentKongUrl(String url) {
        settingSupport.setKongUrl(url);
        return settingSupport.getKongUrl();
    }

    @Override
    public String getCurentKongUrl() {
        return settingSupport.getKongUrl();
    }

    @Override
    public String addKongUrl(String url) {
        redisTemplate.opsForSet().add(KONG_URL_SET,url);
        Set<String> kongSet = redisTemplate.opsForSet().members(KONG_URL_SET);
        if(null != kongSet){
            return JSONObject.toJSONString(kongSet);
        }
        return "[]";
    }

    @Override
    public Set<String> getHistoryKongUrl() {
        Set<String> kongSet = redisTemplate.opsForSet().members(KONG_URL_SET);
        return kongSet;
    }

    @Override
    public void deleteKongUrl(String url) {
        redisTemplate.opsForSet().remove(KONG_URL_SET,url);
    }

    @Override
    public void deleteAllKongCache(String groupName) {
        List<KongServerDTO> serverDTOList = groupService.getGroupServerList(groupName);
        //Set<String> kongSet = redisTemplate.opsForSet().members(KONG_URL_SET);
        if(CollectionUtils.isEmpty(serverDTOList)){
            return;
        }
        for (KongServerDTO item: serverDTOList) {
            String Url = String.format(KongAdminConstants.HTTP_PREFIX + item.getServer() + KongServiceUrls.KONG_UPDATE_CACHE);
            restTemplate.delete(Url);
        }
    }

    @Override
    public void deleteKongCache(String Url) {
        String item = String.format(Url + KongServiceUrls.KONG_UPDATE_CACHE);
        restTemplate.delete(item);
    }

}
