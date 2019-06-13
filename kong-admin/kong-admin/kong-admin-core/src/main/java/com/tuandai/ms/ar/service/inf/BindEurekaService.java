package com.tuandai.ms.ar.service.inf;

import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;

/**
 * @author Gus Jiang
 * @date 2018/5/24  15:23
 */
public interface BindEurekaService {

    JSONObject updateUpstreamTarget(String kongUrl, String upstream);

}
