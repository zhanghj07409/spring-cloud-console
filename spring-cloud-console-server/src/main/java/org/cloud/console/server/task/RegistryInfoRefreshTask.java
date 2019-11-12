package org.cloud.console.server.task;

import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import org.cloud.console.server.entity.ServiceInfoEntity;
import org.cloud.console.server.service.ServiceInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能说明: 服务状态更新<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/14<br>
 * <br>
 */
public class RegistryInfoRefreshTask implements Runnable{
    Logger logger=LoggerFactory.getLogger(RegistryInfoRefreshTask.class);
    private DiscoveryClient discoveryClient;
    private ServiceInfoService serviceInfoService;
    public RegistryInfoRefreshTask(DiscoveryClient client, ServiceInfoService infoService ){
        this.discoveryClient=client;
        this.serviceInfoService=infoService;
    }
    @Override
    public void run() {
        logger.info("service info task start");
        try {
            Map<String,Map<String,ServiceInfoEntity>> appMap=serviceInfoService.getAllMap();
            List<String> serviceList= discoveryClient.getServices();
            //对比上次心跳的服务列表 是否比本次服务列表多
            for(String appName:appMap.keySet()){
                List<ServiceInstance> list=discoveryClient.getInstances(appName);
                if(serviceList.contains(appName.toLowerCase())||serviceList.contains(appName.toUpperCase())){//上次的服务组本次还在
                    Map<String,ServiceInfoEntity> lastServiceMap=appMap.get(appName);
                    for(String serviceName:lastServiceMap.keySet()){
                        List<String> serviceNameList=getServicename(list);
                        if(!serviceNameList.contains(serviceName.toLowerCase())&&!serviceNameList.contains(serviceName.toUpperCase())){//上次的微服务本次不存在
                            InstanceInfo offLineInstance=new InstanceInfo(lastServiceMap.get(serviceName).getInstanceInfo());
                            logger.info("本次心跳 有服务不存在:"+JSONObject.toJSONString(offLineInstance));
                            offLineInstance.setStatus(InstanceInfo.InstanceStatus.DOWN);
                            serviceInfoService.refreshLocalServiceMap(offLineInstance);
                        }
                    }
                }else{//上次的服务组本次不在
                    Map<String,ServiceInfoEntity> lastServiceMap=appMap.get(appName);
                    for(String serviceName:lastServiceMap.keySet()){
                        InstanceInfo offLineInstance=new InstanceInfo(lastServiceMap.get(serviceName).getInstanceInfo());
                        offLineInstance.setStatus(InstanceInfo.InstanceStatus.DOWN);
                        serviceInfoService.refreshLocalServiceMap(offLineInstance);
                    }
                }
            }
            //将本次服务列表更新数据写入列表中
            for(String sevice:serviceList){
                List<ServiceInstance> list=discoveryClient.getInstances(sevice);
                serviceInfoService.registryList(list);
            }
        } catch (Exception e) {
            logger.error("获取本地服务列表失败",e);
        }
    }

    private List<String> getServicename(List<ServiceInstance> list){

        List<String> serviceNameList=new ArrayList<>();
        for(ServiceInstance serviceInstance:list){
            InstanceInfo instanceInfo=((EurekaDiscoveryClient.EurekaServiceInstance)serviceInstance).getInstanceInfo();
            serviceNameList.add(instanceInfo.getInstanceId());
        }
        return  serviceNameList;
    }
}
