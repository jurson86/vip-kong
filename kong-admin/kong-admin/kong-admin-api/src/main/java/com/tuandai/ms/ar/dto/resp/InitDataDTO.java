package com.tuandai.ms.ar.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * TODO 类的描述
 *
 * @author wanggang
 * @createTime 2018-08-21 16:40:00
 */
@Setter
@Getter
@NoArgsConstructor
public class InitDataDTO {

    private List<KongGroupDTO> groupList;

    private String bindUrl;

    private List<String> serverList;
}
