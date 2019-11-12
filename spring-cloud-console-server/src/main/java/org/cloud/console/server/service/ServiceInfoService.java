package org.cloud.console.server.service;

import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.discovery.shared.Applications;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.EurekaHttpResponse;
import org.apache.commons.lang.StringUtils;
import org.cloud.console.server.config.MyMonitorConfig;
import org.cloud.console.server.entity.ServiceInfoEntity;
import org.cloud.console.server.enums.NotifyTypeEnum;
import org.cloud.console.server.enums.WebsocketTypeEnum;
import org.cloud.console.server.vo.NotifyMsgVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/15<br>
 * <br>
 */
@Service
public class ServiceInfoService {

    private Logger logger=LoggerFactory.getLogger(ServiceInfoService.class);
    private Map<String,Map<String,ServiceInfoEntity>> onlineAppMap=new HashMap<>();//所有在线服务的列表
    private Map<String,Map<String,ServiceInfoEntity>> offLineAppMap=new HashMap<>();//所有离线服务的列表
    private Map<String,String> serviceIdAppnamemap=new HashMap<>();//所有离线服务的列表

    @Autowired
    private NotifiyService notifiyService;
    @Autowired
    private DiscoveryClient discoveryClient;
    @Autowired
    private MyMonitorConfig myMonitorConfig;
    @Autowired
    private EurekaHttpClient eurekaHttpClient;

    public void registryList(List<ServiceInstance> serviceList){
        try {
            for(ServiceInstance serviceInstance:serviceList){
                InstanceInfo instanceInfo=((EurekaDiscoveryClient.EurekaServiceInstance)serviceInstance).getInstanceInfo();
                refreshLocalServiceMap(instanceInfo);
            }
        } catch (Exception e) {
            logger.error("registryList erro",e);
        }
    }

    /**
     * 刷新服务列表信息  并发送相关通知
     * @param instanceInfo
     */
    public void refreshLocalServiceMap(InstanceInfo instanceInfo){
        serviceIdAppnamemap.put(instanceInfo.getInstanceId(),instanceInfo.getAppName());
        logger.info("refresh instanceInfo:"+JSONObject.toJSONString(instanceInfo));
        InstanceInfo newInstanceInfo=new InstanceInfo(instanceInfo);
        notifyServiceInstanceInfo(instanceInfo);
        String appName=newInstanceInfo.getAppName();
        String serviceId=instanceInfo.getInstanceId();
        if(InstanceInfo.InstanceStatus.UP.equals(newInstanceInfo.getStatus())){//服务上线状态
            Map<String,ServiceInfoEntity> onLinelocalServiceMap=onlineAppMap.get(appName);
            if(onLinelocalServiceMap==null){//更新在线服务列表数据
                onLinelocalServiceMap=new HashMap<String,ServiceInfoEntity>();
                onlineAppMap.put(appName,onLinelocalServiceMap);
            }
            ServiceInfoEntity serviceInfoEntity=onLinelocalServiceMap.get(serviceId);
            if(serviceInfoEntity==null){
                serviceInfoEntity=new ServiceInfoEntity(newInstanceInfo);
                onLinelocalServiceMap.put(serviceId,serviceInfoEntity);
            }else {
                serviceInfoEntity.setInstanceInfo(newInstanceInfo);
            }
            Map<String,ServiceInfoEntity> offLinelocalServiceMap=offLineAppMap.get(appName);
            if(offLinelocalServiceMap!=null){//移除该服务在 不在线服务列表中内容
                offLinelocalServiceMap.remove(serviceId);
            }
        }else{//服务其他状态 可以认为是离线
            Map<String,ServiceInfoEntity> onLinelocalServiceMap=onlineAppMap.get(appName);
            ServiceInfoEntity oldService=null;
            if(onLinelocalServiceMap!=null){//移除在线服务列表数据
                oldService=onLinelocalServiceMap.remove(serviceId);
            }
            Map<String,ServiceInfoEntity> offLinelocalServiceMap=offLineAppMap.get(appName);
            if(offLinelocalServiceMap==null){//更新不在线服务列表中内容
                offLinelocalServiceMap=new HashMap<String,ServiceInfoEntity>();
                offLineAppMap.put(appName,offLinelocalServiceMap);
            }
            if(oldService==null){
                oldService=new ServiceInfoEntity(newInstanceInfo);
            }
            oldService.setInstanceInfo(newInstanceInfo);
            offLinelocalServiceMap.put(serviceId,oldService);
        }
    }
    /**
     * 刷新健康检查信息
     * @param entity
     */
    public void refreshHealthInfo(ServiceInfoEntity entity){
        logger.info("refresh Health:"+entity.getHealthInfo().toString());
        InstanceInfo instanceInfo=entity.getInstanceInfo();
        JSONObject health=entity.getHealthInfo();
        String app=instanceInfo.getAppName();
        String instanceId=instanceInfo.getInstanceId();
        notifyHealth(entity);
        Map<String,ServiceInfoEntity> lastServiceMap=onlineAppMap.get(app);
        if(lastServiceMap!=null){
            ServiceInfoEntity lastServiceinfo=lastServiceMap.get(instanceId);
            if(lastServiceinfo!=null){
                lastServiceinfo.setHealthInfo(health);
            }
        }
    }
    /**
     * 健康检查异常发生通知
     * @param entity
     */
    public void healthCheckError(ServiceInfoEntity entity){
        logger.info("refresh Health:"+entity.getHealthInfo().toString());
        InstanceInfo instanceInfo=entity.getInstanceInfo();
        JSONObject health=entity.getHealthInfo();
        String app=instanceInfo.getAppName();
        String instanceId=instanceInfo.getInstanceId();
        Map<String,ServiceInfoEntity> lastServiceMap=onlineAppMap.get(app);
        if(lastServiceMap!=null){
            ServiceInfoEntity lastServiceinfo=lastServiceMap.get(instanceId);
            if(lastServiceinfo!=null){
                JSONObject lastHealth=lastServiceinfo.getHealthInfo();
                if(lastHealth!=null){
                    if(!health.get("status").equals(lastHealth.get("status"))){
                        sendHealthStatusChangeMsg(instanceId,lastHealth,health);
                    }
                    lastServiceinfo.setHealthInfo(health);
                }else{
                    lastServiceinfo.setHealthInfo(health);
                    sendHealthStatusChangeMsg(instanceId,lastHealth,health);
                }

            }
        }


    }
    /**
     * 判断服务状态 并发送相关通知
     * @param instanceInfo
     */
    public void notifyServiceInstanceInfo(InstanceInfo instanceInfo){
        InstanceInfo localInstaceInfo=new InstanceInfo(instanceInfo);
        String appName=localInstaceInfo.getAppName();
        String serviceId=instanceInfo.getInstanceId();
        Map<String,ServiceInfoEntity> onLinelocalServiceMap=onlineAppMap.get(appName);
        Map<String,ServiceInfoEntity> offLinelocalServiceMap=offLineAppMap.get(appName);
        InstanceInfo onlineInfo=null;
        InstanceInfo offlineInfo=null;
        if(onLinelocalServiceMap!=null){
            ServiceInfoEntity entity=onLinelocalServiceMap.get(serviceId);
            if(entity!=null){
                onlineInfo=entity.getInstanceInfo();
            }
        }
        if(offLinelocalServiceMap!=null){
            ServiceInfoEntity entity=offLinelocalServiceMap.get(serviceId);
            if(entity!=null){
                offlineInfo=entity.getInstanceInfo();
            }
        }
        if(onlineInfo!=null){
            if(!localInstaceInfo.getStatus().equals(onlineInfo.getStatus())){
                sendServiceStatusChangeMsg(onlineInfo,localInstaceInfo);
            }else{
                //状态无变化
//                notifiyService.sendMsg(localInstaceInfo.getInstanceId()+"status:"+onlineInfo.getStatus()+"-->"+localInstaceInfo.getStatus(),"status no change");
            }
        }
        if(offlineInfo!=null){
            if(!localInstaceInfo.getStatus().equals(offlineInfo.getStatus())){
                sendServiceStatusChangeMsg(offlineInfo,localInstaceInfo);
//                notifiyService.sendMsg(localInstaceInfo.getInstanceId()+"status:"+offlineInfo.getStatus()+"-->"+localInstaceInfo.getStatus(),"status change");
            }else{
                //状态无变化
//                notifiyService.sendMsg(localInstaceInfo.getInstanceId()+"status:"+offlineInfo.getStatus()+"-->"+localInstaceInfo.getStatus(),"status no change");
            }
        }if(onlineInfo==null&& offlineInfo==null){
            sendServiceRegistMsg(localInstaceInfo);
        }
    }
    /**
     * 判断服务状态 并发送相关通知
     * @param entity
     */
    public void notifyHealth(ServiceInfoEntity entity){
        InstanceInfo nowIns=entity.getInstanceInfo();
        JSONObject nowHealth=entity.getHealthInfo();
//        InstanceInfo oldIns=null;
        JSONObject oldHealth=null;
        ServiceInfoEntity oldServiceInfo=null;
        int failTime =0;
        Map<String,ServiceInfoEntity> oldMap=onlineAppMap.get(nowIns.getAppName());
        if(oldMap!=null){
            oldServiceInfo=oldMap.get(nowIns.getInstanceId());
            if(oldServiceInfo!=null){
//                oldIns=oldServiceInfo.getInstanceInfo();
                oldHealth=oldServiceInfo.getHealthInfo();
                failTime=oldServiceInfo.getHealthFailTime();
            }
        }
        JSONObject dif=compareHealth(oldHealth,nowHealth);
        if(!dif.isEmpty()){
            if(myMonitorConfig.getHealthFialNotifyTime()<0||(myMonitorConfig.getHealthFialNotifyTime()>0&&failTime<myMonitorConfig.getHealthFialNotifyTime())){
//                notifiyService.sendMsg("id"+nowIns.getInstanceId()+"\tdif"+dif.toJSONString(),"health change");
                sendHealthStatusChangeMsg(nowIns.getInstanceId(),oldHealth,nowHealth);
            }
            oldServiceInfo.setHealthFailTime(failTime+1);
        }else{
            if(oldServiceInfo!=null){
                oldServiceInfo.setHealthFailTime(0);
            }
        }
    }
    public JSONObject compareHealth(JSONObject oldHealth,JSONObject newHealth){
        JSONObject dif=new JSONObject();
        String[] models=myMonitorConfig.getModel();
        for(String model:models){
            String[] keys=model.split("\\.");
            JSONObject oldTmp=oldHealth;
            JSONObject newTmp=newHealth;
            for(String key:keys){
                if(oldTmp!=null){
                    oldTmp= (JSONObject) oldTmp.get(key);
                }
                if(newTmp!=null){
                    newTmp= (JSONObject) newTmp.get(key);
                }
            }
            String oldStatus="";
            String newStatus="";
            if(oldTmp!=null){
                oldStatus=oldTmp.getString("status");
            }
            if(newTmp!=null){
                newStatus=newTmp.getString("status");
            }
            if(oldStatus!=null){
                if(!oldStatus.equalsIgnoreCase(newStatus)){
                    switch (myMonitorConfig.getHealthChangeNotify()){
                        case ALL:{
                            dif.put(model,oldStatus+"->"+newStatus);
                            dif.put("healthChangeNotify",oldStatus+"->"+newStatus);
                        }break;
                        case DOWN_TO_UP:{
                            if(oldStatus.equalsIgnoreCase("down")){
                                dif.put(model,oldStatus+"->"+newStatus);
                                dif.put("healthChangeNotify",oldStatus+"->"+newStatus);
                            }break;
                        }
                        case UP_TO_DOWN:{
                            if(oldStatus.equalsIgnoreCase("up")){
                                dif.put(model,oldStatus+"->"+newStatus);
                                dif.put("healthChangeNotify",oldStatus+"->"+newStatus);
                            }break;
                        }
                    }
                }
            }else{
                if(newStatus!=null){
                    dif.put(model,oldStatus+"->"+newStatus);
                    dif.put("healthChangeNotify",oldStatus+"->"+newStatus);
                }
            }
        }
        if(oldHealth!=null){
            String oldStatus=oldHealth.getString("status");
            String newStatus="";
            if(newStatus!=null){
                newStatus=newHealth.getString("status");
            }
            if(oldStatus!=null){
                if(!oldStatus.equalsIgnoreCase(newStatus)){
                    switch (myMonitorConfig.getHealthChangeNotify()){
                        case ALL:dif.put("status",oldStatus+"->"+newStatus);break;
                        case DOWN_TO_UP:{
                            if(oldStatus.equalsIgnoreCase("down")){
                                dif.put("status",oldStatus+"->"+newStatus);
                            }break;
                        }
                        case UP_TO_DOWN:{
                            if(oldStatus.equalsIgnoreCase("up")){
                                dif.put("status",oldStatus+"->"+newStatus);
                            }break;
                        }
                    }
                }
            }else{
                if(newStatus!=null){
                    dif.put("status",oldStatus+"->"+newStatus);
                }
            }
        }
        return dif;
    }
    public JSONObject firstHealthCheck(JSONObject newHealth){
        JSONObject dif=new JSONObject();
        String[] models=myMonitorConfig.getModel();
        for(String model:models){
            String[] keys=model.split("\\.");
            JSONObject newTmp=newHealth;
            for(String key:keys){
                if(newTmp!=null){
                    newTmp= (JSONObject) newTmp.get(key);
                }
            }
            String oldStatus="";
            String newStatus="";
            if(newTmp!=null){
                newStatus=newTmp.getString("status");
            }
            if(newStatus!=null){
                if(StringUtils.isNotBlank(newStatus)&&!newStatus.toLowerCase().equals("up")){
                    dif.put(model,newStatus);
                }
            }
        }
        if(newHealth!=null){
            String newStatus="";
            if(newStatus!=null){
                newStatus=newHealth.getString("status");
            }
            if(newStatus!=null){
                if(StringUtils.isNotBlank(newStatus)&&!newStatus.toLowerCase().equals("up")){
                    dif.put("status",newStatus);
                }
            }
        }
        return dif;
    }
    public Map<String, Map<String, ServiceInfoEntity>> getCopyOnlineAppMap() {
        Map<String, Map<String, ServiceInfoEntity>> map=new HashMap<>();
        map.putAll(onlineAppMap);
        return map;
    }


    public Map<String, Map<String, ServiceInfoEntity>> getCopyOfflineAppMap() {
        Map<String, Map<String, ServiceInfoEntity>> map=new HashMap<>();
        map.putAll(offLineAppMap);
        return map;
    }
    public  Map<String, Map<String, ServiceInfoEntity>> getAllMap(){
        Map<String, Map<String, ServiceInfoEntity>> allMap=new HashMap<>();
        Map<String, Map<String, ServiceInfoEntity>> onlineAppMap=getCopyOnlineAppMap();
        Map<String, Map<String, ServiceInfoEntity>> offlineAppMap=getCopyOnlineAppMap();
        allMap.putAll(onlineAppMap);
        for(String key: offlineAppMap.keySet()){
            Map<String, ServiceInfoEntity> needAddMap=allMap.get(key);
            needAddMap.putAll(offlineAppMap.get(key));
        }
        return allMap;
    }

    public ServiceInfoEntity getServiceInfo(String serviceId){
        ServiceInfoEntity serviceInfoEntity=null;
        String appName=getAppNameByServiceId(serviceId);
        Map<String, Map<String, ServiceInfoEntity>> appMap=getCopyOnlineAppMap();
        Map<String, ServiceInfoEntity> serviceMap=null;
        if(appMap!=null){
            serviceMap=appMap.get(appName.toUpperCase());
            if(serviceMap!=null){
                serviceInfoEntity=serviceMap.get(serviceId);
                if(serviceInfoEntity!=null){
                    return serviceInfoEntity;
                }
            }
        }
        appMap= getCopyOfflineAppMap();
        serviceMap=appMap.get(appName.toUpperCase());
        if(serviceMap!=null){
            serviceInfoEntity=serviceMap.get(serviceId);
            if(serviceInfoEntity!=null){
                return serviceInfoEntity;
            }
        }
        return null;
    }
    public void refreshAllService(){
        EurekaHttpResponse<Applications> eurekaHttpResponse=eurekaHttpClient.getApplications();
        if(eurekaHttpResponse!=null){
            Applications applications=eurekaHttpResponse.getEntity();
            if(applications!=null){
                for (Application application:applications.getRegisteredApplications()){
                    for(InstanceInfo instanceInfo:application.getInstances()){
                        refreshLocalServiceMap(instanceInfo);
                    }
                }
            }
        }
    }
    private void sendHealthStatusChangeMsg(String serviceId,JSONObject oldHealth,JSONObject nowHealth){
        NotifyMsgVo vo=new NotifyMsgVo();
        vo.setBusinessKey(serviceId);
        if(oldHealth!=null){
            JSONObject dif=compareHealth(oldHealth,nowHealth);
            vo.setTitle(serviceId +"健康状态变更:"+dif.remove("healthChangeNotify"));
            vo.setMsg("状态变更项:"+dif.toJSONString()+"</br></hr></br>原始状态完整信息:"+JSONObject.toJSONString(oldHealth)+"</br></hr></br>现有状态完整信息:"+JSONObject.toJSONString(nowHealth));
        }else{
            JSONObject dif=firstHealthCheck(nowHealth);
            if(dif.size()>0){
                vo.setTitle(serviceId +"启动时 健康状态 异常");
                vo.setMsg("异常状态:"+dif.toJSONString()+"</br></hr></br>完整健康检查信息:"+JSONObject.toJSONString(nowHealth));
            }else{
                return;
            }
        }
        vo.setNotifyTypeEnum(NotifyTypeEnum.ALL);
        vo.setSubtype(WebsocketTypeEnum.DEFAULT.getKey());
        vo.setSuccess(true);
        notifiyService.sendMsg(vo);

    }
    private void sendServiceStatusChangeMsg(InstanceInfo last,InstanceInfo now){
        NotifyMsgVo vo=new NotifyMsgVo();
        vo.setTitle(last.getInstanceId() +"状态变更 status:"+last.getStatus()+"->"+now.getStatus());
        vo.setBusinessKey(last.getInstanceId());
        vo.setMsg("原始状态:"+JSONObject.toJSONString(last)+"</br></hr></br>新状态:"+JSONObject.toJSONString(now));
        vo.setNotifyTypeEnum(NotifyTypeEnum.ALL);
        vo.setSubtype(WebsocketTypeEnum.DEFAULT.getKey());
        vo.setSuccess(true);
        notifiyService.sendMsg(vo);

    }
    private void sendServiceRegistMsg(InstanceInfo instanceInfo){
        if(myMonitorConfig.isRegistNotify()){//上线是否通知
            NotifyMsgVo vo=new NotifyMsgVo();
            vo.setTitle(instanceInfo.getInstanceId() +"上线");
            vo.setBusinessKey(instanceInfo.getInstanceId());
            vo.setMsg(instanceInfo.getInstanceId() +"上线</br></hr></br>微服务相关信息:"+JSONObject.toJSONString(instanceInfo));
            vo.setNotifyTypeEnum(NotifyTypeEnum.ALL);
            vo.setSubtype(WebsocketTypeEnum.DEFAULT.getKey());
            vo.setSuccess(true);
            notifiyService.sendMsg(vo);
        }
    }
    private String getAppNameByServiceId(String serviceId){
        return serviceIdAppnamemap.get(serviceId);
    }
}
