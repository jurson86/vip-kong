package com.tuandai.ms.ar.service.impl;


import com.tuandai.ms.ar.dao.KongUserMapper;
import com.tuandai.ms.ar.model.user.KongUser;
import com.tuandai.ms.ar.service.inf.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    KongUserMapper userMapper;

    @Override
    public KongUser queryByUserId(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

}
