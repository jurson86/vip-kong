package com.tuandai.ms.ar.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

/**
 *  绑定服务
 *
 * @author wanggang
 * @createTime 2018-08-22 9:50:00
 */
@Setter
@Getter
@NoArgsConstructor
public class BindServerReq {

    //@NotEmpty(message = "组不能为空")
    private String groupName;

    @NotEmpty(message = "绑定服务不能为空")
    private String server;

    private Long userId;
}
