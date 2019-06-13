### Kong Admin 控制台

#### 文件说明
<pre>
design             ：  设计文档
kong-admin         ：  控制台后端服务；
kong-admin-web     ：  控制台前端源码；

</pre>

#### kong-admin-web 整合
<pre>
1、kong-admin-web 源码路径为 src ：
kong-admin-web
  --src

2、kong-admin-web源码变更后，编译出来文件，放入views文件夹：
kong-admin-web
  --views
    --static
    --index.html

3、kong-admin 将 kong-admin-web中views下文件复制到 resource/static下即可：
kong-admin
  --kong-admin-core
   --src
     --main
       --resources
         --static
           --static
           --index.html

</pre>











