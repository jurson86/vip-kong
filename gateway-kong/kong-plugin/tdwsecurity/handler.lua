local plugin_name=({...})[1]:match("^kong%.plugins%.([^%.]+)")

local BasePlugin =require "kong.plugins.base_plugin"

local responses = require "kong.tools.responses"

local plugin=BasePlugin:extend()

require("kong.plugins.tdwsecurity.tdwsecurity")

-- 插件执行顺序
plugin.PRIORITY=13

function plugin:new()
  plugin.super.new(self,plugin_name)
end

function plugin:access(config)
  plugin.super.access(self)
  local ctx = ngx.ctx
  ctx.rt_body_chunks = {}
  ctx.rt_body_chunk_number = 1
end


function plugin:header_filter(config)
  plugin.super.header_filter(self)
  ngx.header.content_length = ngx.ctx.len
end

function plugin:body_filter(config)
  plugin.super.body_filter(self)

    local ctx=ngx.ctx
    local chunk,eof=ngx.arg[1],ngx.arg[2]
    --chunk="aaa"
    chunk=chunk or ""
    local info=ctx.buf
    if info then
      ctx.buf=info..chunk
    else
      ctx.buf=chunk
    end

    ngx.log(ngx.INFO,eof)
    if eof then
      ngx.log(ngx.INFO,"111>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"..ctx.buf)
      local data=encode("123456789009876543211234567890666666",ctx.buf)
      ngx.log(ngx.INFO,"111>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"..data)
      ctx.len=string.len(data)
      ngx.arg[1]=data
      ctx.buf=nil
    else
      ngx.arg[1] = nil
      ngx.log(ngx.INFO,"222>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"..chunk)
    end 
end


function plugin:log(config)
  plugin.super.log(self)
--  ngx.header["Content-Type"] = "application/json"
 --  ngx.header.content_length = ngx.ctx.len
  --ngx.log(ngx.INFO,ngx.ctx.len)
end

return plugin
