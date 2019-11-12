package org.cloud.console.server.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;


/**
 * 功能说明: 安全认证配置类<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/4/17<br>
 * <br>
 */
@Configuration
@ConfigurationProperties(prefix = "security")
public class SecurityConfig implements ApplicationContextAware {

    private static SecurityConfig instance = null;
    private static ApplicationContext context;

    @Value("${security.user.name}")
    private String name;

    @Value("${security.user.password}")
    private String password;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setApplicationContext(ApplicationContext contex) throws BeansException {
        this.context = contex;
    }
    public static ApplicationContext getContext() {
        return context;
    }

    public static SecurityConfig getInstance(){
        if (instance == null){
            instance = (SecurityConfig) context.getBean("securityConfig");
        }
        return instance;
    }

}
