local resty_lock = require("resty.lock")
local tdw_cache = ngx.shared.tdw_cache

local _M = {}

local mt = { __index = _M }

local script = {}

script['setandexpire'] = [[
     redis.call("set",KEYS[1],ARGV[1])
     if tonumber(ARGV[2]) > 0 then
      return redis.call("expire",KEYS[1],ARGV[2])
     end
     ]]


script['getandexpire'] = [[
     local v = redis.call("get",KEYS[1])
     local ok = 1 
     if tonumber(ARGV[1]) > 0 then
       ok = redis.call("expire",KEYS[1],ARGV[1])
     else
       ok = redis.call("persist",KEYS[1])
     end
     if ok == 0 then
       return nil
     else
       return v
     end
    ]]

_M.script = script

_M.prefix = '_kong_ttl_'

function _M.fail(msg,err)
  msg = msg or "exception:"
  err = err or ""
  local e = msg..err
  ngx.log(ngx.ERR,e)
  return e
end

function _M.lock(key,lock_name)
   lock_name = lock_name or "tdw_lock"
   local lock,err = resty_lock:new(lock_name)
   if not lock then
      ngx.log(ngx.ERR,_M.fail('failed to create to lock:',err))
   end
   lock:expire(2) --单位:秒
   local elapsed,err = lock:lock(key)
   if not elapsed then
       return nil,_M.fail("failed to lock:",err)
   end
   return lock,nil
end

function _M.unlock(lock,msg,err)
  if not lock then
     return nil
  end

  local ok,err = lock:unlock()
  if not ok then
     return _M.fail("failed to unlock:",err)
  end
  if msg then
    return _M.fail(msg,err)
  end
  return nil
end

function _M.get_from_custom(conf,key,timeout,callback,...)
  if callback and type(callback) == 'function' then
    return  callback(conf,key,timeout,...)
  else
    return nil,'not found callback function'
  end
end

function _M.set_to_custom(conf,key,timeout,callback,...)
  if callback and type(callback) == 'function' then
    return  callback(conf,key,timeout,...)
  else
    return false,'not found callback function'
  end
end

function _M.from_custom(conf,param,lock,noexistcallback,...)
   local key = param.key
   local timeout = param.timeout
   local cache_timeout = param.cache_timeout
   
   local value,err = _M.get_from_custom(conf,key,timeout,noexistcallback,...)
   if not value or value == null or value == ngx.null  then
      return value,_M.unlock(lock,"err:",err)
   end
   ok,err = tdw_cache:set(key,value,cache_timeout+20)
   ok,err = tdw_cache:set(_M.prefix..key,value,cache_timeout)
   if not ok then
      return value,_M.unlock(lock,"failed to updated tdw_cache:",err)
   else
      return value,_M.unlock(lock)
   end
end

--callback 回调函数 当无法从本来cache获取value时,
--调用回调函数获取value,便于灵活扩展
function _M.get_by_cache(self,key,timeout,cache_timeout,noexistcallback,existcallback,...)
   if not key then
     return nil,fail('key is nil')
   end
   timeout = timeout  or  15 * 24 * 60 * 60 --默认15秒
   cache_timeout = cache_timeout or 15 --默认15秒
   if timeout <= 0 then
      timeout = 15 * 24 * 60 * 60 --默认15天
   end
   if cache_timeout <= 0 then
      cache_timeout = 15 --默认15秒
  end
  if type(noexistcallback) ~= 'function' then
     return nil,fail("回调函数为空") 
  end

 
   local param = { key = key, timeout = timeout, cache_timeout = cache_timeout }
   --local inteval = timeout * 0.3
   local ok,alive,err
   local value,flag = tdw_cache:get(key)
   local lock = _M.lock(key)

   value,flag = tdw_cache:get(key) --double check
   if not value then
     --[[
     value,err = _M.get_from_custom(self.conf,key,timeout,noexistcallback,...)
     if not value or value == null or value == ngx.null  then
       return value,_M.unlock(lock,"err:",err)
     end  
     
     ok,err = tdw_cache:set(key,value,timeout)
     ok,err = tdw_cache:set(_M.prefix..key,value,timeout)
     if not ok then
       return value,_M.unlock(lock,"failed to updated tdw_cache:",err)
     end
     --]]
     return _M.from_custom(self.conf,param,lock,noexistcallback,...)
   else
    --[[
    alive,err = tdw_cache:ttl(key)
     if alive then
        if alive < inteval then
           _M.set_to_custom(self.conf,key,timeout,existcallback,...)
        end
     end
    --]]
     alive ,err = tdw_cache:ttl(_M.prefix..key)
     if not alive then
         return _M.from_custom(self.conf,param,lock,noexistcallback,...)
     else
         tdw_cache:set(key,value,timeout)
         return value,_M.unlock(lock)
     end
   end
end

function _M.new(conf)
  conf.script = _M.script
  return setmetatable({ conf = conf },mt)
end

return _M
