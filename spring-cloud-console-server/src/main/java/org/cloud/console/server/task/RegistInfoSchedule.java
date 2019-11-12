package org.cloud.console.server.task;

import org.cloud.console.server.service.ServiceInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/27<br>
 * <br>
 */
@Component
public class RegistInfoSchedule {
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private ServiceInfoService serviceInfoService;
    @Autowired
    private ThreadPoolTaskExecutor serviceTaskThread;
    @Autowired
    private ThreadPoolTaskExecutor healthTaskThread;

    @Scheduled(fixedRateString ="${mymonitor.refreshServiceSeconds}")
    public void getSchedule() {
        serviceTaskThread.execute(new RegistryInfoRefreshTask(discoveryClient,serviceInfoService));//更新服务列表信息
        serviceTaskThread.execute(new HealthInfoExecuteTask(healthTaskThread,serviceInfoService)); //进行 健康检查分发任务
    }
    @Scheduled(fixedRateString ="${mymonitor.compareAllServiceSeconds}")
    public void getAllService() {
        serviceTaskThread.execute(new AllServiceInfoRefreshTask(serviceInfoService));//从eureka 中获取所有微服务

    }

}
