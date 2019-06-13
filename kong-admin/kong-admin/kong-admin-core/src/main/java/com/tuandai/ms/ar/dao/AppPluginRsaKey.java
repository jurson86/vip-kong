package com.tuandai.ms.ar.dao;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

/**
 * @author Gus Jiang
 * @date 2018/5/30  17:27
 */
public class AppPluginRsaKey implements Serializable {

    private static  final  String SAFE_CHAR = "*";

	@NotBlank(message = "服务端私钥不能为空")
	private String priServerKey ;

    private String pubServerKey ;

    private String priClientKey ;

	@NotBlank(message = "客户端公钥不能为空")
	private String pubClientKey ;

	public AppPluginRsaKey(String priServerKey, String pubServerKey, String priClientKey, String pubClientKey) {
		this.priServerKey = priServerKey;
        this.pubServerKey = pubServerKey;
        this.priClientKey = priClientKey;
        this.pubClientKey = pubClientKey;
	}

	public AppPluginRsaKey() {
	}

    public String getPriServerKey() {
        return priServerKey;
    }

    public String getPubServerKey() {
        return pubServerKey;
    }

    public String getPriClientKey() {
        return priClientKey;
    }

    public String getPubClientKey() {
        return pubClientKey;
    }

    public void setPriServerKey(String priServerKey) {
        this.priServerKey = priServerKey;
    }

    public void setPubServerKey(String pubServerKey) {
        this.pubServerKey = pubServerKey;
    }

    public void setPriClientKey(String priClientKey) {
        this.priClientKey = priClientKey;
    }

    public void setPubClientKey(String pubClientKey) {
        this.pubClientKey = pubClientKey;
    }

    @Override
    public String toString() {
        return "{" +
                "\"pri_server_key\":\"" + priServerKey +
                "\",\"pub_server_key\":\"" + pubServerKey +
                "\",\"pri_client_key\":\"" + priClientKey +
                "\",\"pub_client_key\":\"" + pubClientKey +
                "\"}";
    }

}
