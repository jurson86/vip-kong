require("kong.plugins.tdwutils.tdwredis")
local utils = require("kong.plugins.tdwutils.tdwutils")
local kong = ngx.shared.kong

local _M ={}

local script = {}

script.groups = [[
local function host(group)
  local hosts = redis.call('hgetall',group)
  local temp = {}
  if hosts and (#hosts) >= 2 then
    for i = 1,#(hosts),2 do
      local host = hosts[i]
      local ok = redis.call('exists','kong_time_'..group..'_'..host)
      if ok == 1 then
        if string.match(host,':(.*)') == nil then
           table.insert(temp,host..':8001') 
        elseif #string.match(host,':(.*)') == 0 then
           table.insert(temp,host..'8001') 
        else
           table.insert(temp,host) 
        end
      else
        redis.call('hdel',group,host)
      end
    end

    if (#temp) > 0 then
      return true,temp
    else
      return false,nil
    end

  else
    return false,nil
  end
end

local function get_groups()
  local kongGroup = 'kong-groups'
  local result = {}

  local groups = redis.call('hgetall',kongGroup)
  if groups and (#groups) >= 2 then
     local temp = {}
     for i = 1,#(groups),2 do
        local group = groups[i]
        local res,hosts = host(group)
        if res then
          local groupkey = string.match(group,"kong_(.+)")
          temp[groupkey] = hosts
        else
          redis.call('hdel',kongGroup,group)
        end
     end

      if temp then
        for g,h in pairs(temp) do
          table.insert(result,g)
          if type(h) == 'table' then
            table.insert(result,table.concat(h,','))
          else
            table.insert(result,h)
          end
        end
     end

  end

  return result
end

return get_groups()
]]

script.beat = [[
local function beat()
  local kongGroup = 'kong-groups'
  local groupName = "kong_"..KEYS[1]
  local host = KEYS[2]
  local timeout = ARGV[1]
  local res = redis.call('hset',groupName,host,'1')
  res = redis.call('expire',groupName,timeout)
  redis.call('hset',kongGroup,groupName,1)
  local hostKey = 'kong_time_'..groupName..'_'..host
  local r = redis.call('set',hostKey,1)
  r = redis.call('expire',hostKey,timeout)
end
  beat()
]]

--去除首尾空格
local function trim (s) 
  return (string.gsub(s, "^%s*(.-)%s*$", "%1")) 
end 

local function dynamic_keys(prefix,dyn_key_prefix, t)
      t = t or {}
      local temp = {}

      for k, v in pairs(t) do
        local directive = string.match(k, dyn_key_prefix .. "(.+)")
        if directive then
          temp[directive]= tostring(v)
        end
      end
      t[prefix] = temp
end

local function check(conf)
   local err = false

   if conf then
       if not conf.group then
         ngx.log(ngx.ERR,"请配置kong所属的组")
         err = true
       end

       if not conf.ip then
         ngx.log(ngx.ERR,"请配置IP")
         err = true
       end

       local redis = conf.redis
       if not redis or not redis.hosts or not redis.mastername or not redis.password then
         ngx.log(ngx.ERR,"请配置redis包括:redis_hosts,redis_mastetname,redis_password")
         err = true
       end
       
      if not conf.timeInteval then
          conf.timeInteval = 20
      end
      conf.timeout = conf.timeInteval + 5

      if not conf.cacheTimeInteval then
          conf.cacheTimeInteval = 5 * 24 * 60 * 60
      end
   else
       ngx.log(ngx.ERR,"扩展配置为空")
       err = true
   end
   
   return err
end


function _M.get_conf(path)
   if not path then
      path = "/etc/kong/kong-ext.conf"
   end
   local f = io.open(path,"r")
   if f then
      local conf = {}
      for line in f:lines() do
           local l = trim(line)
          if string.len(l) > 0  and not string.find(l,"^#") then
              local vals = utils.split(line,"=")
              conf[trim(vals[1])] = trim(vals[2])
          end
      end
      f:close()
      dynamic_keys("redis","redis_",conf)
      return conf
   else
     ngx.log(ngx.ERR,"文件:"..path.."不存在")
     return nil
   end
end


--操作redis
function  _M.beats(conf,group,host)
  ngx.log(ngx.ERR,"beat is start ...................................")
  local pluginname = "tdwregistry"
  local redis,err = get_redis(conf)
  if redis then
     if not conf.loadscript then
       conf.script = script
       init_script(conf,redis,pluginname)
       conf.loadscript = true
     end
     ngx.log(ngx.ERR,kong[pluginname.."_beat"].."............................beat")
     ngx.log(ngx.ERR,kong[pluginname.."_groups"].."............................groups")
     local res,err = eval_script(redis,kong[pluginname.."_beat"],2,group,host,conf.timeout)
     close(redis)
  else
     ngx.log(ngx.ERR,"redis 信息配置错误")
  end
end

--心跳处理
function _M.handle(premature,conf)
  local redis,err = get_redis(conf)
  _M.beats(conf,conf.group,conf.ip)
  ngx.timer.at(conf.timeInteval,_M.handle,conf)
end

--清楚缓存
function _M.clear_cache(premature,conf)
 local tdw_cache = ngx.shared.tdw_cache
 local tdw_lock  = ngx.shared.tdw_lock
 if tdw_cache then
   tdw_cache:flush_expired()
 end
 if tdw_lock then
   tdw_lock:flush_expired()
 end
 ngx.timer.at(conf.cacheTimeInteval,_M.clear_cache,conf)
end

function _M.registry()
  local conf = _M.get_conf()
  --local socket = require("socket")
  --local hostName = socket.dns.gethostname()
  --local ip = socket.dns.toip(hostName)
  if  not check(conf) then
     if ngx.worker.id() == 0 then
        ngx.timer.at(conf.timeInteval,_M.handle,conf)
        ngx.timer.at(conf.cacheTimeInteval,_M.clear_cache,conf)
     end
  end
end

return _M
