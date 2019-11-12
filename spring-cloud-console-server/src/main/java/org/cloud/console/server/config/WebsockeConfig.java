package org.cloud.console.server.config;

import org.apache.catalina.session.StandardSessionFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

import javax.servlet.http.HttpSession;
import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/22<br>
 * <br>
 */
@Configuration
public class WebsockeConfig extends ServerEndpointConfig.Configurator {
    private static final Logger log=LoggerFactory.getLogger(WebsockeConfig.class);

    private static final String HttpSession = null;
    /* 修改握手,就是在握手协议建立之前修改其中携带的内容 */
    @Override
    public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
        /*如果没有监听器,那么这里获取到的HttpSession是null*/
        StandardSessionFacade ssf = (StandardSessionFacade) request.getHttpSession();
        if (ssf != null) {
            javax.servlet.http.HttpSession session = (HttpSession) request.getHttpSession();
            sec.getUserProperties().put("sessionid", session.getId());
            sec.getUserProperties().put("LoginUser",session.getAttribute("LoginUser"));
        }
        super.modifyHandshake(sec, request, response);
    }
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
