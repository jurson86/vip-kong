package com.tuandai.ms.ar.dao;

import com.tuandai.ms.ar.dto.req.QueryUserReq;
import com.tuandai.ms.ar.dto.resp.KongUserDTO;
import com.tuandai.ms.ar.model.user.KongUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface KongUserMapper {

    int insert(KongUser record);

    KongUser selectByPrimaryKey(Long userId);

    KongUser selectByUserName(String userName);

    /**
     * 管理员 第一登录 设置默认的组和绑定服务器
     * @param userId
     * @param defaultGroup
     * @return
     */
    int setDefaultGroup(@Param("userId") Long userId, @Param("defaultGroup") String defaultGroup);

    /**
     * 分页查询组中用户
     * @param req
     * @return
     */
    List<KongUserDTO> selectGroupUserPageList(QueryUserReq req);

    /**
     * 修改用户状态
     * @param userId
     * @param status
     * @return
     */
    int updateUserStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 选择性更新
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(KongUser record);

    /**
     * 修改密码
     * @param userId
     * @param password
     * @return
     */
    int updatePassword(@Param("userId") Long userId, @Param("password") String password);

}