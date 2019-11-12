package org.cloud.console.server.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
//import org.cloud.console.plugins.kafka.service.impl.KafkaAdminClientService;
import org.cloud.console.server.config.CloudConsolePluginsConfig;
import org.cloud.console.server.config.PluginType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能说明: 插件服务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@RestController
@RequestMapping("/pluginsClient")
public class PluginsController {

    @Autowired
    private CloudConsolePluginsConfig cloudConsolePluginsConfig;

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public JSONObject listPlugins() {
        PluginType[] pluginTypes= cloudConsolePluginsConfig.getPluginType();
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray) JSONObject.toJSON(pluginTypes);
        jsonObject.put("rows",jsonArray);
        System.out.println(jsonObject);
        return jsonObject;
    }

}
