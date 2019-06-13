package com.tuandai.ms.ar.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.tuandai.ms.ar.constants.KongServiceUrls;
import com.tuandai.ms.ar.service.inf.BindEurekaService;
import com.tuandai.ms.ar.service.inf.KongRegisterService;
import com.tuandai.ms.ar.service.inf.UpstreamAnalyzeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Gus Jiang
 * @date 2018/5/24 15:26
 */
@Service
public class BindEurekaServiceImpl implements BindEurekaService {
	private static final Logger logger = LoggerFactory.getLogger(BindEurekaServiceImpl.class);

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	private EurekaClient discoveryClient;

	@Autowired
	private UpstreamAnalyzeService upstreamAnalyzeService;

	@Autowired
	KongRegisterService kongRegisterService;

	@Autowired
	RestTemplate restTemplate;

	@Override
	public JSONObject updateUpstreamTarget(String kongUrl,String upstream) {
		// eureka
		ArrayList<String> eurekaTargets = ListEurekaUpstreamIpPort(upstream);
		logger.debug("服务 {} eureka ip列表: {}",upstream, eurekaTargets);
//		Set<String> kongSet = kongRegisterService.getHistoryKongUrl();
//		if (null != kongSet && kongSet.size() > 0) {
//			for (String url : kongSet) {
//				update(eurekaTargets, url, upstream);
//			}
//		}
		update(eurekaTargets, kongUrl, upstream);
		JSONObject jsonUpstream = upstreamAnalyzeService.GetKongUpstreamTargets(kongUrl,upstream);
		return jsonUpstream;
	}

	/**
	 * @param targets upstream 服务器列表
	 * @param kongUrl kong 服务器
	 * @param upstream upstream 名称
	 */
	private void update(ArrayList<String> targets, String kongUrl, String upstream) {

//		JSONObject result = restTemplate.getForObject(
//				String.format(kongUrl + KongServiceUrls.KONG_UPSTREAM_TARGET, upstream), JSONObject.class);

		// kong 获取kong 中 upstream 的服务器 targets 列表
		JSONObject jsonUpstream = upstreamAnalyzeService.GetKongUpstreamTargets(kongUrl, upstream);

		if (null == targets) {
			logger.info("Kong {} : Service {} has no targets on eureka !", kongUrl, upstream);
			return;
		}
		ArrayList<String> eurekaTargets = new ArrayList<String>(targets);

		JSONArray jaTargets = jsonUpstream.getJSONArray("data");
		logger.debug("{} 服务器中已经存在的 target: {}",upstream,jaTargets);
		// analyze
		ArrayList<String> deleteTargets = new ArrayList<String>();
		for (int i = 0; i < jaTargets.size(); i++) {
			if (eurekaTargets.contains(jaTargets.getJSONObject(i).getString("target"))) {
				eurekaTargets.remove(jaTargets.getJSONObject(i).getString("target"));
			} else {
				deleteTargets.add(jaTargets.getJSONObject(i).getString("id"));
			}
		}
		logger.debug("{} 增加 target {}",upstream,eurekaTargets);
		// add
		if (eurekaTargets.size() > 0) {
			String defaultWeight = "100";
			Iterator it = eurekaTargets.iterator();
			while (it.hasNext()) {
				upstreamAnalyzeService.AddKongUpstreamTarget(kongUrl,upstream, it.next().toString(), defaultWeight);
			}
		}
		logger.debug("{} 删除 target {}",upstream,deleteTargets);
		// delete
		if (deleteTargets.size() > 0) {
			Iterator it = deleteTargets.iterator();
			while (it.hasNext()) {
				upstreamAnalyzeService.DeleteKongUpstreamTarget(kongUrl,upstream, it.next().toString());
			}
		}

	}

	/**
	 * 解析 eureka 响应信息
	 * 
	 * @param upstream
	 * @return
	 */
	private ArrayList<String> ListEurekaUpstreamIpPort(String upstream) {

		Applications applications = discoveryClient.getApplications();
		List<Application> appList = applications.getRegisteredApplications();
		if (!CollectionUtils.isEmpty(appList)) {
			for (Application application : appList) {
				if (upstream.toUpperCase().equals(application.getName().toUpperCase())) {
					ArrayList<String> aList = new ArrayList<String>();
					List<InstanceInfo> listIns = application.getInstances();
					for (int i = 0; i < listIns.size(); i++) {
						aList.add(listIns.get(i).getIPAddr() + ":" + listIns.get(i).getPort());
					}
					return aList;
				}
			}
		}
		return null;
	}
}
