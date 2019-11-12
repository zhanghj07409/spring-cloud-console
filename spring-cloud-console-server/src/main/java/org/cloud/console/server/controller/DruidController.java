package org.cloud.console.server.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/9<br>
 * <br>
 */
@Controller()
@RequestMapping("/druidview")
public class DruidController {

    Logger logger=LoggerFactory.getLogger(ServiceController.class);
    @RequestMapping("/proxy")
    public void proxy(HttpServletRequest request, HttpServletResponse response, String url) throws IOException {
        logger.info("set proxy target:"+url);
        if(!url.endsWith("/")){
            url=url+"/";
        }
        request.getSession().setAttribute("proxy_target",url+"druid");
        response.sendRedirect(request.getContextPath() + "/proxy/druid");
    }
}
