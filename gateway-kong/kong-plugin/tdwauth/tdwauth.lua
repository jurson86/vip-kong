--require("kong.plugins.tdwauth.tdwredis")
require("kong.plugins.tdwutils.tdwredis")
local tdw_cache = require("kong.plugins.tdwauth.tdwcache")
local kong = ngx.shared.kong

local _M = {}

_M.timeout = 15 * 24 * 60 * 60
_M.cache_timeout = 15

function _M.get_and_expire(...)
   local value = nil
   local conf =   select(1,...)
   local key =   select(2,...)
   local timeout = select(3,...)
   local pluginname = "tdwauth"
   local redis,err = get_redis(conf)
   if redis then
     if not _M.loadscript then
       init_script(conf,redis,pluginname)
       _M.loadscript = true
     end

     local keysha = kong[pluginname.."_getandexpire"]
     --redis返回空的类型为userdata,
     --返回结果需要使用v == null or v == ngx.null 进行判断
     value,err = eval_script(redis,keysha,1,key,timeout)
     close(redis)
   else
     ngx.log(ngx.ERR,err)
   end
   return value,err
end

function _M.expire(...)
  local conf =   select(1,...)
  local key =   select(2,...)
  local timeout = select(3,...)
  local redis,err = get_redis(conf)
  redis:expire(key,timeout)
  close(redis)
end
-- 获取redis的值
function _M.get(conf,key,dbindex)
 local redis,err,v
 --redis,err=get_redis(conf)
 ---
 local cache = tdw_cache.new(conf)
 v,err = cache:get_by_cache(key,_M.timeout,_M.cache_timeout,_M.get_and_expire,_M.expire)

 if v == null or v == ngx.null then
   v = nil
 end

 if v then
   return v
 else
   ngx.log(ngx.ERR,err)
 end
 --redis = cache.conf.redis
 redis,err=get_redis(conf)
 ---
 if not redis then
  return nil,err
 end
 v,err = redis:get(key)
 --setPool(redis)
 close(redis)
 if not v then
   ngx.log(ngx.ERR,"redis中查询"..key.."失败:"..err)
   return nil,err
 end

 if v == ngx.null then
 return nil,"redis中[key="..key.."]不存在"
 end
 return v,nil
end 

-- 设置redis的值,expire_time大于0，才设置超时时间
function _M.set(conf,key,val,expire_time)
  local redis = get_redis(conf)
  if not redis then
    return 0
  end
  local ok,err = redis:evalsha(kong.info["setandexpire"],1,key,val,expire_time)
  --local ok,err= redis:eval(script_str,1,key,val,expire_time)
  if not ok then
    ngx.log(ngx.ERR,"redis 设置值错误:"..err)
  end
  setPool(redis)
  return ok
end

--获取redis的值，exprie_time大于0，才设置超时时间
function _M.get_expire(conf,key,expire_time)
  local redis,err,ok
  redis,err = get_redis(conf)
  if not redis then
   return nil,err
  end
  ok,err = redis:evalsha(kong.info["getandexpire"],1,key,expire_time)
  if not ok then
   ngx.log(ngx.ERR,"redis 设置值错误:"..err)
  end
  setPool(redis)
  return ok
end

return _M
