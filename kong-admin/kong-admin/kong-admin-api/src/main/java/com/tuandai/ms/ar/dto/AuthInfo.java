package com.tuandai.ms.ar.dto;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author Gus Jiang
 * @date 2018/5/30  17:27
 */
public class AuthInfo implements Serializable {
	@NotBlank(message = "用户ID不能为空")
	@Pattern(regexp = "[a-z0-9A-Z]{0,}", message = "账号只能包含数字|字母")
	private String userId;
	@NotBlank(message = "ServiceId没有配置")
	private String serviceId;

	public AuthInfo(String userId, String serviceId) {
		this.userId = userId;
		this.serviceId = serviceId;
	}

	public AuthInfo() {
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}


    @Override
    public String toString() {
        return "{" +
                "\"userId\":\"" + userId +
                "\",\"serviceId\":\"" + serviceId +
                "\"}";
    }
}
