## PostgreSQL 9.6 安装

### 条件

     服务器操作系统：  centos 7  64位
     postgresql 版本号:  9.6
     安装RPM包：postgresql-9.6.8-1-linux-x64-binaries.tar.gz
     下载地址：
     https://www.enterprisedb.com/download-postgresql-binaries

### 安装步骤

   1、创建Postgres用户：  
      useradd postgres

  2、切换到postgres用户:
      su - postgres

  3、在/data/opt/postgres目录下建立postgres的数据目录
     mkdir  data   log  
     
 4、解压tar.gz
     tar -zxvf   postgresql-9.6.8-1-linux-x64-binaries.tar.gz
     
5、配置profile   
     vim ~/.bash_profile  
     添加如下内容：  
    export PGHOME=/data/opt/postgres/pgsql  
    export PGDATA=/data/opt/postgres/data  
    export PATH=$PATH:$PGHOME/bin  
    
    重新加载profile  
    source    ~/.bash_profile  
    
6、初始化   
    initdb -D  /data/opt/postgres/data
     
7、更改配置  
    vi   /data/opt/postgres/data/postgresql.conf  
    调整监听主机地址：  
    listen_addresses = '*'           #   IP 地址， * 表示所有地址  
    
    vi   /data/opt/postgres/data/pg_hba.conf  
    #表示允许任意网段的任意机器通过密码验证的方式访问到该数据库
    host    all             all             0.0.0.0/0                   md5
    
8、启动数据库  
   pg_ctl  -D  /data/opt/postgres/data/ -l /data/opt/postgres/log/running.log  start
   

9、更改密码  
    $su - postgres  
   $psql  
   > ALTER USER postgres WITH PASSWORD 'postgres';  
   > select * from pg_shadow ;  
   > \q
   
主备    构建：  [https://my.oschina.net/Suregogo/blog/551626](https://my.oschina.net/Suregogo/blog/551626)
高可用构建： [https://my.oschina.net/Suregogo/blog/552765](https://my.oschina.net/Suregogo/blog/552765)
   
## Kong 安装

### 条件  
    安装包：  kong-community-edition-0.13.1.el7.noarch.rpm
    下载地址：  https://bintray.com/kong/kong-community-edition-rpm/centos/0.13.1#files/centos%2F7


### 安装步骤  

    $ sudo yum install epel-release
    $ sudo yum install  kong-community-edition-0.13.1.el7.noarch.rpm --nogpgcheck


### PostgreSQL 创建用户
    创建用户：  
    CREATE USER kong;    
    创建库：  
    CREATE DATABASE kong OWNER kong;
    更改密码 ：  
   $su - postgres  
   $psql  
   > ALTER USER kong WITH PASSWORD 'kong';  
   > select * from pg_shadow ;  
   > \q


### 更改Kong配置  
<pre>
 vi /etc/kong/kong.conf 
 --------------------------------
database = postgres             # Determines which of PostgreSQL or Cassandra
                                 # this node will use as its datastore.
                                 # Accepted values are `postgres` and
                                 # `cassandra`.

pg_host = 127.0.0.1             # The PostgreSQL host to connect to.
pg_port = 5432                  # The port to connect to.
pg_user = kong                  # The username to authenticate if required.
pg_password = kong              # The password to authenticate if required.
pg_database = kong              # The database name to connect to.

# admin 监听地址改一下，监听所有ip
admin_listen = 0.0.0.0:8001

</pre>


### 初始化数据库
<pre>
Kong migrations 运行 

$ kong migrations up [-c /etc/kong/kong.conf]

</pre>

### Kong 启动  
<pre>
#  kong start [-c /etc/kong/kong.conf]
Kong started

</pre>











