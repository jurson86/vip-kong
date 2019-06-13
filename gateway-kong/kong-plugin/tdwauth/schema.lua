local function check_hosts(value,config)
   local utils = require("kong.plugins.tdwutils.tdwutils")
   local hosts=utils.split(value,",")
   if not config.sentinel then
      if (#hosts) > 1 then
         return false,"非哨兵模式下,请配置单机redis"
      end
      return true,nil,{ hosts = value }
   end
   return true,nil,{ hosts = value }
end

return {
  no_consumer = true,
  fields = {
    redis ={
      type = "table",
      schema = {
       fields ={
          hosts      = {type = "string", required = true,func = check_hosts},
          dbindex    = {type = "number", default = 8},
          retry      = {type = "number", default = 3},
          sentinel   = {type = "boolean",default = true},
          password   = {type = "string", required = true},
          mastername = {type = "string", default = "mymaster"},
          timeout    = {type = "number", default = 2000},
          tokenkey   = {type = "string", default = "token"},
          delay = {type = "boolean", default = true},
        }
      }
     }
  }
}
