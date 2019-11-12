package org.cloud.console.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * 功能说明: 插件配置信息<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@Configuration
@ConfigurationProperties(prefix = "CloudConsolePlugins")
public class CloudConsolePluginsConfig {

    private PluginType[] pluginType;

    public PluginType[] getPluginType() {
        return pluginType;
    }

    public void setPluginType(PluginType[] pluginType) {
        this.pluginType = pluginType;
    }
}
