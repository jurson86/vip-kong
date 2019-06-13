package com.tuandai.ms.ar.service.inf;

import com.tuandai.ms.ar.dto.req.AddUserReq;
import com.tuandai.ms.ar.dto.req.QueryUserReq;
import com.tuandai.ms.ar.dto.req.UpdatePasswordReq;
import com.tuandai.ms.ar.dto.resp.KongUserDTO;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserToken;
import com.tuandai.pagehelper.util.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * shiro相关接口
 *
 *
 * @date 2017-06-06 8:49
 */
public interface KongUserService {

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    KongUser queryByUserId(Long userId);

    /**
     * 根据用户ID，查询用户
     * @param userName
     */
    KongUser queryByUserName(String userName);

    Page<KongUserDTO> selectGroupUserPageList(QueryUserReq req);

    /**
     * 增加用户
     * @param req
     */
    KongUser addUser(AddUserReq req, Map<String,String> groups);

    /**
     * 修改用户
     * @param req
     * @return
     */
    int updateUser(AddUserReq req);

    int updateUserStatus(Long userId, Integer status);

    /**
     * 更新密码
     * @param req
     * @return
     */
    int updatePassword(UpdatePasswordReq req);
}
