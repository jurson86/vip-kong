package com.tuandai.ms.ar.service.impl;

import com.tuandai.ms.ar.exception.BaseRuntimeException;
import com.tuandai.ms.ar.model.user.KongUserGroup;
import com.tuandai.ms.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取组
 *
 * @author wanggang
 * @createTime 2018-08-23 17:48:00
 */
@Service
public class KongGroupService {

    private final static Logger logger = LoggerFactory.getLogger(KongGroupService.class);

    private static final String src="local function host(group)\r\n" + 
    		"  local hosts = redis.call('hgetall',group)\r\n" + 
    		"  local temp = {}\r\n" + 
    		"  if hosts and (#hosts) >= 2 then\r\n" + 
    		"    for i = 1,#(hosts),2 do\r\n" + 
    		"      local host = hosts[i]\r\n" + 
    		"      local ok = redis.call('exists','kong_time_'..group..'_'..host)\r\n" + 
    		"      if ok == 1 then\r\n" + 
    		"        if string.match(host,':(.*)') == nil then\r\n" + 
    		"           table.insert(temp,host..':8001') \r\n" + 
    		"        elseif #string.match(host,':(.*)') == 0 then\r\n" + 
    		"           table.insert(temp,host..'8001') \r\n" + 
    		"        else\r\n" + 
    		"           table.insert(temp,host) \r\n" + 
    		"        end\r\n" + 
    		"      else\r\n" + 
    		"        redis.call('hdel',group,host)\r\n" + 
    		"      end\r\n" + 
    		"    end\r\n" + 
    		"\r\n" + 
    		"    if (#temp) > 0 then\r\n" + 
    		"      return true,temp\r\n" + 
    		"    else\r\n" + 
    		"      return false,nil\r\n" + 
    		"    end\r\n" + 
    		"\r\n" + 
    		"  else\r\n" + 
    		"    return false,nil\r\n" + 
    		"  end\r\n" + 
    		"end\r\n" + 
    		"\r\n" + 
    		"local function get_groups()\r\n" + 
    		"  local kongGroup = 'kong-groups'\r\n" + 
    		"  local result = {}\r\n" + 
    		"\r\n" + 
    		"  local groups = redis.call('hgetall',kongGroup)\r\n" + 
    		"  if groups and (#groups) >= 2 then\r\n" + 
    		"     local temp = {}\r\n" + 
    		"     for i = 1,#(groups),2 do\r\n" + 
    		"        local group = groups[i]\r\n" + 
    		"        local res,hosts = host(group)\r\n" + 
    		"        if res then\r\n" + 
    		"          local groupkey = string.match(group,\"kong_(.+)\")\r\n" + 
    		"          temp[groupkey] = hosts\r\n" + 
    		"        else\r\n" + 
    		"          redis.call('hdel',kongGroup,group)\r\n" + 
    		"        end\r\n" + 
    		"     end\r\n" + 
    		"\r\n" + 
    		"      if temp then\r\n" + 
    		"        for g,h in pairs(temp) do\r\n" + 
    		"          table.insert(result,g)\r\n" + 
    		"          if type(h) == 'table' then\r\n" + 
    		"            table.insert(result,table.concat(h,','))\r\n" + 
    		"          else\r\n" + 
    		"            table.insert(result,h)\r\n" + 
    		"          end\r\n" + 
    		"        end\r\n" + 
    		"     end\r\n" + 
    		"\r\n" + 
    		"  end\r\n" + 
    		"\r\n" + 
    		"  return result\r\n" + 
    		"end\r\n" + 
    		"\r\n" + 
    		"return get_groups()";

    @Autowired
    private RedisTemplate redisTemplate;

    private static DefaultRedisScript<List> redisScript = null;
    static{
        redisScript = new DefaultRedisScript<List>();
        redisScript.setResultType(List.class);
        redisScript.setScriptText(src);
    }

    public Map<String,String> groups() {
        List<String> keys = new ArrayList<>();
        List result = (List) redisTemplate.execute(redisScript, keys);
        logger.info("组列表{}",result.toString());
        Map<String,String> groups = new HashMap<>();
        for(int i=0;i<result.size();i+=2) {
            groups.put(result.get(i).toString(), result.get(i+1).toString());
        }
        return groups;
    }

    public List<KongUserGroup> groupList(){
		Map<String,String> allGroups = groups();
		List<KongUserGroup> groupList = new ArrayList<>();
		for(Map.Entry<String,String> group : allGroups.entrySet()){
			String groupName = group.getKey();
			KongUserGroup kongUserGroup = new KongUserGroup();
			kongUserGroup.setKongGroup(groupName);
			groupList.add(kongUserGroup);
		}
		return groupList;
	}

    /**
     * 获取组中服务，用于获取 upstream
     * @param groupName
     * @return
     */
    public String getServerByGroupName(String groupName){
        Map<String,String> groups = groups();
        String servers = groups.get(groupName);
        if(StringUtils.isEmpty(servers)){
            throw new BaseRuntimeException(groupName + "组不存在或者没有kong服务");
        }
        return servers.split(",")[0];
    }


}
