package com.tuandai.ms.ar.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO 类的描述
 *
 * @author wanggang
 * @createTime 2018-08-22 10:51:00
 */
@Setter
@Getter
@NoArgsConstructor
public class BaseReq {

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
