package org.cloud.console.server.config;

import org.cloud.console.server.enums.HealthChangeNotifyEnum;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/12<br>
 * <br>
 */
@Configuration
@ConfigurationProperties(prefix = "mymonitor")
public class MyMonitorConfig {
    private String[] model={"redis","db","configServer","hystrix","discoveryComposite","discoveryComposite.discoveryClient","discoveryComposite.eureka"};
    private int healthFialNotifyTime=3;
    private boolean registNotify=false;//服务上线是否通知
    private HealthChangeNotifyEnum healthChangeNotify=HealthChangeNotifyEnum.ALL;//是否所有状态变化都发消息
    public String[] getModel() {
        return model;
    }

    public void setModel(String[] model) {
        this.model = model;
    }

    public int getHealthFialNotifyTime() {
        return healthFialNotifyTime;
    }

    public void setHealthFialNotifyTime(int healthFialNotifyTime) {
        this.healthFialNotifyTime = healthFialNotifyTime;
    }

    public boolean isRegistNotify() {
        return registNotify;
    }

    public void setRegistNotify(boolean registNotify) {
        this.registNotify = registNotify;
    }

    public HealthChangeNotifyEnum getHealthChangeNotify() {
        return healthChangeNotify;
    }

    public void setHealthChangeNotify(String healthChangeNotify) {
        this.healthChangeNotify = HealthChangeNotifyEnum.toEnum(healthChangeNotify);
    }
}
