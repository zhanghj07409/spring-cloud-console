package org.cloud.console.server.service;

import org.cloud.console.server.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 功能说明: 登录服务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/4/17<br>
 * <br>
 */
@Service
public class LoginService {

    @Autowired
    SecurityConfig securityConfig;

    public void login(String name,String password) throws Exception{
        if(!name.equals(securityConfig.getName())){
            throw new Exception("用户名错误");
        }else if(!password.equals(securityConfig.getPassword())){
            throw new Exception("密码错误");
        }
    }
}
