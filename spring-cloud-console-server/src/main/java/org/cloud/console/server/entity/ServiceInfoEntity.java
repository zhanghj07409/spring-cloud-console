package org.cloud.console.server.entity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/19<br>
 * <br>
 */
public class ServiceInfoEntity {
    private InstanceInfo instanceInfo;
    private JSONObject healthInfo;
    private int healthFailTime=0;

    public ServiceInfoEntity(InstanceInfo instanceInfo){
        this.instanceInfo = new InstanceInfo(instanceInfo);
    }

    public InstanceInfo getInstanceInfo() {
        return instanceInfo;
    }

    public void setInstanceInfo(InstanceInfo instanceInfo) {
        this.instanceInfo = instanceInfo;
    }

    public JSONObject getHealthInfo() {
        return healthInfo;
    }

    public void setHealthInfo(JSONObject healthInfo) {
        this.healthInfo = healthInfo;
    }

    public int getHealthFailTime() {
        return healthFailTime;
    }

    public void setHealthFailTime(int healthFailTime) {
        this.healthFailTime = healthFailTime;
    }
}
