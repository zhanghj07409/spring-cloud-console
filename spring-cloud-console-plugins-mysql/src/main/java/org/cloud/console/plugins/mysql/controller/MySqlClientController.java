package org.cloud.console.plugins.mysql.controller;

import com.alibaba.fastjson.JSONObject;
import org.cloud.console.common.util.properties.MessageUtil;
import org.cloud.console.common.util.response.BaseReturnResult;
import org.cloud.console.plugins.mysql.service.MySqlClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能说明: MySql客户端<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@RestController
@RequestMapping("/mySqlClient")
public class MySqlClientController {

    @Autowired
    private MySqlClientService mySqlAdminClientService;

    @RequestMapping(value = "/listMySql")
    public JSONObject listMySql() {
        return mySqlAdminClientService.getMySql();
    }

    @RequestMapping(value = "/listGlobalVariables")
    public JSONObject listGlobalVariables(String queryParam) {
        return mySqlAdminClientService.getGlobalVariables(queryParam);
    }

    @RequestMapping(value = "/updateGlobalVariable")
    public BaseReturnResult updateGlobalVariable(String key,String value) {
        try {
            mySqlAdminClientService.updateGlobalVariable(key,value);
            return MessageUtil.Success();
        }catch (Exception e){
            return MessageUtil.Exception(e);
        }
    }

}
