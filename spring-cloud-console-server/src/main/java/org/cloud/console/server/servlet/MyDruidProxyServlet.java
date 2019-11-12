package org.cloud.console.server.servlet;

import org.apache.http.client.utils.URIUtils;
import org.mitre.dsmiley.httpproxy.ProxyServlet;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;

/**
 * 功能说明: druid反向代理<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/9<br>
 * <br>
 */
public class MyDruidProxyServlet extends ProxyServlet {
    protected void initTarget() throws ServletException {

    }
    @Override
    protected void service(HttpServletRequest servletRequest, HttpServletResponse servletResponse) throws ServletException, IOException {
        Object newTargetUri=servletRequest.getSession().getAttribute("proxy_target");
        if(newTargetUri!=null){
            servletRequest.setAttribute(ATTR_TARGET_URI, newTargetUri);

            URI targetUriObj;
            try {
                targetUriObj = new URI(newTargetUri.toString());
            } catch (Exception var15) {
                throw new ServletException("Rewritten targetUri is invalid: " + newTargetUri, var15);
            }

            servletRequest.setAttribute(ATTR_TARGET_HOST, URIUtils.extractHost(targetUriObj));
        }
        super.service(servletRequest,servletResponse);
    }
}
