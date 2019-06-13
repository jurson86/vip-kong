package com.tuandai.ms.ar.dto.req;

import com.tuandai.ms.ar.validator.AddGroup;
import com.tuandai.ms.ar.validator.UpdateGroup;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * 增加用户请求
 *
 * @author wanggang
 * @createTime 2018-08-22 11:34:00
 */
@Setter
@Getter
@NoArgsConstructor
public class AddUserReq {

    @NotEmpty(message = "用户名不能为空",groups = {AddGroup.class})
    private String username;

    @NotEmpty(message = "真实姓名不能为空",groups = {AddGroup.class,UpdateGroup.class})
    private String realName;

    @NotNull(message = "用户编号不能为空",groups = {UpdateGroup.class})
    private Long userId;

    @NotEmpty(message = "组名不能为空",groups = {AddGroup.class, UpdateGroup.class})
    private String[] groups;

    @NotEmpty(message = "密码不能为空",groups = {AddGroup.class})
    @Length(min = 3,message = "密码长度不能小于3位", groups = {AddGroup.class})
    private String password;

    @NotNull(message = "状态不能为空",groups = {AddGroup.class, UpdateGroup.class})
    private Integer status;

    private String email;

    private String mobile;

    /**
     * 修改用户
     */
    private Long updateUser;

}
