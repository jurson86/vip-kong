package = "kong-plugin-tdwregistry" 
version = "1.0-1" 

local pluginName = package:match("^kong%-plugin%-(.+)$")

supported_platforms = {"linux", "macosx"}

source = {
  url = "...",
  -- tag = "0.1.0"
}

description = {
  summary = "tdwrsa is used to limit access speed",
  homepage = "http://getkong.org",
  license = "MIT"
}

dependencies = {
}

build = {
  type = "builtin",
  modules = {
    ["kong.plugins."..pluginName..".tdwregistry"]  = "tdwregistry.lua",
    --["kong.plugins."..pluginName..".handler"]  = "handler.lua",
    --["kong.plugins."..pluginName..".schema"]   = "schema.lua",
  }
}
