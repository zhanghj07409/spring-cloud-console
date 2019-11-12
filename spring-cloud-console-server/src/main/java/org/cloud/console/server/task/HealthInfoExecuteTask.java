package org.cloud.console.server.task;

import org.cloud.console.server.entity.ServiceInfoEntity;
import org.cloud.console.server.service.ServiceInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * 功能说明: 分发健康检查任务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/14<br>
 * <br>
 */
public class HealthInfoExecuteTask implements Runnable{
    Logger logger=LoggerFactory.getLogger(HealthInfoExecuteTask.class);
    private ServiceInfoService serviceInfoService;
    private ThreadPoolTaskExecutor taskExecutor;
    public HealthInfoExecuteTask(ThreadPoolTaskExecutor taskExecutor, ServiceInfoService infoService ){
        this.serviceInfoService=infoService;
        this.taskExecutor=taskExecutor;
    }
    @Override
    public void run() {
        logger.info("health task start");
        try {
            Map<String,Map<String,ServiceInfoEntity>> onlineAppMap=serviceInfoService.getCopyOnlineAppMap();
            for(String app:onlineAppMap.keySet()){
                Map<String,ServiceInfoEntity> map=onlineAppMap.get(app);
                Collection<ServiceInfoEntity> collection=map.values();
                Iterator<ServiceInfoEntity> iterator=collection.iterator();
                while (iterator.hasNext()){
                    taskExecutor.execute(new HealthInfoRefreshTask(serviceInfoService,iterator.next()));
                }
            }
        } catch (Exception e) {
            logger.error("更新健康状态异常",e);
        }
    }

}
