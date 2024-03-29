local cjson = require "cjson"
local http = require('resty.http')

local zipkin_api_path = '/api/v1/spans'

local _M={}

function _M.prepare_trace(plugin_conf, req, ctx, status)
  local zipkin_trace = ctx.trace
  local headers = req.get_headers()

  if (not zipkin_trace) or (not zipkin_trace.sampled)
    or not (zipkin_trace.simulate_client or zipkin_trace.simulate_server) then
    return
  end

  ngx.update_time() -- update nginx cached timestamp
  local now = ngx.now()

  -- cjson will try to use scientific format, which we don't want
  local start_time = string.format("%.f", math.floor(1000000 * ngx.req.start_time()))
  local end_time = string.format("%.f", math.floor(1000000 * now))

  local duration = math.floor(1000000 * (now - ngx.req.start_time()))

  local formatted_trace = {
      {
      id = zipkin_trace.span_id,
      parentId = zipkin_trace.parent_span_id,
      traceId = zipkin_trace.trace_id,
      timestamp = start_time,
      name = ngx.var.request_uri,
      duration = duration,
      annotations = {}
    }
  }

  if zipkin_trace.simulate_client then

    local client_endpoint = {
      --serviceName = headers["x-consumer-username"] or ngx.var.remote_addr,
      serviceName="kong",
      ipv4 = ngx.var.remote_addr,
      port = ngx.var.remote_port,
    }
    -- client send
    table.insert(formatted_trace[1].annotations, {
      value = 'cs',
      timestamp = start_time,
      endpoint = client_endpoint
    })
    -- client receive
    table.insert(formatted_trace[1].annotations, {
      value = 'cr',
      timestamp = end_time,
      endpoint = client_endpoint
    })
  end

  if zipkin_trace.simulate_server then

    local server_endpoint = {
      serviceName = ctx.api.upstream_url
    }
    -- server receive
    table.insert(formatted_trace[1].annotations, {
      value = 'sr',
      timestamp = start_time,
      endpoint = server_endpoint
    })
    -- server send
    table.insert(formatted_trace[1].annotations, {
      value = 'ss',
      timestamp = end_time,
      endpoint = server_endpoint
    })
  end

  return formatted_trace

end

function _M.send_trace(plugin_conf, trace)
  ngx.log(ngx.DEBUG, 'sending trace')

  local encoded_trace = cjson.encode(trace)

  ngx.log(ngx.DEBUG, encoded_trace)

  local client = http.new()

  local res, err = client:request_uri(plugin_conf.zipkin_url .. zipkin_api_path,
    {
      method = "POST",
      body = encoded_trace,
      headers = {
        ["Content-Type"] = "application/json",
    }
  })
  if not res then
    ngx.log(ngx.ERR, err)
  elseif (res.status ~= 202) and (res.status ~= 200) then
    ngx.log(ngx.ERR, "Unexpected response from Zipkin: " .. res.status .. " - " .. res.reason .. ": " .. res.body)
  end
end
return _M
