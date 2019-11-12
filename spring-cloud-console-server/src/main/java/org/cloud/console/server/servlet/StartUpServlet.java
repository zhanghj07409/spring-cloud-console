package org.cloud.console.server.servlet;

import org.cloud.console.server.service.JobService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/16<br>
 * <br>
 */
@WebServlet(loadOnStartup = 1,urlPatterns = {"/xxx/x.xxx"} )//这urlPatterns 就是为了不让他拦截任何内容
public class StartUpServlet extends HttpServlet {
    @Autowired
    private JobService jobService;

    private static final Logger log = LoggerFactory.getLogger(StartUpServlet.class);
    @Override
    public void init(ServletConfig config) throws ServletException {
        //开启加载定时任务
        try {
            log.info("loading task");
            jobService.init();
        } catch (Exception e) {
            log.error("loading task fail",e);
            throw new ServletException(e);
        }
    }
}
