package org.cloud.console.server.task;

import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import org.cloud.console.server.entity.ServiceInfoEntity;
import org.cloud.console.server.service.ServiceInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class AllServiceInfoRefreshTask implements Runnable{
    Logger logger=LoggerFactory.getLogger(AllServiceInfoRefreshTask.class);

    private ServiceInfoService serviceInfoService;
    public AllServiceInfoRefreshTask(ServiceInfoService infoService ){
        this.serviceInfoService=infoService;
    }
    @Override
    public void run() {
        logger.info("refresh all Service List ");
        try {
            serviceInfoService.refreshAllService();
        } catch (Exception e) {
            logger.error("刷新所有微服务列表失败",e);
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
