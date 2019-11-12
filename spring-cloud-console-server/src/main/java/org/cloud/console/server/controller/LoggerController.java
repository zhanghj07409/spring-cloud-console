package org.cloud.console.server.controller;

import com.alibaba.fastjson.JSONObject;
import com.netflix.appinfo.InstanceInfo;
import org.apache.commons.lang.StringUtils;
import org.cloud.console.server.entity.ServiceInfoEntity;
import org.cloud.console.server.service.ServiceInfoService;
import org.cloud.console.server.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@RestController
@RequestMapping("/log")
public class LoggerController {
    private Logger logger=LoggerFactory.getLogger(LoggerController.class);
    @Autowired
    private ServiceInfoService serviceInfoService;
    @RequestMapping(value = "/getLogger",method =RequestMethod.POST)
    public String getLogger(String serviceId,String key){
        String result=null;
        try {
            ServiceInfoEntity entity=serviceInfoService.getServiceInfo(serviceId);
            InstanceInfo instanceInfo=entity.getInstanceInfo();
            String url=instanceInfo.getHomePageUrl()+"/loggers";
            if(StringUtils.isNotBlank(key)){
                url=url+"/"+key;
            }
            result = HttpClientUtil.get(url,null);
        } catch (Exception e) {
            logger.error("获取日志级别异常",e);
            return  "fail!";
        }
        return result;
    }
    @RequestMapping(value = "/setlogger",method =RequestMethod.POST)
    public String setlogger(String serviceId,String key,String configuredLevel){
        String result=null;
        try {
            ServiceInfoEntity entity=serviceInfoService.getServiceInfo(serviceId);
            InstanceInfo instanceInfo=entity.getInstanceInfo();
            String url=instanceInfo.getHomePageUrl()+"/loggers"+"/"+key;
            JSONObject jsonObject=new JSONObject();
            jsonObject.put("configuredLevel",configuredLevel);
            result = HttpClientUtil.post(url,jsonObject);
        } catch (Exception e) {
            logger.error("修改日志级别异常",e);
            return  "fail!";
        }
        return result;
    }
}
