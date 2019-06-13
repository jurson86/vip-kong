#!/usr/bin/env python
#-*- coding:utf8 -*-
##########################


一、关于lua安装及luarocks安装
	a) 安装lua
	     安装插件之前请先检查lua是否安装
		 > lua -v
		 如果出现lua的相关版本信息,则表示已经安装，不需要再安装lua，否则需要安装lua

		 安装如下：
	         在安装插件之前请先安装lua-5.3.5的软件
		       '''
			      yum -y install readline-dev readline-devel
			      cd /data/source/gateway-kong/安装包
			      tar zxvf lua-5.3.5.tar.gz 
			      cd lua-5.3.5
			      make linux test
			      make install
			   '''
			

二、关于Kong的软件的安装
	a) 关于安装
	    https://bintray.com/kong/kong-community-edition-rpm/centos/0.13.1#files/centos/7
		下载: kong-community-edition-0.13.1.el7.noarch.rpm
		因为已经下载了，所以，只需要在安装包中下载即可.
		
	b) 安装
       yum localinstall kong-community-edition-0.13.1.el7.noarch.rpm
   
       
	c) 追加配置文件
	   cp /etc/kong/kong.conf.default /etc/kong/kong.conf
       vim /etc/kong/kong.conf,追加如下配置:
          database = postgres
		  pg_host = 192.168.16.115   #注意连接主库
		  pg_port = 5432
		  pg_user = kong
		  pg_password = kong
		  pg_database = kong
		  admin_listen = 0.0.0.0:8001	   
		  
	
	d) 初始化数据库
	   kong migrations up  -c /etc/kong/kong.conf  
	   
	e) 启动
       kong start -c /etc/kong/kong.conf

三 安装luarocks		
	a) 安装luarocks
		 wget http://luarocks.github.io/luarocks/releases/luarocks-2.4.4.tar.gz
		 tar -zxvf luarocks-2.4.4.tar.gz
		 cd  luarocks-2.4.4
		 ./configure --with-lua-include=/usr/local/openresty/luajit/include/luajit-2.1
		 make build
		 make install
		 
    检测安装是否成功:
        cd /root 
        luarocks  (显来有相关命令信息就成功)
		
	    否则就需要：==> 配置luarocks环境变量
	   
四、安装插件 kong-plugin
     注：安装包下载地址 http://git.tuandai888.com/kong/kong-tools 安装包

		a) 安装 tdwauth
		   1.下载tdwauth插件:kong-plugin-tdwauth-1.0-1.all.rock
		   2.执行 luarocks install kong-plugin-tdwauth-1.0-1.all.rock
		   #检测命令
		   3.luarocks list |grep tdwauth
		   4.执行 sh install.sh /etc/kong/kong.conf tdwauth
		   
		b) 安装 tdwrsa
		   1.下载tdwrsa插件:kong-plugin-tdwrsa-1.0-1.all.rock
		   2.执行luarocks install kong-plugin-tdwrsa-1.0-1.all.rock
		   #检测命令
		   3.luarocks list |grep tdwrsa
		   4.执行 sh install.sh /etc/kong/kong.conf tdwrsa

		c) 安装 tdwdiy
		   1.下载tdwdiy: kong-plugin-tdwdiy-1.0-1.all.rock
		   2.luarocks install kong-plugin-tdwdiy-1.0-1.all.rock
		   #检测命令
		   3.luarocks list |grep tdwdiy
		   4.执行 sh install.sh /etc/kong/kong.conf tdwdiy

       d) 安装 lua-cjson
		   1.下载tdwdiy:lua-cjson-2.1.0.6-1.src.rock
		   2.luarocks install lua-cjson-2.1.0.6-1.src.rock
		   #检测命令
		   3.luarocks list |grep lua-cjson

	   e)重启kong
		   kong restart
      	   检测是否启动成功: kong health
      	   
五、插件更新

对已经安装的插件进行更新，按如下步骤：

例如更新 tdwauth插件
1.下载tdwauth插件:kong-plugin-tdwauth-1.0-1.all.rock
2.执行 luarocks install kong-plugin-tdwauth-1.0-1.all.rock
#检测命令
3.luarocks list |grep tdwauth
4.kong restart     (更新插件需要重启)