package org.cloud.console.server.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import org.apache.http.conn.HttpHostConnectException;
import org.cloud.console.server.entity.ServiceInfoEntity;
import org.cloud.console.server.service.ServiceInfoService;
import org.cloud.console.server.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.time.LocalDateTime;

/**
 * 功能说明: 进行健康检查<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/14<br>
 * <br>
 */
public class HealthInfoRefreshTask implements Runnable{
    Logger logger=LoggerFactory.getLogger(HealthInfoRefreshTask.class);
    private ServiceInfoService serviceInfoService;
    private ServiceInfoEntity entity;
    public HealthInfoRefreshTask(ServiceInfoService infoService,ServiceInfoEntity entity){
        this.serviceInfoService=infoService;
        this.entity=entity;
    }
    @Override
    public void run() {
        logger.info("health refresh tash start");
//        for(ServiceInfoEntity entity:serviceInfoEntityCollection){
            String url=null;
            InstanceInfo instanceInfo=new InstanceInfo(entity.getInstanceInfo());
            try {
                ServiceInfoEntity newEntity=new ServiceInfoEntity(instanceInfo);
                url=instanceInfo.getHealthCheckUrl();
                String result=HttpClientUtil.getHealth(url);
                JSONObject health=JSON.parseObject(result);
                newEntity.setHealthInfo(health);
                serviceInfoService.refreshHealthInfo(newEntity);
            } catch (HttpClientErrorException e){
                ServiceInfoEntity newEntity=new ServiceInfoEntity(instanceInfo);
                String msg=null;
                if(e.getStatusCode().is4xxClientError()){
                    msg="health check url 不可访问\turl:"+url;

                }else if(e.getStatusCode().is5xxServerError()){
                    msg="health check service 异常\turl:"+url;
                }else{
                    msg="健康检查异常";
                }
                logger.error(msg,e);
                JSONObject health=new JSONObject();
                health.put("status",InstanceInfo.InstanceStatus.DOWN);
                health.put("error",e);
                health.put("msg",msg);
                health.put("timeStamp",LocalDateTime.now());
                newEntity.setHealthInfo(health);
                serviceInfoService.healthCheckError(newEntity);
            } catch(HttpHostConnectException e) {
                ServiceInfoEntity newEntity=new ServiceInfoEntity(instanceInfo);
                String msg="服务连接不上";
                logger.error(msg,e);
                JSONObject health=new JSONObject();
                health.put("status",InstanceInfo.InstanceStatus.DOWN);
                health.put("error",e);
                health.put("msg",msg);
                health.put("timeStamp",LocalDateTime.now());
                newEntity.setHealthInfo(health);
                serviceInfoService.healthCheckError(newEntity);
                logger.error(msg,e);
            }catch(Exception e) {
                logger.error("更新健康状态异常",e);
            }

//        }
    }

}
