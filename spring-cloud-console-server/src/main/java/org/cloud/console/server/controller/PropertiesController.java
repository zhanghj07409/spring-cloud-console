package org.cloud.console.server.controller;

import com.netflix.appinfo.InstanceInfo;
import org.cloud.console.server.entity.ServiceInfoEntity;
import org.cloud.console.server.service.ServiceInfoService;
import org.cloud.console.server.util.HttpClientUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能说明: 修改微服务中相关配置<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/25<br>
 * <br>
 */
@RestController
@RequestMapping("/properConfig")
public class PropertiesController {
    Logger logger=LoggerFactory.getLogger(PropertiesController.class);
    @Autowired
    private ServiceInfoService serviceInfoService;

    @RequestMapping("/updateProperties")
    public String updateServerProperties(String serviceId,String key,String value){
        String result= null;
        try {
            Map<String,String> requestParam =new HashMap<>();
            requestParam.put(key,value);
            ServiceInfoEntity entity=serviceInfoService.getServiceInfo(serviceId);
            InstanceInfo instanceInfo=entity.getInstanceInfo();
            String url=instanceInfo.getHomePageUrl()+"/env";
            result = HttpClientUtil.post(url,requestParam);
        } catch (NullPointerException e) {
            logger.error("修改配置异常",e);
            return  "fail! service not existed";
        }catch (Exception e) {
            logger.error("修改配置异常",e);
            return  "fail!";
        }
        return result;
    }
    @RequestMapping("/getProperties")
    public String getProperties(String serviceId){
        String result= null;
        try {
            ServiceInfoEntity entity=serviceInfoService.getServiceInfo(serviceId);
            InstanceInfo instanceInfo=entity.getInstanceInfo();
            String url=instanceInfo.getHomePageUrl()+"/configprops";
            result = HttpClientUtil.get(url,null);
        } catch (NullPointerException e) {
            logger.error("获取配置异常",e);
            return  "fail! service not existed";
        }catch (Exception e) {
            logger.error("获取配置异常",e);
            return  "fail!";
        }
        return result;
    }
}
