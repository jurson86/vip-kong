package com.tuandai.ms.ar.controller.kong;

import com.tuandai.ms.ar.constants.KongAdminConstants;
import com.tuandai.ms.ar.dao.AppPluginRsaKey;
import com.tuandai.ms.ar.dto.ResponseDTO;
import com.tuandai.ms.ar.dto.req.BindServerReq;
import com.tuandai.ms.ar.dto.resp.KongServerDTO;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserGroup;
import com.tuandai.ms.ar.service.impl.KongGroupService;
import com.tuandai.ms.ar.service.inf.KongPluginGlobalCachService;
import com.tuandai.ms.ar.service.inf.KongRegisterService;
import com.tuandai.ms.ar.service.inf.KongUserGroupService;
import com.tuandai.ms.ar.service.inf.KongUserService;
import com.tuandai.ms.ar.utils.ShiroUtils;
import com.tuandai.ms.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Gus Jiang
 * @date 2018/5/23 17:46
 */
@RestController
@RequestMapping({ "/kong" })
public class KongController {

	@Autowired
	KongRegisterService kongRegisterService;

	@Autowired
	KongPluginGlobalCachService kongPluginGlobalCachService;

    @Autowired
    KongUserGroupService kongUserGroupService;

    @Autowired
    KongUserService kongUserService;

    @Autowired
	KongGroupService kongGroupService;

    /**
     * 获取当前设置的kong服务地址
     * @return
     */
    @RequestMapping(value="/url", method = RequestMethod.GET)
	public ResponseDTO<KongServerDTO> getUrl() {
        KongUser user = ShiroUtils.getUserEntity();
		KongUserGroup userGroup = kongUserGroupService.getByUserAndGroup(user.getUserId(),user.getDefaultKongGroup());
        String server = "";
		if(null != userGroup){
            server = userGroup.getBingKongServer();
        }
		//判断当前 server 是否存在
        List<KongServerDTO> serverList = kongUserGroupService.getGroupServerList(user.getDefaultKongGroup());
		if(CollectionUtils.isNotEmpty(serverList)){
			if(StringUtils.isEmpty(server) || !serverList.contains(new KongServerDTO(server))){
				server = serverList.get(0).getServer();
				BindServerReq req = new BindServerReq();
				req.setGroupName(user.getDefaultKongGroup());
				req.setUserId(user.getUserId());
				req.setServer(serverList.get(0).getServer());
				kongUserGroupService.bindServer(req);
			}
		}
		return ResponseDTO.success(new KongServerDTO(server));
	}

    /**
     * 获取历史设置过的kong服务列表
     * @return
     */
    @RequestMapping(value="/url/list", method = RequestMethod.GET)
    public ResponseDTO<List<KongServerDTO>> getUrlList() {
        KongUser user = ShiroUtils.getUserEntity();
        List<KongServerDTO> list = kongUserGroupService.getGroupServerList(user.getDefaultKongGroup());
        KongUserGroup kongUserGroup = kongUserGroupService.getByUserAndGroup(user.getUserId(),user.getDefaultKongGroup());
        //设置默认
        for(KongServerDTO kongServerDTO : list){
            if(kongServerDTO.getServer().equals(kongUserGroup.getBingKongServer())){
                kongServerDTO.setDefaultFlag(1);
            }
        }
        return ResponseDTO.success(list);
    }

	/**
	 * 清理kong服务列表内nginx所有缓存 此操作只有在服务部署后，未运行状态才可以执行，否则会导致缓存击穿
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/delete/caches", method = RequestMethod.DELETE)
	public ResponseDTO deleteCaches() {
		kongRegisterService.deleteAllKongCache(ShiroUtils.getUserEntity().getDefaultKongGroup());
		return ResponseDTO.success();
	}

	/**
	 * 清理指定kong服务nginx所有缓存 此操作只有在服务部署后，未运行状态才可以执行，否则会导致缓存击穿
	 * 
	 * @return
	 */
	@RequestMapping(value = "/delete/cache", method = RequestMethod.DELETE)
	public ResponseDTO deleteCache(@RequestParam String kongUrl) {
		kongRegisterService.deleteKongCache(KongAdminConstants.HTTP_PREFIX + kongUrl);
		return ResponseDTO.success();
	}

	/**
	 * 设置APP加解密RSA秘钥
	 * 
	 * @return
	 */
	@RequestMapping(value = "/rsa/key", method = RequestMethod.POST)
	public String setRsaKey(@Valid @RequestBody AppPluginRsaKey rsaKey) {
		String result = kongPluginGlobalCachService.setAppPluginRsaKey(rsaKey);
		return result;
	}

	/**
	 * 查询APP加解密RSA秘钥
	 * 
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/rsa/key", method = RequestMethod.GET)
	public String getRsaKey() {
		String result = kongPluginGlobalCachService.getAppPluginRsaKey();
		return result;
	}

}