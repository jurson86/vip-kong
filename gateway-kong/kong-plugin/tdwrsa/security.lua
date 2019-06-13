local json = require("cjson")
local pkey = require("openssl.pkey")
local digest = require("openssl.digest")
local cipher = require("openssl.cipher")
local rsa = require("kong.plugins.tdwrsa.tdwrsa")

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

local pkcs={
  pkcs1="PKCS#1",
  pkcs8="PKCS#8"
}


local keys={}

keys["client_public_key11"] = [[
-----BEGIN PUBLIC KEY-----
MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBALikFbLvWwshL5GVzNBV8wIG/7lnDyir
k3lC4qd9i1m1M9qgWUSoYvptx1N71xCHGqt03fLfXSQvh0ej4XTP6u8CAwEAAQ==
-----END PUBLIC KEY-----
]]

keys["server_private_key11"] = [[
-----BEGIN RSA PRIVATE KEY-----
MIIBVAIBADANBgkqhkiG9w0BAQEFAASCAT4wggE6AgEAAkEAxrAOk3WPntPS4V24
NtzR0syAwfRX7op1+x7XoMFPYEtLb4w333Ha8S8M4QFEIP9MXevhv0bTPzMMAFMZ
H9C5nQIDAQABAkBeu/M3PAaOpFEYQ6diAUfKStfZoreKdVprsfj+HS3Lq0LZVcaO
7sLoCPtLD5+HoQFx5OwrAeZruk/LJqVl7YQBAiEA+SnyADd3lE/Lt3Orbvb34o3c
5bPmsu023xIIh3B77jECIQDMI5T5nddDKqmyVHi+jxZpyfoHDkZsPDg/PMF0WdfL
LQIhANWKTFxBLVvPk5FTMd61DLd+y42VxCcexEMj6mlhTuVRAiAFJui9nAQwx8Tc
oVEizg2swdIzl2KjnKl2vxvKyfGuSQIgL9j1TI5Ey6waxPn8W8aa6Em+zDN9tZJZ
3+rhjJDPrhc=
-----END RSA PRIVATE KEY-----
]]



local _M={}

local pubKey
local priKey
local cp
--去除空字符(不是空格)
local function trim(str)
  local tb={""}
  for name in string.gmatch(str,"[^%z]") do
    table.insert(tb,name)
  end
  return table.concat(tb)
end

function _M.guid()
    local seed={'e','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'}
    local tb={}
    for i=1,32 do
        table.insert(tb,seed[math.random(1,16)])
    end

    local sid=table.concat(tb)
    return string.format('%s-%s-%s-%s-%s',
        string.sub(sid,1,8),
        string.sub(sid,9,12),
        string.sub(sid,13,16),
        string.sub(sid,17,20),
        string.sub(sid,21,32)
    )
end

--验签
function _M.verify(data)
  local ok=true
  local err,result

  if not pubKey then
    pubKey,err=rsa:new({key_type=pkcs.pkcs8,public_key=keys.client_public_key,padding=conf.padding,algorithm=conf.signature_algorithm})

    if not pubKey then
      return false,err
    end

  end

  local sign_data = ngx.decode_base64(data.sign)
  result,err=pubKey:verify(data.key..conf.symbol..data.data,sign_data)

  if not result then
   ngx.log(ngx.ERR,"验签错误")
   ok=false
  end

  return ok,err
end


--解密key
function _M.decode_key(key)
 local err

 if not priKey then
    priKey,err=rsa:new({key_type=pkcs.pkcs8,private_key=keys.server_private_key,padding=conf.padding,algorithm=conf.signature_algorithm})

    if not priKey then
      ngx.log(ngx.ERR,"new rsa pri key err:"..err)
      return nil,err
    end

 end

 local original_key=trim(priKey:decrypt(ngx.decode_base64(key)))
 return original_key,nil
end

--解密data
function _M.decode_data(key,data)
  if not cp then
    cp=cipher.new(conf.algorithm)
  end

  local k=string.sub(key,conf.startIndex,conf.endIndex)
  local p=cp:decrypt(k)
  local original_data=p:final(ngx.decode_base64(data))
  return original_data,nil
end


--解密数据,返回解密的key和数据
function _M.decode(data)
  local ok,err,original_key,original_data

  ok,err=_M.verify(data)

  if not ok then
    return nil,nil,err
  end

  original_key,err=_M.decode_key(data.key)

  if not original_key then
    return nil,nil,err
  end

  original_data,err= _M.decode_data(original_key,data.data)
  
  if not original_data then
    return nil,err
  end

  return original_key,original_data,err
end

------------------------ 开始加密数据 ----------------------


--加密数据
function _M.encode_data(original_key,original_data)
  if not cp then
    cp=cipher.new(conf.algorithm)
  end

  local key=string.sub(original_key,conf.startIndex,conf.endIndex)
  local p=cp:encrypt(key)
  local data=ngx.encode_base64(p:final(original_data))
  return data
end


--加密key
function _M.encode_key(original_key)
 local err,enc

  if not pubKey then
    ngx.log(ngx.ERR,keys.client_public_key..">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>")
    pubKey,err=rsa:new{key_type=pkcs.pkcs8,public_key=keys.client_public_key,padding=conf.padding,algorithm=conf.signature_algorithm}

    if not pubKey then
      ngx.log(ngx.ERR,"new rsa pub err:"..err)
      return nil,err
    end

  end

 local pad=os.date("%Y%m%d%H%M%S%m%d%H%M%S", ngx.time())
 enc,err= pubKey:encrypt(original_key.."&&##"..pad)

 if not enc then
  ngx.log(ngx.ERR,"fail to encrypt:"..err)
  return nil,err
 end
  
  local key=ngx.encode_base64(enc)
  return key,nil
end

--生成签名
function _M.sign(key,data)
  local err,sig

  if not priKey then
     priKey,err=rsa:new({key_type=pkcs.pkcs8,private_key=keys.server_private_key,padding=conf.padding,algorithm=conf.signature_algorithm})

     if not priKey then
       ngx.log(ngx.ERR,"new rsa pri err :"..err)
       return nil,err
     end

  end

  sig,err= priKey:sign(key..conf.symbol..data)

  if not sig then
   ngx.log(ngx.ERR,"fail to sign"..err)
   return nil,err
  end

  local sign=ngx.encode_base64(sig)
  return sign,nil
end

--加密
function _M.encode(original_key,original_data)
  local key,err,sign
  local data=_M.encode_data(original_key,original_data)
  key,err =_M.encode_key(original_key)

  if not key then
    return nil,nil,nil,err
  end

  sign,err=_M.sign(key,data)

  if not sign then
   return nil,nil,nil,err
  end

  return data,key,sign,err
end

function _M.setkeys(config)
  local init = true
  if not  keys["client_public_key"] or not keys["server_private_key"] or keys["client_public_key"]  ~= config["client_public_key"] or keys["server_private_key"] ~= config["server_private_key"] then
   init = false
  end

  if not init then
    keys["client_public_key"]  = config["client_public_key"]
    keys["server_private_key"] = config["server_private_key"]
    priKey,err=rsa:new({key_type=pkcs.pkcs8,private_key=keys.server_private_key,padding=conf.padding,algorithm=conf.signature_algorithm})
    if not priKey then
       ngx.log(ngx.ERR,"new rsa pri err :"..err)
       return nil,err
    end
    pubKey,err=rsa:new({key_type=pkcs.pkcs8,public_key=keys.client_public_key,padding=conf.padding,algorithm=conf.signature_algorithm})
    if not pubKey then
      return false,err
    end
  end
end

return _M
