package com.tuandai.ms.ar.controller.user;

import com.tuandai.ms.ar.controller.BaseController;
import com.tuandai.ms.ar.dto.ResponseDTO;
import com.tuandai.ms.ar.dto.req.AddUserReq;
import com.tuandai.ms.ar.dto.req.QueryUserReq;
import com.tuandai.ms.ar.dto.req.UpdatePasswordReq;
import com.tuandai.ms.ar.dto.resp.KongUserDTO;
import com.tuandai.ms.ar.model.enums.ResCodeEnum;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.model.user.KongUserGroup;
import com.tuandai.ms.ar.service.inf.KongUserGroupService;
import com.tuandai.ms.ar.service.inf.KongUserService;
import com.tuandai.ms.ar.service.inf.ShiroService;
import com.tuandai.ms.ar.utils.ShiroUtils;
import com.tuandai.ms.ar.validator.AddGroup;
import com.tuandai.ms.ar.validator.UpdateGroup;
import com.tuandai.pagehelper.util.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户组
 *
 * @author wanggang
 * @createTime 2018-08-21 15:16:00
 */
@RestController
@RequestMapping("/user")
public class KongUserController extends BaseController{

    @Autowired
    KongUserService kongUserService;

    @Autowired
    KongUserGroupService kongUserGroupService;

    /**
     * 查询组列表
     * @return
     */
    @RequestMapping("list")
    public ResponseDTO<Page<KongUserDTO>> list(@RequestBody QueryUserReq req){
        req.setManagerFlag(ShiroUtils.getUserEntity().getManagerFlag());
        req.setGroupName(ShiroUtils.getUserEntity().getDefaultKongGroup());
        Page<KongUserDTO> page = kongUserService.selectGroupUserPageList(req);
        return ResponseDTO.success(page);
    }

    /**
     * 查询组列表
     * @return
     */
    @RequestMapping("info/{userId}")
    public ResponseDTO<KongUserDTO> info(@PathVariable("userId") Long userId){
        KongUserGroup kongUserGroupongUserGroup = kongUserGroupService.getByUserAndGroup(userId,ShiroUtils.getUserEntity().getDefaultKongGroup());
        if(null == kongUserGroupongUserGroup){
            return ResponseDTO.error("用户不存在");
        }
        KongUser user = kongUserService.queryByUserId(userId);
        if(null == user){
            return ResponseDTO.error("用户不存在");
        }
        KongUserDTO kongUserDTO = new KongUserDTO();
        kongUserDTO.setCreateTime(user.getCreateTime());
        kongUserDTO.setEmail(user.getEmail());
        kongUserDTO.setMobile(user.getMobile());
        kongUserDTO.setStatus(user.getStatus());
        kongUserDTO.setUsername(user.getUsername());
        kongUserDTO.setRealName(user.getRealName());
        kongUserDTO.setUserId(user.getUserId());
        return ResponseDTO.success(kongUserDTO);
    }

    /**
     * 增加用户
     * @param req
     * @param formValid
     * @return
     */
    @RequestMapping("add")
    public ResponseDTO add(@Validated({AddGroup.class}) @RequestBody AddUserReq req, BindingResult formValid){
        checkForm(formValid);
        kongUserService.addUser(req,kongUserGroupService.getAllroups());
        return ResponseDTO.success();
    }

    /**
     * 修改用户
     * @param req
     * @param formValid
     * @return
     */
    @RequestMapping("update")
    public ResponseDTO update(@Validated({UpdateGroup.class}) @RequestBody AddUserReq req, BindingResult formValid){
        checkForm(formValid);
        req.setUpdateUser(ShiroUtils.getUserId());
        kongUserService.updateUser(req);
        return ResponseDTO.success();
    }

    /**
     * 修改用户
     * @param req
     * @param formValid
     * @return
     */
    @RequestMapping("update_password")
    public ResponseDTO updatePassword(@Validated() @RequestBody UpdatePasswordReq req, BindingResult formValid){
        checkForm(formValid);
        req.setUserId(ShiroUtils.getUserId());
        kongUserService.updatePassword(req);
        return ResponseDTO.success();
    }

    /**
     * 修改用户状态
     * 0：禁用   1：正常
     * @param userId
     * @param status
     * @return
     */
    @RequestMapping("update_status/{userId}/{status}")
    public ResponseDTO updateStatus(@PathVariable("userId") Long userId, @PathVariable("status") Integer status){
        if(status != 1 && status != 0){
            return ResponseDTO.error(ResCodeEnum.PARAM_ERROR);
        }
        //非管理员不能修改管理员
        KongUser kongUser = kongUserService.queryByUserId(userId);
        boolean isAdmin = kongUser.getUsername().equals("admin");
        boolean isUpdateManager = (kongUser.getManagerFlag() == 1 && ShiroUtils.getUserEntity().getManagerFlag() == 0);
        if(isUpdateManager || isAdmin){
            return ResponseDTO.error("非法操作");
        }
        kongUserService.updateUserStatus(userId,status);
        return ResponseDTO.success();
    }

}
