package com.tuandai.ms.ar.service.impl;

import com.tuandai.ms.ar.TestBase;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

/**
 * 组测试
 *
 * @author wanggang
 * @createTime 2018-08-24 9:49:00
 */
public class KongUserGroupServiceImplTest extends TestBase{

    @Autowired
    private KongUserGroupServiceImpl kongUserGroupService;

    @Test
    public void testGetKongList(){
        Set<String> serverList = kongUserGroupService.getKongList();
        printResult(serverList);
    }

}
