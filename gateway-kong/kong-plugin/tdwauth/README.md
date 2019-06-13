tdwauth 用于token认证插件


一、luarocks安装

wget http://luarocks.github.io/luarocks/releases/luarocks-2.4.4.tar.gz

tar -zxvf luarocks-2.4.4.tar.gz

cd luarocks-2.4.4

./configure --prefix=/usr/local/luarocks

make build

make install

OK了,luarock的可执行文件被安装到了 /usr/local/luarocks/bin/luarocks.


二、插件安装

 1. 下载tdwauth项目
 
 2. 执行 luarocks make
 
 3.检查插件是否安装成功(该步骤可选)

   luarocks list |grep tdwauth
   
三、配置

  登录kong管理控制台,插件参数配置如下：

  
   {
   
	"name":"tdwauth",           --插件名称
	"config": {
		"redis":{
		  "host":"10.10.10.10",  --必填,redis地址
		  "port":6379,           --选填,redis端口
		  "dbindex":8,           --选填,redis数据库
		  "sentinel":true,       --必填,是否采用哨兵
		  "password":"123",      --必填,redis密码
		  "mastername":"master", --选填,redis的maste
		  "timeout":2000,        --选填,连接超时,单位毫秒
		  "tokenkey":"token",    --选填,客户端传输token使用的key
		}
	 }
   }