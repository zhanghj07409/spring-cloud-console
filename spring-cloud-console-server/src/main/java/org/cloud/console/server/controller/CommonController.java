package org.cloud.console.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能说明: 服务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/4/16<br>
 * <br>
 */
@Controller
public class CommonController {

    @RequestMapping(value = "/")
    public String listPlugins() {
        return "login";
    }
}
