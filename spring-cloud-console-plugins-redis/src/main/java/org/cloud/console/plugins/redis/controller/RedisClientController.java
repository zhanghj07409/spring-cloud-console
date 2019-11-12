package org.cloud.console.plugins.redis.controller;

import com.alibaba.fastjson.JSONObject;
import org.cloud.console.plugins.redis.service.RedisClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能说明: Redis客户端<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@RestController
@RequestMapping("/redisClient")
public class RedisClientController {

    @Autowired
    private RedisClientService redisAdminClientService;

    @RequestMapping(value = "/listRedis")
    public JSONObject listRedis() {
        return redisAdminClientService.getRedis();
    }

    @RequestMapping(value = "/listRedisInfo")
    public JSONObject listRedisInfo(String dbindex,String key) {
        return redisAdminClientService.getRedisInfo(dbindex,key);
    }

}
