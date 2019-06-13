package com.tuandai.ms.ar.model.user;

public class KongUserGroup {
    private Long id;

    private Long userId;

    private String kongGroup;

    private String bingKongServer;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getKongGroup() {
        return kongGroup;
    }

    public void setKongGroup(String kongGroup) {
        this.kongGroup = kongGroup == null ? null : kongGroup.trim();
    }

    public String getBingKongServer() {
        return bingKongServer;
    }

    public void setBingKongServer(String bingKongServer) {
        this.bingKongServer = bingKongServer == null ? null : bingKongServer.trim();
    }
}