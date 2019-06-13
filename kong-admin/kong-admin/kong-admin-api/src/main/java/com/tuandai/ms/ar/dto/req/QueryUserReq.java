package com.tuandai.ms.ar.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 查询组用户列表参数
 *
 * @author wanggang
 * @createTime 2018-08-22 10:50:00
 */
@Setter
@Getter
@NoArgsConstructor
public class QueryUserReq extends BaseReq{

    private String username;

    private String realName;

    private String groupName;

    private Integer managerFlag;

}
