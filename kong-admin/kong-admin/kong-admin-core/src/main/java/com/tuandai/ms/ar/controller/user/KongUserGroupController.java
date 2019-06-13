package com.tuandai.ms.ar.controller.user;

import com.tuandai.ms.ar.controller.BaseController;
import com.tuandai.ms.ar.dto.ResponseDTO;
import com.tuandai.ms.ar.dto.req.AddGroupReq;
import com.tuandai.ms.ar.dto.req.BindServerReq;
import com.tuandai.ms.ar.dto.resp.KongGroupDTO;
import com.tuandai.ms.ar.dto.resp.UserGroupDTO;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserGroup;
import com.tuandai.ms.ar.service.inf.KongUserGroupService;
import com.tuandai.ms.ar.service.inf.KongUserService;
import com.tuandai.ms.ar.utils.ShiroUtils;
import com.tuandai.ms.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 用户组
 *
 * @author wanggang
 * @createTime 2018-08-21 15:16:00
 */
@RestController
@RequestMapping("/user/group")
public class KongUserGroupController extends BaseController{

    @Autowired
    KongUserGroupService kongUserGroupService;

    @Autowired
    KongUserService kongUserService;

    /**
     * 查询组列表
     * @return
     */
    @RequestMapping("list")
    public ResponseDTO<List<KongGroupDTO>> list(){
        KongUser user = ShiroUtils.getUserEntity();
        List<KongUserGroup> groupList = kongUserGroupService.selectUserGroupList(user.getUserId());
        List<KongGroupDTO> dtoList = new ArrayList<>();
        if(CollectionUtils.isEmpty(groupList)){
            return ResponseDTO.success(dtoList);
        }
        Map<String, String> allGroups = kongUserGroupService.getAllroups();
        for(KongUserGroup userGroup : groupList){
            KongGroupDTO dto = new KongGroupDTO();
            dto.setGroup(userGroup.getKongGroup());
            //非管理员，判断组是否存在
            if(user.getManagerFlag() != 1){
                String servers = allGroups.get(userGroup.getKongGroup());
                //不存在跳过
                if(StringUtils.isEmpty(servers)){
                    //kongUserGroupService.deleteUserFromGroup(user.getUserId(),userGroup.getKongGroup());
                    continue;
                }
            }
            dtoList.add(dto);
        }
        setDefaultGroup(dtoList,user);
        return ResponseDTO.success(dtoList);
    }

    private List<KongGroupDTO> setDefaultGroup(List<KongGroupDTO> groupList, KongUser user){
        if(CollectionUtils.isEmpty(groupList)){
            return groupList;
        }
        boolean hasDefault = false;
        for(KongGroupDTO dto : groupList){
            if(dto.getGroup().equals(user.getDefaultKongGroup())){
                hasDefault = true;
                dto.setDefaultFlag(1);
            }
        }
        //如果组被删除，修改组
        if(!hasDefault){
            KongGroupDTO defaultGroup = groupList.get(0);
            defaultGroup.setDefaultFlag(1);
            switchDefaultGroup(defaultGroup.getGroup());
        }
        return groupList;
    }

    /**
     * 删除组用户
     * @return
     */
    @RequestMapping("delete/{userId}")
    public ResponseDTO delete(@PathVariable("userId") Long userId){
        KongUser kongUser = kongUserService.queryByUserId(userId);
        if("admin".equals(kongUser.getUsername())){
            return ResponseDTO.error("非法操作");
        }
        kongUserGroupService.deleteUserFromGroup(userId,ShiroUtils.getUserEntity().getDefaultKongGroup());
        return ResponseDTO.success();
    }

    /**
     * 只返回当前登录用户中存在的组
     * @return
     */
    @RequestMapping("list/{userId}")
    public ResponseDTO<List<KongGroupDTO>> list(@PathVariable("userId") Long userId){
        KongUser user = ShiroUtils.getUserEntity();
        List<KongUserGroup> groupList = kongUserGroupService.selectUserGroupList(user.getUserId());
        List<KongUserGroup> groupList2 = kongUserGroupService.selectUserGroupList(userId);
        //只返回当前登录用户中存在的组
        List<KongGroupDTO> dtoList = new ArrayList<>();
        for(KongUserGroup group : groupList2){
            for(KongUserGroup group1 : groupList){
                if(group.getKongGroup().equals(group1.getKongGroup())){
                    KongGroupDTO dto = new KongGroupDTO();
                    dto.setGroup(group.getKongGroup());
                    dtoList.add(dto);
                }
            }
        }
        return ResponseDTO.success(dtoList);
    }

    /**
     * 切换组
     * @param groupName
     * @return
     */
    @RequestMapping("switch_group/{groupName}")
    public ResponseDTO switchDefaultGroup(@PathVariable("groupName") String groupName){
        KongUser user = ShiroUtils.getUserEntity();
        if(!user.getDefaultKongGroup().equals(groupName)){
            kongUserGroupService.switchShowGroup(ShiroUtils.getUserEntity(),groupName);
        }
        return ResponseDTO.success();
    }

    /**
     * 绑定服务
     * @param req
     * @param formValid
     * @return
     */
    @RequestMapping("bind_server")
    public ResponseDTO bindServer(@Validated @RequestBody BindServerReq req, BindingResult formValid){
        checkForm(formValid);
        req.setGroupName(ShiroUtils.getUserEntity().getDefaultKongGroup());
        req.setUserId(ShiroUtils.getUserEntity().getUserId());
        kongUserGroupService.bindServer(req);
        return ResponseDTO.success();
    }

    /**
     * 给用户增加组
     * @param req
     * @param formValid
     * @return
     */
    @RequestMapping("add")
    public ResponseDTO add(@Validated @RequestBody AddGroupReq req, BindingResult formValid){
        checkForm(formValid);
        req.setGroupName(ShiroUtils.getUserEntity().getDefaultKongGroup());
        kongUserGroupService.addGroup(req);
        return ResponseDTO.success();
    }

}
