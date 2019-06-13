package com.tuandai.ms.ar.service.inf;

import com.tuandai.ms.ar.dao.AppPluginRsaKey;

/**
 * @author Gus Jiang
 * @date 2018/6/5  18:01
 * 插件全局配置信息设置
 */
public interface KongPluginGlobalCachService {

    String APP_PLUGIN_RSA_KEY = "APP_PLUGIN_RSA_KEY";

    /**
     * 设置服务网关，RSA加解密插件公私钥，配置所有使用此插件的接口
     * @param rsaKey
     * @return
     */
    String setAppPluginRsaKey(AppPluginRsaKey rsaKey);

    /**
     * 查询服务网关，RSA加解密插件公私钥
     * @return
     */
    String getAppPluginRsaKey();

}
