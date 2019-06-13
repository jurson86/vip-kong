package com.tuandai.ms.ar.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 用户所属组
 *
 * @author wanggang
 * @createTime 2018-08-24 17:04:00
 */
@Setter
@Getter
@NoArgsConstructor
public class UserGroupDTO {

    private List<KongGroupDTO> allGroup;

    private List<KongGroupDTO> userGroup;
}
