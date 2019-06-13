## 接口文档

### admin api  

##### 端口号 PORTS :  

8001 默认的管理端监听端口  
8001 is the default port on which the Admin API listens.  

8444 默认的https 管理端监听端口  
8444 is the default port for HTTPS traffic to the Admin API.

##### 支撑请求类型  Supported Content Types  

1、 application/x-www-form-urlencoded  
.e.g
<pre>
config.limit=10&config.period=seconds
</pre>

2、 application/json  
.e.g  
<pre>
{
    "config": {
        "limit": 10,
        "period": "seconds"
    }
}
</pre>


### 接口列表
<p>
  注：    所有接口示例 均采用 json 格式演示
</p>


1、kong节点信息查询  
GET     http://127.0.0.1:8001/ 
<pre>
响应结果：
{
    "hostname": "",
    "node_id": "6a72192c-a3a1-4c8d-95c6-efabae9fb969",
    "lua_version": "LuaJIT 2.1.0-alpha",
    "plugins": {
        "available_on_server": [
            ...
        ],
        "enabled_in_cluster": [
            ...
        ]
    },
    "configuration" : {
        ...
    },
    "tagline": "Welcome to Kong",
    "version": "0.11.0"
}
</pre>



2、kong节点状态查询  
GET     http://127.0.0.1:8001/status 
<pre>
响应结果：
{
  "database": {
    "reachable": true
  },
  "server": {
    "connections_writing": 1,
    "total_requests": 6,
    "connections_handled": 3,
    "connections_accepted": 3,
    "connections_reading": 0,
    "connections_active": 1,
    "connections_waiting": 0
  }
}
</pre>



3、注册接口   【查询 、删除接口 从官网查看】  
GET     http://127.0.0.1:8001/apis 
<pre>
请求体：
{
  "hosts": [
    "jgq-target",
    "tcc.order"
  ],
  "name": "jgqApiTarget",
  "upstream_url": "http://tcc.order",
  "retries": 3
}

响应结果：
{
  "created_at": 1521641586296,
  "strip_uri": true,
  "id": "ea1ff795-ce42-4062-b281-7283b7ab9f8b",
  "hosts": [
    "jgq-target",
    "tcc.order"
  ],
  "name": "jgqApiTarget",
  "http_if_terminated": false,
  "preserve_host": false,
  "upstream_url": "http://tcc.order",
  "upstream_connect_timeout": 60000,
  "upstream_send_timeout": 60000,
  "upstream_read_timeout": 60000,
  "retries": 3,
  "https_only": false
}
</pre>



3、更新接口  
GET     http://127.0.0.1:8001/apis/[name  or id]  
   .e.g:  http://127.0.0.1:8001/apis/jgqApiTarget    

<pre>
请求体：
{
  "hosts": [
    "tcc.order"
  ],
  "name": "jgqApiTarget",
  "upstream_url": "http://tcc.order",
  "retries": 3
}

响应结果：
{
  "created_at": 1521641586296,
  "strip_uri": true,
  "id": "ea1ff795-ce42-4062-b281-7283b7ab9f8b",
  "hosts": [
    "tcc.order"
  ],
  "name": "jgqApiTarget",
  "http_if_terminated": false,
  "preserve_host": false,
  "upstream_url": "http://tcc.order",
  "upstream_connect_timeout": 60000,
  "upstream_send_timeout": 60000,
  "upstream_read_timeout": 60000,
  "retries": 3,
  "https_only": false
}
</pre>









