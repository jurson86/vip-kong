package = "kong-plugin-tdwtrace" 
version = "1.0-1" 

local pluginName = package:match("^kong%-plugin%-(.+)$")

supported_platforms = {"linux", "macosx"}

source = {
  url = "...",
  -- tag = "0.1.0"
}

description = {
  summary = "tdwrsa is used to trace request data log",
  homepage = "http://getkong.org",
  license = "MIT"
}

dependencies = {
}

build = {
  type = "builtin",
  modules = {
    ["kong.plugins."..pluginName..".handler"]  = "handler.lua",
    ["kong.plugins."..pluginName..".trace"]    = "trace.lua",
    ["kong.plugins."..pluginName..".schema"]   = "schema.lua",
    ["kong.plugins."..pluginName..".zipkin"]   = "zipkin.lua",
  }
}
