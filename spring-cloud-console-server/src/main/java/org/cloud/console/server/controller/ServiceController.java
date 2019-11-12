package org.cloud.console.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import org.apache.commons.lang.StringUtils;
import org.cloud.console.server.entity.ServiceInfoEntity;
import org.cloud.console.server.service.ServiceInfoService;
import org.cloud.console.server.task.AllServiceInfoRefreshTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@RestController
@RequestMapping("/service")
public class ServiceController {
    Logger logger=LoggerFactory.getLogger(ServiceController.class);
    @Autowired
    private ServiceInfoService serviceInfoService;
    @Autowired
    private EurekaClient eurekaClient;
    @Autowired
    private EurekaHttpClient eurekaHttpClient;
    @Autowired
    private ThreadPoolTaskExecutor serviceTaskThread;
    @RequestMapping("/updateStatus")
    public String updateServerProperties(String serviceId,InstanceInfo.InstanceStatus status){
        String result= null;
        try {
            ServiceInfoEntity entity=serviceInfoService.getServiceInfo(serviceId);
            InstanceInfo instanceInfo=entity.getInstanceInfo();
            instanceInfo.setLastDirtyTimestamp(System.currentTimeMillis());
            eurekaHttpClient.statusUpdate(instanceInfo.getAppName(),instanceInfo.getInstanceId(),status,instanceInfo);
            serviceTaskThread.execute(new AllServiceInfoRefreshTask(serviceInfoService));
        } catch (NullPointerException e) {
            logger.error("修改配置异常",e);
            return  "fail";
        }catch (Exception e) {
            logger.error("修改配置异常",e);
            return  "fail";
        }
        return "success";
    }

    @RequestMapping("/getAppNameList")
    public Map<String,Integer> getAppNameList(){
        Map<String,Integer> appSet=new HashMap<>();
        Map<String, Map<String, ServiceInfoEntity>> onlineAppMap=serviceInfoService.getCopyOnlineAppMap();
        for(String key:onlineAppMap.keySet()){
            appSet.put(key,onlineAppMap.get(key).size());
        }
        Map<String, Map<String, ServiceInfoEntity>> offlineAppMap=serviceInfoService.getCopyOfflineAppMap();
        for(String key:offlineAppMap.keySet()){
            Integer size=appSet.get(key);
            if(size!=null){
                appSet.put(key,offlineAppMap.get(key).size()+size);
            }else{
                appSet.put(key,offlineAppMap.get(key).size());
            }
        }
        return  appSet;
    }

    @RequestMapping("/getServiceList")
    public Map<String,List<Map<String,Object>>> getAppNameList(String appName){
        Map<String,List<Map<String,Object>>> serviceMap=new HashMap();
        Map<String, Map<String, ServiceInfoEntity>> onlineAppMap=serviceInfoService.getCopyOnlineAppMap();
        Map<String, Map<String, ServiceInfoEntity>> offlineAppMap=serviceInfoService.getCopyOfflineAppMap();
        if(StringUtils.isNotBlank(appName)){
            List<Map<String,Object>> serviceList =getAllServiceList(appName,onlineAppMap);
            serviceList.addAll(getAllServiceList(appName,offlineAppMap));
            serviceMap.put(appName,serviceList);
        }else{
            for(String key:onlineAppMap.keySet()){
                serviceMap.put(key,getAllServiceList(key,onlineAppMap));
            }
            for(String key:offlineAppMap.keySet()){
                List<Map<String,Object>> serviceList = serviceMap.get(key);
                if(serviceList==null){
                    serviceList=new ArrayList<>();
                }
                serviceList.addAll(getAllServiceList(key,offlineAppMap));
            }
        }
        return  serviceMap;
    }
    @RequestMapping("/getServiceInfo")
    public String getServiceInfo(String serviceId){
        return JSONObject.toJSONString(serviceInfoService.getServiceInfo(serviceId));
    }
    private List<Map<String,Object>> getAllServiceList(String appName,Map<String, Map<String, ServiceInfoEntity>> appMap){
        List<Map<String,Object>> serviceList =new ArrayList<>();
        Map<String, ServiceInfoEntity> serviceMap=appMap.get(appName);
        if(serviceMap!=null){
            for(String key:serviceMap.keySet()){
                ServiceInfoEntity serviceInfoEntity=serviceMap.get(key);
                InstanceInfo instanceInfo=serviceInfoEntity.getInstanceInfo();
                if(instanceInfo!=null){
                    Map<String,Object> map=new HashMap<>();
                    map.put("instanceId",instanceInfo.getInstanceId());
                    map.put("status",instanceInfo.getStatus());
                    serviceList.add(map);
                }
            }
        }
        return serviceList;
    }
}
