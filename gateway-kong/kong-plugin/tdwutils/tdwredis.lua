local rredis = require("resty.redis")
rredis.add_commands("select")
rredis.add_commands("sentinel")
rredis.add_commands("eval")
rredis.add_commands("eval_script")

local cjson = require "cjson"

local kong = ngx.shared.kong
local utils = require("kong.plugins.tdwutils.tdwutils")
local master = {} 
local temp = {}

function conn(conf)
  if not master.host then
     ngx.log(ngx.ERR,"host为空，无法连接redis")
     return nil ,"host为空，无法连接redis"
  end

  if not master.port then
     master.port = 6379
  end

  if tonumber(master.port) == nil  then
    ngx.log(ngx.ERR,"redis端口错误")
    return nil,"redis端口错误"
  end

  local redis,err,ok
  redis,err = rredis:new()

  if err then
     ngx.log(ngx.ERR,"创建redis对象失败:"..err)
     return nil,err
  end

  redis:set_timeout(conf.timeout or 2000)

  ok,err = redis:connect(master.host,master.port)
  if err then
    err="redis连接失败:"..err.." > ["..master.host..":"..master.port.."]"
    ngx.log(ngx.ERR,err)
    return nil,err
  else
    return redis,nil
  end
end

--检查配置文件是否有更新
function check_and_update(conf)
  local t={}
  t.hosts = conf.hosts
  t.dbindex = conf.dbindex
  t.sentinel = conf.sentinel
  t.password = conf.password
  t.mastername = conf.mastername
  t.timeout = conf.timeout
  t.tokenkey = conf.tokenkey
  local s1 = cjson.encode(t)
  local s2 = cjson.encode(temp)

  if s1 ~= s2 then
    temp = t
    reset()
    return true
  end

  return false
end

function reset()
  master.host = nil
  master.port = nil
end

--获取redis对象
function get_redis(config)

  if not config then
   return nil,"配置为空"
  end

  local conf = config.redis or {}
  conf.retry = 3

  local res = check_and_update(conf)

  local sentinel = conf.sentinel or false
  
  --if not master or not (master.host and master.port) then
  if not master or not (master.host and master.port) or res then
     if sentinel then
        -- host,port,err=get_redis_info(host,port,conf.mastername,conf.timeout)
        local ok,err = get_redis_by_sentinels(conf,false)
        if not ok then
          -- ngx.log(ngx.ERR,err)
          return nil,err
        end
     else
        --如果直接配置redis,默认采用第一个redis地址
        local hosts = utils.split(conf.hosts,",")
        local host = hosts[1]
        local h = utils.split(host,":")
        if not h[1] then
           return nil,"redis没有配置host"
        end
        master.host = h[1]
        master.port = h[2] or 6379
     end 
  end 

  local err,retry,redis

  if conf.retry <= 0 or conf.retry > 5 then
      retry = 2
  else
     retry = conf.retry
  end

  for c=1,retry,1 do
    redis,err = conn(conf)
    if redis then
      break
    end
  end
     
  if not redis then
    if sentinel then
      local ok,err = get_redis_by_sentinels(conf,true)
      if not ok then
        return nil,err
      else
        redis,err = conn(conf)
        if not redis then
          return nil,err
        end
      end
    else
      return nil,err
    end
  end


  local res, err = redis:auth(conf.password)

  if err then
    ngx.log(ngx.ERR,"认证失败: ", err)
    return nil,err
  end

  local ok, err = redis:select(conf.dbindex or 0)

  if err then
   ngx_log(ngx.ERR, "切换 Redis database失败: "..err)
   return nil, err
  end
  
  --初始化脚本
  --load_script(redis)
  return redis,nil
end

-- 设置连接池
function setPool(redis,timeout,psize)
 if not redis then
   return false,"redis is nil"
 end
 -- 单位：毫秒
 local pool_max_idle_time = timeout or 1000*60*10
 --连接池大小
 local pool_size = psize or 200
 local ok,err = redis:set_keepalive(pool_max_idle_time,pool_size)

 if not ok then
   ngx.log(ngx.ERR,"redis设置连接池失败:"..err)
   return false,err
 end

 return true,nil
end


function close(redis,timeout,psize)
  return setPool(redis,timeout,psize)
end

-- 通过redis哨兵获取redis服务器信息
function get_redis_info(sentinelhost,sentinelport,mastername,timeout)

  mastername = mastername or "mymaster"
  local redis,err = rredis:new()

  if not redis then
     ngx.log(ngx.ERR,"创建redis对象失败:"..err)
     return nil,nil,err
  end

  redis:set_timeout(timeout or 2000)

  local ok,err = redis:connect(sentinelhost,sentinelport)

  if not ok then
     ngx.log(ngx.ERR,"redis sentinel ["..sentinelhost..":"..sentinelport.."] 连接失败:"..err)
     return nil,nil,err
  end

  local serverinfo,err = redis:sentinel("get-master-addr-by-name",mastername)

  if not serverinfo or serverinfo == ngx.null  then
   err = err or " null"
   ngx.log(ngx.ERR,"获取redis信息错误,请检查redis配置信息:"..err)
   return nil,nil,err
  end

  setPool(redis)
  return serverinfo[1],serverinfo[2],nil

end

function setMaster(premature,redis)
  if premature then
     return
  end

  local res, err = redis:read_reply()
  if not res then
     ngx.log(ngx.ERR,"redis读取订阅消息失败: ".. err)
     if err ~= "timeout" then
       redis:unsubscribe("+switch-master")
     end
     return
  end
  local resp = cjson.encode(res)
  -- 消息格式:<master name> <oldip> <oldport> <newip> <newport> 
  local msg = resp[3]
  local datas = utils.split(msg," ")
  if (#datas) > 3 then
    if datas[1] == master.name then
      master.host = datas[4]
      master.port = data[5]
    end
  end
end


function subscribe(redis)
    local res, err = redis:subscribe("+switch-master")
    if not res then
        ngx.log(ngx.ERR,"redis订阅失败: "..err)
        return
    end

    ngx.timer.at(1,setMaster,redis)
end

function sentinel(host,conf)

    local h = utils.split(host,":")
    local sentinelhost = h[1]
    local sentinelport = h[2] or 26379

    local redis,err = rredis:new()

    if not redis then
      ngx.log(ngx.ERR,"创建redis对象失败:"..err)
      return false,err
    end

    redis:set_timeout(conf.timeout or 2000)

    local ok,err = redis:connect(sentinelhost,sentinelport)

    if not ok then
        ngx.log(ngx.ERR,"redis sentinel ["..sentinelhost..":"..sentinelport.."] 连接失败:"..err)
        return false,err
    end

    local serverinfo,err = redis:sentinel("get-master-addr-by-name",conf.mastername)

    if not serverinfo or serverinfo == ngx.null  then
        err = err or " null"
        ngx.log(ngx.ERR,"获取redis信息错误,请检查redis配置信息:"..err)
        return false,err
    end

    master.host = serverinfo[1]
    master.port = serverinfo[2]

    --设置连接池时间30分钟
    close(redis,1000*60*30)
    --setPool(redis)
    -- subscribe(redis)
    --return serverinfo[1],serverinfo[2],nil
    return true,nil
end


--通过sentinel集群获取redis
function get_redis_by_sentinels(conf,random)

  if not conf then
    return false,"无法获取配置信息"
  end

  local  mastername = conf.mastername or "mymaster"
  master.name = mastername

  local hosts = utils.split(conf.hosts,",")
  if random then
      local index = math.random(#hosts)
      if index == master.index then
        index = math.random(#hosts)
        master.index = index
      end
      local h = hosts[index]
      if sentinel(h,conf) then
         return true,nil
      end
  end

  local ok
  for k,host in pairs(hosts) do 
     ok,err = sentinel(host,conf)
     if ok then
        master.index = k
        return true,nil
     end
  end

  return false,err

end


function eval_script(redis,keysha,keyCount,...)
  if not redis then
    return 0
  end

  local ok,err= redis:evalsha(keysha,keyCount,...)
  if err then
     ngx.log(ngx.ERR,"redis 执行script错误:"..err)
  end
  return ok,err
end
              
--初始化脚本
function init_script(conf,redis,pluginname)

   if not redis then
     redis = get_redis(conf)
   end

   local luascript = conf.script or {}

   for key ,value in pairs(luascript) do
     local sha,err = redis:script("load",value)

     if  sha then
       kong[pluginname.."_"..key] = sha
     else
       ngx.log(ngx.ERR,"redis加载脚本错误:"..err)
     end
   end
  -- setPool(redis)
end
