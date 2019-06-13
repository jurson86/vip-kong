package = "kong-plugin-tdwutils" 
version = "1.0-1" 

local pluginName = package:match("^kong%-plugin%-(.+)$")

supported_platforms = {"linux", "macosx"}

source = {
  url = "...",
  -- tag = "0.1.0"
}

description = {
  summary = "tdwrsa is used to encry or decry with rsa",
  homepage = "http://getkong.org",
  license = "MIT"
}

dependencies = {
}

build = {
  type = "builtin",
  modules = {
    ["kong.plugins."..pluginName..".tdwutils"]   = "tdwutils.lua",
    ["kong.plugins."..pluginName..".tdwredis"]   = "tdwredis.lua",
  }
}
