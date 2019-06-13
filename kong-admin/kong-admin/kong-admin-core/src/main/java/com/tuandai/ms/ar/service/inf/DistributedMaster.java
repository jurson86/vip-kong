package com.tuandai.ms.ar.service.inf;

import java.util.concurrent.ConcurrentHashMap;

public interface DistributedMaster {

    /**
     * 获取Master
     * @author Gus Jiang
     * @date 2018/5/24  15:23
     * @return
     * @throws InterruptedException
     */
    public boolean acquire(String lockKey,String hostId);

    /**
     * 查询此应用使用过的分布式锁，及锁状态
     * @return
     */
    public ConcurrentHashMap<String,Boolean> getLockKeyState();
}