package com.tuandai.ms.ar.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

/**
 * 更新密码
 *
 * @author wanggang
 * @createTime 2018-08-23 17:22:00
 */
@Setter
@Getter
@NoArgsConstructor
public class UpdatePasswordReq {

    @NotEmpty(message = "原密码不能为空")
    private String password;

    @NotEmpty(message = "新密码不能为空")
    @Length(min = 3,message = "密码长度不能小于3位")
    private String newPassword;

    private Long userId;
}
