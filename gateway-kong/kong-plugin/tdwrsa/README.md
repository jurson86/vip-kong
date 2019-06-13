tdwrsa 用于rsa,dse3的加解密

一、luarocks安装

wget http://luarocks.github.io/luarocks/releases/luarocks-2.4.4.tar.gz

tar -zxvf luarocks-2.4.4.tar.gz

cd luarocks-2.4.4

./configure --prefix=/usr/local/luarocks

make build

make install

OK了,luarock的可执行文件被安装到了 /usr/local/luarocks/bin/luarocks.

二、插件安装


1.下载tdwrsa项目

2.执行 
cd tdwrsa
luarocks make


3.检查插件是否安装成功(该步骤可选)

luarocks list |grep tdwrsa

三、配置

登录kong管理控制台,插件参数配置如下：

{

	"name":"tdwrsa",
	"config":{
	"client_public_key":"",   --必填,客户端公钥
	"server_private_key":""   --必填,服务端私钥
	}
}


