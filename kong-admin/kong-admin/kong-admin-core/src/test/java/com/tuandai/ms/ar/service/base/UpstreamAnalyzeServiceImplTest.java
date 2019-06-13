package com.tuandai.ms.ar.service.base;

import static org.assertj.core.api.Assertions.assertThat;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.ms.ar.TestBase;
import com.tuandai.ms.ar.config.SettingSupport;
import com.tuandai.ms.ar.constants.KongAdminConstants;
import com.tuandai.ms.ar.constants.KongServiceUrls;
import com.tuandai.ms.ar.service.impl.KongGroupService;
import com.tuandai.ms.ar.service.impl.KongUserGroupServiceImpl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Set;

/**
 * @author Gus Jiang
 * @date 2018/5/24  10:33
 */
public class UpstreamAnalyzeServiceImplTest extends TestBase{

    private static final Logger logger = LoggerFactory.getLogger(UpstreamAnalyzeServiceImplTest.class);


    @Autowired
    private UpstreamAnalyzeServiceImpl upstreamAnalyzeService;

    @Autowired
    private KongUserGroupServiceImpl kongGroupService;
//
//
//    @Before
//    public void setUp() {
//        MockitoAnnotations.initMocks(this);
//    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getKongUpstreamTargets() throws Exception {

        RestTemplate restTemplate = new RestTemplate();
        SettingSupport settingSupport = new SettingSupport();
        settingSupport.setKongUrl("http://172.22.46.171:8001");

        String Url = String.format(settingSupport.getKongUrl() + KongServiceUrls.KONG_UPSTREAM_TARGET,"tcc.order");
        ResponseEntity<JSONObject> result = restTemplate.getForEntity(Url, JSONObject.class);
        logger.debug(result.getBody().toJSONString());

    }

    @Test
    public void testListKongUpstreams(){
        Set<String> kongList = kongGroupService.getKongList();
        String kongUrl = kongList.iterator().next();
        JSONObject jsonObject = upstreamAnalyzeService.ListKongUpstreams(KongAdminConstants.HTTP_PREFIX + kongUrl);
        printResult(jsonObject);
    }


}