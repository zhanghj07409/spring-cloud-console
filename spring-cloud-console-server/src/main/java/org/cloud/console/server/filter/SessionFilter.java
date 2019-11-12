package org.cloud.console.server.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * 功能说明: SessionFilter<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@WebFilter(filterName = "sessionFilter",urlPatterns = {"/*"})
public class SessionFilter implements Filter {

    private static String filterPattern = ".*do|((?!\\.).)*|.*\\.html";
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) resp;

        String requestURI = request.getRequestURI();

        String page = requestURI.substring(request.getContextPath().length());


        HttpSession session = request.getSession();
        if (page.matches(filterPattern)) {
            // 取得用户信息
            String currUser = (String) session.getAttribute("LoginUser");
            // 要在登录页添加功能，如注册、修改密码，需要修改page.equals()，否则跳入登录页
            if (currUser == null && !page.equals("/")
                    && !page.equals("/login/in")
                    && !page.equals("/health")
                    && !page.equals("/env")
                    && !page.equals("/configprops")
                    && !page.equals("/druid")
                    && !page.contains("/loggers")
                    && !page.equals("/login.html")) {
                response.setContentType("text/html;charset=utf-8");
                String type = request.getHeader("X-Requested-With") == null ? "" : request.getHeader("X-Requested-With");
                if ("XMLHttpRequest".equals(type)) {
                    response.setHeader("REDIRECT", "REDIRECT");//告诉ajax这是重定向
                    response.setHeader("CONTEXTPATH", request.getContextPath() + "/");//重定向地址
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return;
                } else if (page.equals("/login/logout")) {
                    response.sendRedirect(request.getContextPath() + "/");
                    return;
                } else {//如果不是ajax请求，则直接重定向
                    response.sendRedirect(request.getContextPath() + "/");
                    return;
                }
            }
        }

        // 继续向下执行
        chain.doFilter(req, resp);

    }

    public void init(FilterConfig config) throws ServletException {

    }

    public void destroy() {
        // TODO Auto-generated method stub

    }

}
