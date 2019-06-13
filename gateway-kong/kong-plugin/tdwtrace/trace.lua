local cjson = require "cjson"
local log_file = require "logging.rolling_file"

local max_size       = 1024 * 1024*10  --10kb
local max_index      = 5
local total_log_size = max_size * max_index --more than needed because of the log pattern
local log_filename   = "/data/logs/sgw/test.log"
local logger         = log_file(log_filename, max_size, max_index)

local _M={}


local function random_sample(plugin_conf)
  if plugin_conf.sample_once_every_n_requests == 0 then
    return false
  elseif plugin_conf.sample_once_every_n_requests == 1 then
    return true
  else
   -- return math.random(1, plugin_conf.sample_once_every_n_requests) == 1
    return true
  end
end



local function random_string_of_len(length)
  local value = ""
  for i=1,length,1 do
    value = value..string.format("%x", math.random(0, 15))
  end
  return value
end

local function inject(req, trace)
  req.set_header("X-B3-TraceId", trace.trace_id)
  req.set_header("X-B3-SpanId", trace.span_id)
  req.set_header("X-B3-Sampled", tostring(trace.sampled))
  if trace.parent_span_id then
    req.set_header("X-B3-ParentSpanId", tostring(trace.parent_span_id))
  end
end


--获取请求数据
function _M.req()

  local data={}
  data["type"]="SPRING_REQ"
  data.uri=ngx.var.request_uri
  data.httpMethod=ngx.req.get_method()
  data.header=cjson.encode(ngx.req.get_headers())
  ngx.req.read_body()
  data.param=cjson.encode(ngx.req.get_post_args())
  data.body= ngx.req.get_body_data()
  return cjson.encode(data)
end


--获取响应数据
function _M.resp()
  local data={}
  data["type"]="SPRING_RESP"
  ngx.update_time() 
  local now = ngx.now()
  local start_time = string.format("%.f", math.floor(1000000 * ngx.req.start_time()))
  local end_time = string.format("%.f", math.floor(1000000 * now))

  local tm = math.floor(1000000 * (now - ngx.req.start_time()))
   --转换为毫秒
  data.time=string.format("%.f",math.floor(tm/1000))

  data.uri=ngx.var.request_uri
  data.status=ngx.status
  data.error=""
  data.requestBody=ngx.ctx.buf
  return cjson.encode(data)
end

--记录日志
function _M.log(t)

local trace=ngx.ctx.trace
local serviceinfo="[swg,"..trace.trace_id..","..trace.span_id..","..tostring(trace.exportable).."]"
local pid=ngx.worker.pid()
local threadinfo="[ nginx kong]"
local tag="---"
local currentline=debug.getinfo(1).currentline
local src=debug.getinfo(1).short_src
local tag1=":"
local perf="PerfLog_0:"
local data=""
 if t == 1 then
   data=_M.resp()
   perf="PerfLog_1:"
   ngx.ctx.len=string.len(ngx.ctx.buf)
   ngx.arg[1]= ngx.ctx.buf
   ngx.arg[2]=true
   ngx.ctx.buf=nil

 else
   data=_M.req()
 end
local info={serviceinfo,pid,tag,threadinfo,src,tag1,perf..data}
ngx.update_time()
local now=ngx.now()
local tm=os.date("%Y-%m-%d %H:%M:%S",now)..string.sub(now,string.find(now,"%.%d+$"))
logger:info(table.concat(info," "),{time=tm})

end

--请求跟踪
function _M.trace_req(plugin_conf, req, ctx)
  local headers = req.get_headers()
  local sampled = (headers['X-B3-Sampled'] and (headers['X-B3-Sampled'] == 1)) 
    or (headers['X-B3-Flags'] and (headers['X-B3-Flags'] == 1)) 

  sampled = sampled or random_sample(plugin_conf)

  local trace = nil 

  if headers['X-B3-TraceId'] and headers['X-B3-SpanId'] then
     trace = {
      trace_id = headers['X-B3-TraceId'],
      span_id = headers['X-B3-SpanId'],
      parent_span_id = headers['X-B3-ParentSpanId'],
      simulate_client = false,
      simulate_server = plugin_conf.simulate_server,
      sampled = sampled,
      exportable = plugin_conf.exportable 
    }
  elseif sampled then
    trace = {
      trace_id = random_string_of_len(16),
      span_id = random_string_of_len(16),
      simulate_client = true,
      simulate_server = plugin_conf.simulate_server,
      sampled = sampled,
      exportable = plugin_conf.exportable
    }
  end

  if sampled and not plugin_conf.simulate_server then
    inject(req, trace)
  end

  ctx.trace = trace
  _M.log(0)
end

--响应跟踪
function _M.trace_resp()
 _M.log(1)
end

return _M
