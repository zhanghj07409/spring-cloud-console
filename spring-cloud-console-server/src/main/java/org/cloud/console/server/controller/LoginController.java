package org.cloud.console.server.controller;

import org.cloud.console.server.service.LoginService;
import org.cloud.console.common.util.properties.MessageUtil;
import org.cloud.console.common.util.response.BaseReturnResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Base64;

/**
 * 功能说明: 登录服务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/4/17<br>
 * <br>
 */
@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @RequestMapping(value = "/in", method = RequestMethod.POST)
    public BaseReturnResult login(HttpServletRequest request) {
        String name = request.getParameter("name");

        try {
            String password = new String(Base64.getDecoder().decode(request.getParameter("password").getBytes("UTF-8")));

            loginService.login(name,password);
            HttpSession session = request.getSession();
            session.setAttribute("LoginUser",name);
            return MessageUtil.Success("","登录成功！");
        }catch (Exception e){
            return MessageUtil.Exception(e);
        }
    }

    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public void logout(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.getSession().invalidate();
        response.sendRedirect("/");
    }

}
