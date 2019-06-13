local req_read_body = ngx.req.read_body
local req_set_body_data = ngx.req.set_body_data
local req_get_body_data = ngx.req.get_body_data
local security=require("kong.plugins.tdwrsa.security")

local cjson = require "cjson"

local _M={}

local function parse_json(body)
  if body then
    local status, res = pcall(cjson.decode, body)
    if status then
      return res
    end
  end
end

local function set_data(data)
  ngx.req.set_body_data(data)
end 


function _M.execute()
 req_read_body()
 local body = req_get_body_data()
 if not body then
  return true,nil
 end
 local json_body=parse_json(body)
 local key,data,err= security.decode(json_body)
 if not key or not data then
   return nil ,err
 else
   set_data(data)
   return true,nil
 end
end

return _M


