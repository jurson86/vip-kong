package = "kong-plugin-tdwauth"
version = "1.0-1"

local pluginName = package:match("^kong%-plugin%-(.+)$")

supported_platforms = {"linux", "macosx"}

source = {
  url = "...",
  -- tag = "0.1.0"
}

description = {
  summary = "tdwauth is used to auth token",
  homepage = "http://getkong.org",
  license = "MIT"
}

dependencies = {
}

build = {
  type = "builtin",
  modules = {
    ["kong.plugins."..pluginName..".handler"]  = "handler.lua",
    ["kong.plugins."..pluginName..".schema"]   = "schema.lua",
    ["kong.plugins."..pluginName..".tdwauth"]  = "tdwauth.lua",
    ["kong.plugins."..pluginName..".tdwcache"] = "tdwcache.lua",
  }
}
