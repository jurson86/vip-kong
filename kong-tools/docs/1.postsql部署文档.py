#!/usr/bin/env python
#-*- coding:utf8 -*-
##########################

一、关于PostgreSQL的软件的安装
    
	环境的要求:
	     服务器操作系统：  centos 7  64位
	     postgresql 版本号:  9.6
	     安装RPM包：postgresql-9.6.9-1-linux-x64-binaries.tar.gz
		 下载地址：
	     https://www.enterprisedb.com/download-postgresql-binaries (国内打不开了)
		 
	
    安装步骤:
        1) 创建postgresql的用户:
           useradd postgres
		   
		2) 在/data/opt/postgres目录下建立postgres的数据目录
		   mkdir -p /data/opt/postgres/{data,log} && chown -R postgres.postgres /data/opt
		   	
		3) 切换到postgres用户:
		   su - postgres  
	    
		4) 解压
		   上传文件到 /home/postgres
		   tar -zxvf  postgresql-9.6.9-1-linux-x64-binaries.tar.gz
		   mv pgsql /data/opt/postgres/
		   
		5) 当前用户环境配置
           vim ~/.bash_profile 
		   添加如下内容：
			export PGHOME=/data/opt/postgres/pgsql
			export PGDATA=/data/opt/postgres/data
			export PATH=$PATH:$PGHOME/bin
		    
			重新加载: source ~/.bash_profile 
		
		6) 初始化
		   initdb -D  /data/opt/postgres/data
		   
		7) 调整postgresql的配置
           vi   /data/opt/postgres/data/postgresql.conf
				修改:
				listen_addresses = '*'     #   IP 地址， * 表示所有地址 		   
		   
		   vi   /data/opt/postgres/data/pg_hba.conf 
		        修改添加：
				#表示允许任意网段的任意机器通过密码验证的方式访问到该数据库
				host    all             all             0.0.0.0/0                   md5
		
		8) 启动数据库
		   pg_ctl  -D  /data/opt/postgres/data/ -l /data/opt/postgres/log/running.log  start
		   
		
		9) 更改密码
		    #su - postgres
            #psql
			    > ALTER USER postgres WITH PASSWORD 'postgres';
                > select * from pg_shadow ;
                > \q
           		
				>CREATE USER kong;
				>ALTER USER kong WITH PASSWORD 'kong';
				>CREATE DATABASE kong OWNER kong;
    
    master 与 slave 的配置
        master的配置：
		    1) vi   /data/opt/postgres/data/postgresql.conf
			     listen_addresses = '*'
			     wal_level = hot_standby
				 archive_mode = off
				 max_wal_senders = 3
				 wal_keep_segments = 16
           		   
		    2) vi   /data/opt/postgres/data/pg_hba.conf 
			   host    replication     rep             192.168.16.112/20        trust
		  
		    3) 新建用户
			   create user rep replication encrypted password 'rep';
    
	        4) 重启服务
			   pg_ctl  -D  /data/opt/postgres/data/ -l /data/opt/postgres/log/running.log stop 
			   pg_ctl  -D  /data/opt/postgres/data/ -l /data/opt/postgres/log/running.log start
	        
        slave的配置：
		    1) 切换用户
			   su - postgres
			   
		    2) 先进行清空 /data/opt/postgres/data
			    rm -rf /data/opt/postgres/data/*
				chmod 700 /data/opt/postgres/data 
				
			3) 进行同步到 
			    pg_basebackup -D $PGDATA -F p -X stream -v -P -h 192.168.16.115 -U rep
				
            4) vi   /data/opt/postgres/data/postgresql.conf
                 hot_standby  = on 
				 
            5) vi  /data/opt/postgres/data/recovery.conf
                  standby_mode = on
			      primary_conninfo = 'host=192.168.16.115 port=5432 user=rep'
				  trigger_file = '/data/opt/postgres/data/pg.trigger.file'	
				  
            6) 重启服务
			   pg_ctl  -D  /data/opt/postgres/data/ -l /data/opt/postgres/log/running.log stop 
			   pg_ctl  -D  /data/opt/postgres/data/ -l /data/opt/postgres/log/running.log start
              			