package com.tuandai.ms.ar.service.inf;

import java.util.Set;

/**
 * @author Gus Jiang
 * @date 2018/6/7  9:53
 */
public interface KongRegisterService {

    String  KONG_URL_SET = "KONG_URL_SET";
    /**
     *
     * @param url
     * @return
     */
    String setCurrentKongUrl(String url);

    String getCurentKongUrl();

    String addKongUrl(String url);

    Set<String> getHistoryKongUrl();

    void deleteKongUrl(String url);

    void deleteAllKongCache(String groupName);

    void deleteKongCache(String url);

}
