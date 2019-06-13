package com.tuandai.ms.ar.model.user;

import java.util.Date;

/**
 * 用户
 */
public class KongUser {
    private Long userId;

    private String username;

    private String realName;

    private String password;

    private String salt;

    private String email;

    private String mobile;

    private Integer status;

    private String defaultKongGroup;

    private Integer managerFlag;

    private Date createTime;

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt == null ? null : salt.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getManagerFlag() {
        return managerFlag;
    }

    public void setManagerFlag(Integer managerFlag) {
        this.managerFlag = managerFlag;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getDefaultKongGroup() {
        return defaultKongGroup;
    }

    public void setDefaultKongGroup(String defaultKongGroup) {
        this.defaultKongGroup = defaultKongGroup;
    }
}