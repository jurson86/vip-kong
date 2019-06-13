package com.tuandai.ms.ar.controller.kong;

import com.alibaba.fastjson.JSONObject;
import com.tuandai.ms.ar.service.inf.BindEurekaService;
import com.tuandai.ms.ar.service.inf.UpstreamAnalyzeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author Gus Jiang
 * @date 2018/5/23  17:46
 */
@RestController
@RequestMapping({ "/upstream" })
public class UpstreamController {

    private static final Logger logger = LoggerFactory.getLogger(UpstreamController.class);

    @Autowired
    UpstreamAnalyzeService upstreamAnalyzeService;

    @Autowired
    BindEurekaService bindEurekaService;


	@ResponseBody
    @RequestMapping({ "/list" })
	public JSONObject list() {
        JSONObject result = upstreamAnalyzeService.ListKongUpstreams("");
		return result;
	}

    @ResponseBody
    @RequestMapping({ "/get/targets" })
    public JSONObject getUpstream(@RequestParam String upstream) {
        JSONObject jo =  null;//upstreamAnalyzeService.GetKongUpstreamTargets(upstream);
        return jo;
    }



    @ResponseBody
    @RequestMapping({ "/eureka/list" })
    public JSONObject test(@RequestParam String upstream) {
        JSONObject jo =  null;//bindEurekaService.updateUpstreamTarget(upstream);
        return jo;
    }

}