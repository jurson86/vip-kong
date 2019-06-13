--package.path="/usr/local/share/lua/5.1/?.lua;/xtt/lua/?.lua;"
--package.cpath="/usr/local/lib/lua/5.1/?.so;"

local json= require("cjson")
local pkey=require("openssl.pkey")
local digest=require("openssl.digest")
local cipher=require("openssl.cipher")

local conf={
   type="RSA",
   bits=512,
   padding=3,--NoPadding
   signature_algorithm="RSA-SHA1",
   algorithm="des-ede3",
   startIndex=0,
   endIndex=24,
   symbol="&"
}


local keys={}

keys["client_public_key"] = [[
-----BEGIN PUBLIC KEY-----
MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALikFbLvWwshL5GVzNBV8wIG/7lnDyir
k3lC4qd9i1m1M9qgWUSoYvptx1N71xCHGqt03fLfXSQvh0ej4XTP6u8CAwEAAQ==
-----END PUBLIC KEY-----
]]

keys["server_private_key"] = [[
-----BEGIN PRIVATE KEY-----
MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAxrAOk3WPntPS4V24
NtzR0syAwfRX7op1+x7XoMFPYEtLb4w333Ha8S8M4QFEIP9MXevhv0bTPzMMAFMZ
H9C5nQIDAQABAkBeu/M3PAaOpFEYQ6diAUfKStfZoreKdVprsfj+HS3Lq0LZVcaO
7sLoCPtLD5+HoQFx5OwrAeZruk/LJqVl7YQBAiEA+SnyADd3lE/Lt3Orbvb34o3c
5bPmsu023xIIh3B77jECIQDMI5T5nddDKqmyVHi+jxZpyfoHDkZsPDg/PMF0WdfL
LQIhANWKTFxBLVvPk5FTMd61DLd+y42VxCcexEMj6mlhTuVRAiAFJui9nAQwx8Tc
oVEizg2swdIzl2KjnKl2vxvKyfGuSQIgL9j1TI5Ey6waxPn8W8aa6Em+zDN9tZJZ
3+rhjJDPrhc=
-----END PRIVATE KEY-----
]]


local req_data=[[
{"data":"U8aAj6fcIug=","key":"AouQnK9EydfFyWVgiix2X4\/UNt\/Acc5FzG0lCi4ZVyaB2O4CiGhtjZJ/XjjEKLl4+VNJIayMVQ37SBnZO64ocQ==","sign":"qXNUzGZotWx8pU6bRuaxHi/noWI2ODalXlf2Z9ZuIYx5zrI/ZxR/htlQJWqF7ehkhfXEonu550X05/SIkKc76g=="}
]]

local req_data0=[[
{"data":"N3segNduOOhk6lejti/0VXoa4mu1acyzvKmMPGFIxWP19UpkeShbG3/3NZULNzUF5W6MUZLEaTAP8iiDdpzGyB8iVVL4bdzOIq6vQQCjqy/6TTzVd5MXKeMx5WgpzoEG","key":"O4cntR6wVDoaUjYSm32zIQtsUDfoavAsOMIxLDpv/tE78dn5XkUTkkuO4A6+juUPi8MxHPimx67Dp7Q5oSdOuA==","sign":"jPOVIXRT9jetWp/YgZnkB26zMX3WUv4nsNNKYeToeVKbPkEZ5IWy8hQr5+orVjzZWatviCIYUOT1kHFOYz1wVQ=="}
]]

--去除空字符(不是空格)
function trim(str)
  local s=""
  for name in string.gmatch(str,"[^%z]") do
    s = s..name
  end
  return s
end

--测试
--decode(req_data)

--解密数据,返回解密的key和数据
function decode(data)
 --[[ if verify(data) then
     local original_key=decode_key(data.key)
     if original_key  then
        local original_data= decode_data(original_key,data)
        if original_data then
          return original_data
        else
          ngx.say("解密数据错误")
        end
     else
        ngx.say("解密key错误")
    end
  else
    ngx.say("验签错误")
  end
   return original_key,original_data
--]]
verify(data)
local original_key=decode_key(data.key)
local original_data= decode_data(original_key,data.data)
return original_key,original_data
end

--验签
function verify(data)
  local signature=digest.new(conf.signature_algorithm)
  local pubKey=pkey.new{type=conf.type,bits=conf.bits}
  signature:update(data.key..conf.symbol..data.data)
  pubKey:setPublicKey(keys.client_public_key)
  local sign_data = ngx.decode_base64(data.sign)
  local result=pubKey:verify(sign_data,signature)
  ----- ngx.say("验签结果：")
  ----- ngx.say(result)
  return result
end


--解密key
function decode_key(key)
  local priKey=pkey.new{type=conf.type,bits=conf.bits}
  priKey:setPrivateKey(keys.server_private_key)
  local original_key=trim(priKey:decrypt(ngx.decode_base64(key),{rsaPadding=conf.padding}))
  ----- ngx.say("解密key："..original_key)
  return original_key
end

--解密data
function decode_data(key,data)
  local cp=cipher.new(conf.algorithm)
  local k=string.sub(key,conf.startIndex,conf.endIndex)
  local p=cp:decrypt(k)
  local original_data=p:final(ngx.decode_base64(data))
  ----- ngx.say("解密数据:"..original_data)
  return original_data
end

--[[
   ------------------------ 开始加密数据 ----------------------
--]]
keys["server_public_key"]=[[
-----BEGIN PUBLIC KEY-----
MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMawDpN1j57T0uFduDbc0dLMgMH0V+6K
dfse16DBT2BLS2+MN99x2vEvDOEBRCD/TF3r4b9G0z8zDABTGR/QuZ0CAwEAAQ==
-----END PUBLIC KEY-----
]]

keys["client_private_key"]=[[
-----BEGIN PRIVATE KEY-----
MIIBVQIBADANBgkqhkiG9w0BAQEFAASCAT8wggE7AgEAAkEAuKQVsu9bCyEvkZXM
0FXzAgb/uWcPKKuTeULip32LWbUz2qBZRKhi+m3HU3vXEIcaq3Td8t9dJC+HR6Ph
dM/q7wIDAQABAkAIZgZKplKI1hCY2ayASXiNPg5Bu6tX2T4TBPTUU4SUJvMRrrvq
8d1PsHHS6QMh8eqaoclWPVG7nxc0v42Xm0NpAiEA4yUjXiw1ykSFesGXwz/Ee0Ma
3Nn4lhmNscdXfAz7WwMCIQDQGLF8dETjSlMAG9Ll9fDdBTc6CBlcjxHrJ/OR4FQW
pQIhANIl9O/WpTlqZbfpzhfVMln7/qlffI6aO67Dx9u8QvyzAiAOYxaqRxOYL0RL
xqCvG3Dapwipb3PpbU7M1kdFt5+4rQIhALQhBipwK0P5hswUeDuPTdky1SKLYcZY
LuFunn2g3k2B
-----END PRIVATE KEY-----
]]

--加密
function encode(original_key,original_data)
  local data=encode_data(original_key,original_data)
  local key =encode_key(original_key)
  local sign=sign(key,data)
 return "{\"data\":\""..data.."\",\"key\":\""..key.."\",\"sign\":\""..sign.."\"}"

end

--加密数据
function encode_data(original_key,original_data)
  local cp=cipher.new(conf.algorithm)
  local key=string.sub(original_key,conf.startIndex,conf.endIndex)
  local p=cp:encrypt(key)
  local data=ngx.encode_base64(p:final(original_data))
  ----- ngx.say("加密数据："..data)
  return data
end


--加密key
function encode_key(original_key)
  local pubKey=pkey.new{type=conf.type,bits=conf.bits}
  pubKey:setPublicKey(keys.client_public_key)
  --填充时间信息
  local pad=os.date("%Y%m%d%H%M%S%m%d%H%M%S", os.time())
  local key=ngx.encode_base64(pubKey:encrypt(original_key.."&&##"..pad,{rsaPadding=3}))
  --local key=ngx.encode_base64(pubKey:encrypt(original_key))
  ----- ngx.say("key加密数据："..key)
  return key
end

--生成签名
function sign(key,data)
  local signature=digest.new(conf.signature_algorithm) 
  local priKey=pkey.new{type=conf.type,bits=conf.bits}
  priKey:setPrivateKey(keys.server_private_key)
  signature:update(key..conf.symbol..data)
  local sign=ngx.encode_base64(priKey:sign(signature))
  ----- ngx.say("签名数据："..sign)
 -- ngx.say("{\"data\":\""..data.."\",\"key\":\""..key.."\",\"sign\":\""..sign.."\"}")
  return sign
end
