local plugin_name=({...})[1]:match("^kong%.plugins%.([^%.]+)")

local BasePlugin =require "kong.plugins.base_plugin"

local responses = require "kong.tools.responses"

local plugin=BasePlugin:extend()

local auth=require("kong.plugins.tdwauth.tdwauth")

-- require("kong.plugins.tdwauth.tdwredis")

local cjson = require "cjson"

local req_set_header = ngx.req.set_header
local req_get_headers = ngx.req.get_headers
local req_clear_header = ngx.req.clear_header

local utils = require("kong.plugins.tdwutils.tdwutils")

-- 插件执行顺序
plugin.PRIORITY = 40


function plugin:access(config)
  plugin.super.access(self)

  local conf = {}
  conf.redis = config.redis

  local token=ngx.req.get_headers()[conf.redis.tokenkey]

  if not token then
  -- seterror("token为空")
   return utils.response(config.redis.delay,-100,"token为空")
  else
    if  type(token) == "table" then
      token=token[1]
    end
 end

  local v,err=auth.get(conf,token) 

  if not v then
    ngx.log(ngx.ERR,"token认证失败:"..err)
    if err then
        --seterror("token认证失败")
        return utils.response(config.redis.delay,-100,"token认证失败")
    end
  else
   req_set_header("userId",v)
  end
end

return plugin
