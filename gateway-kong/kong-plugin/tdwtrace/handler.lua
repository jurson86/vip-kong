local plugin_name=({...})[1]:match("^kong%.plugins%.([^%.]+)")

local BasePlugin =require "kong.plugins.base_plugin"

local trace = require("kong.plugins.tdwtrace.trace")

local zipkin = require("kong.plugins.tdwtrace.zipkin")

local plugin=BasePlugin:extend()

-- 插件执行顺序,数字越大优先级越高
plugin.PRIORITY = 80

function plugin:access(config)
 trace.trace_req(config, ngx.req, ngx.ctx)
end

function plugin:header_filter(config)
  plugin.super.header_filter(self)
  -- ngx.header["Content-Type"] = "application/json"
    ngx.header.content_length = ngx.ctx.len
  end

local function timer_callback(premature, plugin_conf, trace)
  ngx.log(ngx.DEBUG, 'timer')
  if premature then
    ngx.log(ngx.DEBUG, 'premature')
    return
  end

  if not trace then
    ngx.log(ngx.DEBUG, 'not trace')
    return
  end

  zipkin.send_trace(plugin_conf, trace)

end


function plugin:body_filter(config)
  local chunk,eof=ngx.arg[1],ngx.arg[2]
  local ctx=ngx.ctx
  chunk=chunk or ""
  local info=ctx.buf

  if info then
      ctx.buf=info..chunk
  else
      ctx.buf=chunk
  end

  if eof then
     trace.trace_resp()
     if config.exportable then    
       local trace = zipkin.prepare_trace(config, ngx.req, ngx.ctx, ngx.status)
       local ok, err = ngx.timer.at(0, timer_callback,config, trace)
       if not ok then
         ngx.log(ngx.ERR, "failed to create the timer: ", err)
         return
       end
     end
  else
     ngx.arg[1] = nil
  end

end
  
function plugin:log(config)
end

return plugin
