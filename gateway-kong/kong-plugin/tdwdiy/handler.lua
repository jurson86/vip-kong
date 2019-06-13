local plugin_name=({...})[1]:match("^kong%.plugins%.([^%.]+)")

local BasePlugin =require "kong.plugins.base_plugin"

local plugin=BasePlugin:extend()

local utils = require("kong.plugins.tdwutils.tdwutils")

-- 插件执行顺序,数字越大优先级越高
plugin.PRIORITY = 60


function plugin:new()
  plugin.super.new(self,plugin_name)
end

function plugin:init_worker()
  plugin.super.init_worker(self)
end

function plugin:access(config)
  plugin.super.access(self)
  return utils.response(config.halt.delay,999,config.halt.msg,false)
end


function plugin:header_filter(config)
  plugin.super.header_filter(self)
end

function plugin:body_filter(config)
  plugin.super.body_filter(self)
end


function plugin:log(config)
  plugin.super.log(self)
end

return plugin
