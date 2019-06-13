package com.tuandai.ms.ar.dao;

import com.tuandai.ms.ar.dto.req.BindServerReq;
import com.tuandai.ms.ar.model.user.KongUserGroup;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KongUserGroupMapper {

    int insert(KongUserGroup record);


    KongUserGroup selectByPrimaryKey(Long id);


    List<KongUserGroup> selectUserGroupList(Long userId);

    /**
     * 根据用户名和组名称查询
     * @param userId
     * @param groupName
     * @return
     */
    KongUserGroup selectByUserAndGroupName(@Param("userId") Long userId, @Param("groupName") String groupName);

    /**
     * 绑定服务
     * @param req
     * @return
     */
    int bindServer(BindServerReq req);

    /**
     * 批量插入
     * @param records
     * @return
     */
    int batchInsert(List<KongUserGroup> records);

    /**
     * 删除用户
     * @param userId
     * @return
     */
    int deleteGroupUser(@Param("userId") Long userId, @Param("groupName") String groupName);

    int deleteAllGroup(@Param("groupList") List<KongUserGroup> groupList, @Param("userId") Long userId);
}