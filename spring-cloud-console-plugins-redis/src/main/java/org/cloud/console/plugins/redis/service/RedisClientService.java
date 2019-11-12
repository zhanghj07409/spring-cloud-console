package org.cloud.console.plugins.redis.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.cloud.console.plugins.redis.config.RedisConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import java.util.*;

/**
 * 功能说明: Redis服务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@Component
public class RedisClientService {

    private static final Logger log = LoggerFactory.getLogger(RedisClientService.class);

    private static RedisConfig redisConfig;

    @Autowired
    public void setRedisConfig(RedisConfig redisConfig) {
        this.redisConfig = redisConfig;
    }

    public static Jedis getJedis(){
        Jedis jedis = new Jedis (redisConfig.getUrl().split(":")[0],Integer.parseInt(redisConfig.getUrl().split(":")[1]));
        if(redisConfig.getPassword()!=null&&!redisConfig.getPassword().isEmpty()){
            jedis.auth(redisConfig.getPassword());
        }
        return jedis;
    }

    public JSONObject getRedis() {
        Jedis jedis = RedisClientService.getJedis();
        jedis.connect();//连接

        JSONObject jsonObject = new JSONObject();
        List<Map> topicsList = new ArrayList<Map>();

        try {
            for(int i=0;i<100;i++) {
                if (jedis.select(i).equals("OK")) {
                    Map topicsMap = new HashMap<String, Object>();
                    Set<String> keyset = jedis.keys("*");
                    List<String> keys = new ArrayList<String>(keyset);
                    Collections.sort(keys);

                    topicsMap.put("parentId", "db" + i + " (" + keys.size() + ")");
                    topicsMap.put("parentmenuaddress", "/redisClient/listGlobalVariables");
                    List<Map> topicPartitionList = new ArrayList<Map>();
                    for (String key : keys) {
                        Map topicPartitionMap = new HashMap<String, Object>();
                        topicPartitionMap.put("childrenId", key);
                        topicPartitionMap.put("childrenAttribute1", key);
                        topicPartitionMap.put("childrenmenuaddress", "/redisClient/listRedisInfo?dbindex=" + i + "&key=" + key);

                        topicPartitionList.add(topicPartitionMap);
                    }

                    topicsMap.put("childrenRows", topicPartitionList);
                    topicsList.add(topicsMap);
                    log.info("db" + i + " (" + jedis.keys("*").size() + ")");
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            jedis.disconnect();//断开连接
        }
        JSONArray jsonArray = (JSONArray)JSONObject.toJSON(topicsList);
        jsonObject.put("rows",jsonArray);
        return jsonObject;
    }

    public JSONObject getRedisInfo(String dbindex,String key){

        if(key==null){
            key="";
        }
        if(dbindex==null){
            dbindex="0";
        }

        Jedis jedis = RedisClientService.getJedis();
        jedis.select(Integer.parseInt(dbindex));

        String keytype = jedis.type(key);
        JSONObject jsonObject = new JSONObject();
        try {
            List<Map> brokersInfoList = new ArrayList<Map>();

            if("string".equals(keytype)) {
                Map brokersInfoMap = new HashMap<String,Object>();
                brokersInfoMap.put(keytype,key);
                brokersInfoMap.put("value",jedis.get(key));
                brokersInfoList.add(brokersInfoMap);
            }else if("hash".equals(keytype)) {
                Map<String, String> map = jedis.hgetAll(key);//语句
                for(String mapkey:map.keySet()){
                    Map hashMap = new HashMap<String,Object>();
                    hashMap.put(keytype,mapkey);
                    hashMap.put("value",map.get(mapkey));
                    brokersInfoList.add(hashMap);
                }
            }

            Map brokersMap = new HashMap<String,Object>();
            Map recordKeysMap = new HashMap<String,Object>();
            recordKeysMap.put("recordId",keytype);
            recordKeysMap.put("recordAttribute1","value");
            brokersMap.put("recordKeys",recordKeysMap);
            brokersMap.put("recordRows",brokersInfoList);
            jsonObject.put("rows",brokersMap);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            jedis.disconnect();//断开连接
        }
        return jsonObject;
    }

}


