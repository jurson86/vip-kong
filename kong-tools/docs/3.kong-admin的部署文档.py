#!/usr/bin/env python
#-*- coding:utf8 -*-
##########################

一、关于Kong-admin的软件的安装
    
    1) 克隆git 
	    mkdir /data/source 
		git clone http://git.tuandai888.com/kong/kong-admin.git
		cd  /data/source/gateway-kong/kong-admin
		
		/usr/local/software/maven/bin/mvn clean package -Dmaven.test.skip=true
      	
    2) 部署
        cd  /data/source/gateway-kong/kong-admin/kong-admin-core/target
        cp kong-admin-core-1.0-SNAPSHOT.jar /usr/local/software/kong-admin-core/
        		