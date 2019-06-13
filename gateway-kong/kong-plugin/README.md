kong 插件开发

tdwauth       用于安全认证

tdwsecurity   用于rsa,3dse 加解密

tdwrsa        用于rsa,3dse 加解密,效率比tdwsecurity更高效

注：
每个插件的handler.lua里定义有插件执行顺序，数字越大优先级越高，如：
plugin.PRIORITY=12


依赖库在线安装(先安装luarocks)

luarocks      install     luaossl                   --openssl加解密库

luarocks      install     lua-resty-http         --http库

luarocks      install     lua-cjson                 --json库(需先安装gcc)

依赖库离线安装(先安装luarocks)

luarocks      install     luaossl-20180708-0.src.rock                   --openssl加解密库(下载本地)

luarocks      install     lua-resty-http         --http库

luarocks      install     lua-cjson-2.1.0.6-1.src.rock                 --json库(需先安装gcc)(下载本地)


注： luaossl的git仓库

注：每个插件的handler.lua里定义有插件执行顺序，数字越大优先级越高，现在的优先级如下

tdwrsa 20

tdwauth 40

tdwdiy 60

tdwtrace 80

