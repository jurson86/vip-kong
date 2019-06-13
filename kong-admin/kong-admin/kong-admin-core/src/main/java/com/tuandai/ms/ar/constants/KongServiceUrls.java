package com.tuandai.ms.ar.constants;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * @author Gus Jiang
 * @date 2018/5/23  18:04
 */
public class KongServiceUrls {

    /**
     * API
     */
    public static  final String  KONG_LIST_APIS="/apis";

    /**
     * UPSTREAM
     */
    public static  final String  KONG_LIST_UPSTREAMS="/upstreams";

    /**
     * UPSTREAM  TARGET
     */
    public static  final String  KONG_UPSTREAM_TARGET="/upstreams/%s/targets";

    /**
     * 强制更新nginx所有缓存
     */
    public static  final String  KONG_UPDATE_CACHE="/cache";


    /**
     *
     */
    public static  HashSet<String> HISTORY_URL_LIST = new HashSet<String>();

}
