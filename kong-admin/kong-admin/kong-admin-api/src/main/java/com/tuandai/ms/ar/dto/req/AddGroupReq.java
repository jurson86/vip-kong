package com.tuandai.ms.ar.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.NotNull;

/**
 * 给用户增加组请求
 *
 * @author wanggang
 * @createTime 2018-08-22 13:58:00
 */
@Setter
@Getter
@NoArgsConstructor
public class AddGroupReq {

    private String groupName;

    private String bindServer;

    @NotNull(message = "用户名不能为空")
    private String username;
}
