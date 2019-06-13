package com.tuandai.ms.ar.service.inf;

import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Gus Jiang
 * @date 2018/5/24  9:23
 */
public interface UpstreamAnalyzeService {

    JSONObject ListKongUpstreams(String kongUrl);

    JSONObject AddKongUpstreamTarget(String kongUrl,String upstream,String target,String weight);

    void DeleteKongUpstreamTarget(String kongUrl,String upstream,String targetId);

    JSONObject GetKongUpstreamTargets(String kongUrl, String upstream);

}
