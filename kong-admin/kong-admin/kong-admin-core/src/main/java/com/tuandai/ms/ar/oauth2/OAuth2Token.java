package com.tuandai.ms.ar.oauth2;


import org.apache.shiro.authc.AuthenticationToken;

/**
 * token
 *
 *
 *
 * @date 2017-05-20 13:22
 */
public class OAuth2Token implements AuthenticationToken {
    /**  
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么).  
	 * @since JDK 1.8  
	 */
	private static final long serialVersionUID = 3028890468517062790L;
	private String token;

    public OAuth2Token(String token){
        this.token = token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
