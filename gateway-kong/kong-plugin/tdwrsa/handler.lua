local plugin_name=({...})[1]:match("^kong%.plugins%.([^%.]+)")

local BasePlugin = require "kong.plugins.base_plugin"

local responses = require "kong.tools.responses"

local plugin = BasePlugin:extend()

local security = require("kong.plugins.tdwrsa.security")

local utils = require("kong.plugins.tdwutils.tdwutils")

local access = require("kong.plugins.tdwrsa.access")

local cjson = require "cjson"

local cjson_safe = require "cjson.safe"

-- 插件执行顺序
plugin.PRIORITY = 20

function plugin:access(config)
    plugin.super.access(self)
    security.setkeys(config)

    local ok,err = access.execute()
    if not ok then
     --[[ local info={ code = 0}
      info.msg="{\"code\":-150,\"msg\":\"解密数据错误\"}"
      info.status=false
      ngx.ctx.tdwerror=info
      return
-]]
      return utils.response(config,delay,0,"解密数据错误")
    end
end


function plugin:header_filter(config)
  plugin.super.header_filter(self)
  -- ngx.header["Content-Type"] = "application/json"
  ngx.header.content_length = ngx.ctx.len
end

local function completed()
   local resp
   local ctx = ngx.ctx
   local result = cjson_safe.decode(ctx.buf)
   if result and result.code and tonumber(result.code) ~= nil then
     result = tonumber(result.code)
   else
     result = 1
   end
   local data,key,sign,err = security.encode(security.guid(),ctx.buf)

   if not data or not key or not sign then
     return  utils.response_body(-100,"加解密数据错误,请检查秘钥")
   else
     return utils.response_body(result,data,{key = key ,sign = sign})
   end
end

local function runing()
  ngx.arg[1] = nil
end

function plugin:body_filter(config)
    plugin.super.body_filter(self)
    security.setkeys(config)
    utils.body(completed,runing)
    --[[
    local ctx=ngx.ctx
   
    local chunk,eof=ngx.arg[1],ngx.arg[2]
    chunk=chunk or ""
    local info=ctx.buf

    if info then
      ctx.buf=info..chunk
    else
      ctx.buf=chunk
    end

    if eof then
        local resp
        local result = cjson_safe.decode(ctx.buf)
        if result and result.code and (tonumber(result.code) ~= nil and tonumber(result.code) <= 0)  then
             result = 0
        else
             result = 1
        end
        local data,key,sign,err=security.encode(security.guid(),ctx.buf)

        if not data or not key or not sign then
          return  utils.response_body(0,"加解密数据错误,请检查秘钥")
        else
          return utils.response_body(result,data,{key = key ,sign = sign})
        end
    else
      ngx.arg[1] = nil
    end 
  --]]
end

return plugin
