local responses = require "kong.tools.responses"

local cjson = require "cjson"

local cjson_safe = require "cjson.safe"

local _M={}
-- 对字符串进行分割
function _M.split(str,sp)
    if not str then
       return nil
    end
    local resultStrList = {}
    string.gsub(str,'[^'..sp..']+',function (w)
        if w ~= "" then
          table.insert(resultStrList,w)
        end
    end)
    return resultStrList
end

-- code 小于等于0 表示失败  大于0 成功
function _M.response(delay,code,resp,security)
    local key,sign
    if not delay then
      ngx.ctx.delay_response = false
    end
    if security ~= nil and type(security) == 'table' then
        key = security.key
        sign = security.sign
    end
    --[[
    local data,key,sign,err=security.encode(security.guid(),resp)
    if not data or not key or not sign then
       data = "加解密数据错误,请检查秘钥"
       code = 0
    end
    --]]
    if not delay then
      responses.send_HTTP_OK({data = resp ,key = key ,sign = sign,code = code})
   else
      if custom then
        local temp = cjson_safe.decode(resp)
        if temp == nil then
          temp = {msg = resp,code = -100}
        end
        responses.send_HTTP_OK(temp)
      else
        responses.send_HTTP_OK({code = code,msg = resp})
      end
   end
   return
end

-- code 小于等于0 表示失败  大于0 成功
function _M.response_body(code,data,security)
  local key,sign
  if security ~= nil or type(security) == 'table' then
      key = security.key
      sign = security.sign
  end
  local resp = {}
  resp.data = data or ""
  resp.key = key or ""
  resp.sign = sign or ""
  resp.code = code or 0
  resp = cjson.encode(resp)
  ngx.ctx.len = string.len(resp)
  ngx.arg[1] = resp
  ngx.arg[2] = true
  ngx.ctx.buf = nil
end

function _M.body(func_completed,func_runing,param)
  local ctx = ngx.ctx

  local chunk,eof = ngx.arg[1],ngx.arg[2]
  chunk = chunk or ""
  local info = ctx.buf

  if info then
    ctx.buf = info..chunk
  else
    ctx.buf = chunk
  end

  if eof then
     return func_completed(param)
  else
     return func_runing(param)
  end
end

return _M
