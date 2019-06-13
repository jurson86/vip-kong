package com.tuandai.ms.ar.dto.resp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * TODO 类的描述
 *
 * @author wanggang
 * @createTime 2018-08-21 17:05:00
 */
@Setter
@Getter
@NoArgsConstructor
public class KongServerDTO {

    private String server;

    private Integer defaultFlag;

    public KongServerDTO(String server){
        this.server = server;
    }

    @Override
    public boolean equals(Object object){
        KongServerDTO serverDTO = (KongServerDTO) object;
        return serverDTO.getServer().equals(server);
    }
}
