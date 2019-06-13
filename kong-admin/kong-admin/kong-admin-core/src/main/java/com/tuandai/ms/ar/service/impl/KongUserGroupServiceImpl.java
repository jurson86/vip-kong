package com.tuandai.ms.ar.service.impl;


import com.tuandai.ms.ar.dao.KongUserGroupMapper;
import com.tuandai.ms.ar.dao.KongUserMapper;
import com.tuandai.ms.ar.dto.req.AddGroupReq;
import com.tuandai.ms.ar.dto.req.BindServerReq;
import com.tuandai.ms.ar.dto.resp.KongGroupDTO;
import com.tuandai.ms.ar.dto.resp.KongServerDTO;
import com.tuandai.ms.ar.exception.BaseRuntimeException;
import com.tuandai.ms.ar.model.enums.ResCodeEnum;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserGroup;
import com.tuandai.ms.ar.service.inf.KongUserGroupService;
import com.tuandai.ms.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.*;

/**
 * kong 组业务
 *
 * @author wangg
 * @date 2017-06-06 8:49
 */
@Service
public class KongUserGroupServiceImpl implements KongUserGroupService {


    @Autowired
    KongUserGroupMapper kongUserGroupMapper;

    @Autowired
    KongUserMapper kongUserMapper;

    @Autowired
    KongGroupService kongServersService;

    @Override
    public List<KongUserGroup> selectUserGroupList(Long userId) {
        KongUser kongUser = kongUserMapper.selectByPrimaryKey(userId);
        List<KongUserGroup> groupList = new ArrayList<>();
        if(kongUser.getManagerFlag() == 1){
            Map<String, String> allGroups =  getAllroups();
            for(Map.Entry<String,String> group : allGroups.entrySet()){
                String groupName = group.getKey();
                KongUserGroup kongUserGroup = new KongUserGroup();
                kongUserGroup.setKongGroup(groupName);
                kongUserGroup.setUserId(userId);
                groupList.add(kongUserGroup);
            }
        }else{
            groupList = kongUserGroupMapper.selectUserGroupList(userId);
        }
        return groupList;
    }

    @Override
    public Map<String, String> getAllroups() {
//        Map<String,String> groupMap = new HashMap<>();
//        groupMap.put("testKong","10.100.14.8:8001");
//        groupMap.put("testKong2","10.100.14.68:8001");
        return kongServersService.groups();
    }


    @Override
    public List<KongServerDTO> getGroupServerList(String groupName) {
        Map<String, String> groupMap = getAllroups();
        List<KongServerDTO> list = new ArrayList<>();
        String servers = groupMap.get(groupName);
        if(!StringUtils.isEmpty(servers)){
            String [] serverArray = servers.split(",");
            for(String server : serverArray){
                list.add(new KongServerDTO(server));
            }
        }
        return list;
    }

    @Override
    @Transactional(rollbackFor = {Exception.class})
    public void switchShowGroup(KongUser user, String groupName) {
        //设置显示组
        kongUserMapper.setDefaultGroup(user.getUserId(),groupName);

        //管理员，判断组表中是否存在记录，不存在增加
        if(user.getManagerFlag() == 0){
            return;
        }
        KongUserGroup userGroup = kongUserGroupMapper.selectByUserAndGroupName(user.getUserId(),groupName);
        if(null == userGroup){
            //新增记录
            List<KongServerDTO> serverList = getGroupServerList(groupName);
            if(CollectionUtils.isEmpty(serverList)){
                throw new BaseRuntimeException(groupName + " 组不存在或没有 kong 服务");
            }
            userGroup = new KongUserGroup();
            userGroup.setUserId(user.getUserId());
            userGroup.setKongGroup(groupName);
            userGroup.setBingKongServer(serverList.get(0).getServer());
            kongUserGroupMapper.insert(userGroup);
        }
    }

    @Override
    public int bindServer(BindServerReq req) {
        return kongUserGroupMapper.bindServer(req);
    }

    @Override
    public KongUserGroup getByUserAndGroup(Long userId, String groupName) {
        return kongUserGroupMapper.selectByUserAndGroupName(userId,groupName);
    }

    @Override
    public void addGroup(AddGroupReq req) {
        KongUser kongUser = kongUserMapper.selectByUserName(req.getUsername());
        if(null == kongUser){
            throw new BaseRuntimeException(ResCodeEnum.USER_NOT_EXIST);
        }
        KongUserGroup kongUserGroup = getByUserAndGroup(kongUser.getUserId(), req.getGroupName());
        if(null != kongUserGroup){
            throw new BaseRuntimeException(ResCodeEnum.USER_EXIST_IN_GROUP);
        }
        KongUserGroup userGroup = new KongUserGroup();
        userGroup.setUserId(kongUser.getUserId());
        userGroup.setKongGroup(req.getGroupName());
        userGroup.setBingKongServer(req.getBindServer());
        kongUserGroupMapper.insert(userGroup);
    }

    @Override
    public Set<String> getKongList() {
        Map<String,String> groups = getAllroups();
        Set<String> serverSet = new HashSet<>();
        for(Map.Entry<String,String> group : groups.entrySet()){
            //String groupName = group.getKey();
            String servers = group.getValue();
            serverSet.addAll(Arrays.asList(servers.split(",")));
        }
        return serverSet;
    }

    @Override
    public int deleteUserFromGroup(Long userId, String groupName) {
        return kongUserGroupMapper.deleteGroupUser(userId, groupName);
    }

}
