package com.tuandai.ms.ar.service.impl;


import com.tuandai.ms.ar.dao.KongUserGroupMapper;
import com.tuandai.ms.ar.dao.KongUserMapper;
import com.tuandai.ms.ar.dto.req.AddUserReq;
import com.tuandai.ms.ar.dto.req.QueryUserReq;
import com.tuandai.ms.ar.dto.req.UpdatePasswordReq;
import com.tuandai.ms.ar.dto.resp.KongUserDTO;
import com.tuandai.ms.ar.exception.BaseRuntimeException;
import com.tuandai.ms.ar.model.enums.ResCodeEnum;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserGroup;
import com.tuandai.ms.ar.service.inf.KongUserGroupService;
import com.tuandai.ms.ar.service.inf.KongUserService;
import com.tuandai.pagehelper.util.Page;
import com.tuandai.pagehelper.util.PageHelper;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class KongUserServiceImpl implements KongUserService {

    @Autowired
    KongUserMapper userMapper;

    @Autowired
    KongUserGroupMapper kongUserGroupMapper;

    @Autowired
    KongUserGroupService kongUserGroupService;

    @Override
    public KongUser queryByUserId(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public KongUser queryByUserName(String userName) {
        return userMapper.selectByUserName(userName);
    }

    @Override
    public Page<KongUserDTO> selectGroupUserPageList(QueryUserReq req) {
        Page<KongUserDTO> page = PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<KongUserDTO> userList = userMapper.selectGroupUserPageList(req);
        page.setDataList(userList);
        return page;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public KongUser addUser(AddUserReq req,Map<String,String> groupMap) {
        KongUser existUser = userMapper.selectByUserName(req.getUsername());
        if(null != existUser){
            throw new BaseRuntimeException(ResCodeEnum.USER_EXIST.getMsg());
        }
        //新增用户
        String [] groups = req.getGroups();
        KongUser kongUser = new KongUser();
        kongUser.setDefaultKongGroup(groups[0]);
        kongUser.setCreateTime(new Date());
        kongUser.setUsername(req.getUsername());
        kongUser.setRealName(req.getRealName());
        kongUser.setStatus(req.getStatus());
        kongUser.setManagerFlag(0);
        kongUser.setEmail(req.getEmail());
        kongUser.setMobile(req.getMobile());
        //sha256加密
        String salt = RandomStringUtils.randomAlphanumeric(20);
        kongUser.setPassword(new Sha256Hash(req.getPassword(), salt).toHex());
        kongUser.setSalt(salt);
        userMapper.insert(kongUser);

        //新增用户组
        List<KongUserGroup> groupList = new ArrayList<>();
        for(String group : groups){
            KongUserGroup userGroup = new KongUserGroup();
            userGroup.setKongGroup(group);
            userGroup.setUserId(kongUser.getUserId());
            groupList.add(userGroup);
        }
        kongUserGroupMapper.batchInsert(groupList);
        return kongUser;
    }

    @Override
    public int updateUser(AddUserReq req) {
        //获取修改人所有的组
        List<KongUserGroup> updateUserGroups = kongUserGroupService.selectUserGroupList(req.getUpdateUser());
        kongUserGroupMapper.deleteAllGroup(updateUserGroups,req.getUserId());

        List<KongUserGroup> addGroupList = new ArrayList<>();
        String [] groups = req.getGroups();
        for(String group : groups){
            KongUserGroup userGroup = new KongUserGroup();
            userGroup.setUserId(req.getUserId());
            userGroup.setKongGroup(group);
            addGroupList.add(userGroup);
        }
        kongUserGroupMapper.batchInsert(addGroupList);

        //删除被修改人所有的组
        KongUser kongUser = new KongUser();
        kongUser.setUserId(req.getUserId());
        kongUser.setStatus(req.getStatus());
        kongUser.setRealName(req.getRealName());
        kongUser.setEmail(req.getEmail());
        kongUser.setMobile(req.getMobile());
        kongUser.setDefaultKongGroup(groups[0]);
        //sha256加密
        //KongUser preUser = userMapper.selectByPrimaryKey(req.getUserId());
        //kongUser.setPassword(new Sha256Hash(req.getPassword(), preUser.getSalt()).toHex());
        return userMapper.updateByPrimaryKeySelective(kongUser);
    }

    @Override
    public int updateUserStatus(Long userId, Integer status) {
        return userMapper.updateUserStatus(userId,status);
    }

    @Override
    public int updatePassword(UpdatePasswordReq req) {
        KongUser kongUser = userMapper.selectByPrimaryKey(req.getUserId());
        String oldPassword = new Sha256Hash(req.getPassword(), kongUser.getSalt()).toHex();
        if(!kongUser.getPassword().equals(oldPassword)){
            throw new BaseRuntimeException("原密码错误");
        }
        String newPassword = new Sha256Hash(req.getNewPassword(), kongUser.getSalt()).toHex();
        return userMapper.updatePassword(req.getUserId(),newPassword);
    }

}
