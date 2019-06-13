package com.tuandai.ms.ar.service.inf;

import com.tuandai.ms.ar.dto.req.AddGroupReq;
import com.tuandai.ms.ar.dto.req.BindServerReq;
import com.tuandai.ms.ar.dto.resp.KongServerDTO;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserGroup;

import java.util.*;

/**
 * kong 组业务接口
 *
 *
 * @date 2017-06-06 8:49
 */
public interface KongUserGroupService {

    List<KongUserGroup> selectUserGroupList(Long userId);

    /**
     * 查询所有的组
     *
     * @return
     */
    Map<String, String> getAllroups();

    /**
     * 获取组中服务器列表
     *
     * @param groupName
     * @return
     */
    List<KongServerDTO> getGroupServerList(String groupName);

    /**
     * 切换显示组
     *
     * @param user
     * @param groupName
     */
    void switchShowGroup(KongUser user, String groupName);

    /**
     * 绑定服务
     *
     * @param req
     * @return
     */
    int bindServer(BindServerReq req);

    /**
     * 根据用户和组查询记录
     *
     * @param userId
     * @param groupName
     * @return
     */
    KongUserGroup getByUserAndGroup(Long userId, String groupName);

    /**
     * 增加组
     *
     * @param req
     */
    void addGroup(AddGroupReq req);

    Set<String> getKongList();

    int deleteUserFromGroup(Long userId, String groupName);
}
