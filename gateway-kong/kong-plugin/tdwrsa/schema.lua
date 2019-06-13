local function format(str,mod,start_tag,end_tag)
   local len=string.len(str)
   local i=len % mod
   local m=0
   if i > 0 then
     m= (len-i)/mod + 1
   else
     m=len/mod
   end

   local temp=start_tag.."\n"
   for j=1,m do
    local s = mod * (j-1) +1
    local e = mod * j
    if j < m then
       temp=temp..string.sub(str,s,e).."\n"
    else
       temp=temp..string.sub(str,s,e)
    end
   end
   temp=temp.."\n"..end_tag
   return temp
end


local function format_public_key(value,config)
  local start_tag = "-----BEGIN PUBLIC KEY-----"
  local end_tag   = "-----END PUBLIC KEY-----"
  --[[if string.find(value,start_tag) or string.find(value,end_tag) then
    return false,"公钥格式不正确"
  end--]]
  value=string.gsub(value,"%-%-%-%-%-BEGIN PUBLIC KEY%-%-%-%-%-","")
  value=string.gsub(value,"%-%-%-%-%-END PUBLIC KEY%-%-%-%-%-","")
  value=string.gsub(value,"\n","")
  return true,nil,{client_public_key=format(value,64,start_tag,end_tag)}
end

local function format_private_key(value,config)
  local start_tag = "-----BEGIN RSA PRIVATE KEY-----"
  local end_tag   = "-----END RSA PRIVATE KEY-----"
  --[[if string.find(value,start_tag) or string.find(value,end_tag) then
   return false,"私钥格式不正确"
  end--]]


  value=string.gsub(value,"%-%-%-%-%-BEGIN RSA PRIVATE KEY%-%-%-%-%-","")
  value=string.gsub(value,"%-%-%-%-%-END RSA PRIVATE KEY%-%-%-%-%-","")  
  value=string.gsub(value,"\n","")
  return true,nil,{server_private_key=format(value,64,start_tag,end_tag)}

end


return {
  no_consumer = true,
  fields = {
   server_private_key ={type = "string", required = true,func = format_private_key},
   client_public_key  ={type = "string", required = true,func = format_public_key},
   delay = {type = "boolean", default = true}
  }
}
