package com.tuandai.ms.ar.service.base;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.ms.ar.config.SettingSupport;
import com.tuandai.ms.ar.constants.KongAdminConstants;
import com.tuandai.ms.ar.constants.KongServiceUrls;
import com.tuandai.ms.ar.service.inf.UpstreamAnalyzeService;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

/**
 * @author Gus Jiang
 * @date 2018/5/24 10:13
 */
@Service
public class UpstreamAnalyzeServiceImpl implements UpstreamAnalyzeService {

	@Autowired
	SettingSupport settingSupport;

	@Autowired
	@Qualifier("rawRestTemplate")
	RestTemplate restTemplate;

	@Override
	public JSONObject ListKongUpstreams(String kongUrl) {
//		if (StringUtils.isBlank(settingSupport.getKongUrl())) {
//			return null;
//		}
		String Url = String.format(kongUrl + KongServiceUrls.KONG_LIST_UPSTREAMS);
		JSONObject result = restTemplate.getForObject(Url, JSONObject.class);
		if (null == result) {
			return null;
		}
		return result;

	}

	@Override
	public JSONObject AddKongUpstreamTarget(String kongUrl,String upstream, String target, String weight) {
		String Url = String.format(kongUrl + KongServiceUrls.KONG_UPSTREAM_TARGET, upstream);
		MultiValueMap<String, String> requestEntity = new LinkedMultiValueMap<>();
		requestEntity.add("target", target);
		requestEntity.add("weight", weight);

		JSONObject result = restTemplate.postForObject(Url, requestEntity, JSONObject.class);

		if (null == result) {
			return null;
		}
		return result;
	}

	@Override
	public void DeleteKongUpstreamTarget(String kongUrl,String upstream, String targetId) {
		String Url = String.format(kongUrl + KongServiceUrls.KONG_UPSTREAM_TARGET, upstream) + "/"
				+ targetId;
		restTemplate.delete(Url);
	}

	@Override
	public JSONObject GetKongUpstreamTargets(String kongUrl, String upstream) {
		String Url = String.format(kongUrl + KongServiceUrls.KONG_UPSTREAM_TARGET, upstream);
		JSONObject result = restTemplate.getForObject(Url, JSONObject.class);
		if (null == result) {
			return null;
		}
		return result;
	}

}
