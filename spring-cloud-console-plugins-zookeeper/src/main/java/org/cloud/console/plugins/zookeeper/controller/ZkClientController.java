package org.cloud.console.plugins.zookeeper.controller;

import com.alibaba.fastjson.JSONArray;
import org.cloud.console.plugins.zookeeper.service.ZkClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能说明: ZK客户端<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@RestController
@RequestMapping("/zkClient")
public class ZkClientController {

    @Autowired
    private ZkClientService zkClientService;

    @RequestMapping(value = "/listNodes")
    public JSONArray listNodes(String path) {
        return zkClientService.listNodes(path);
    }


    @RequestMapping(value = "/listNodesInfo")
    public String listNodesInfo(String path) {
        return zkClientService.listNodesInfo(path);
    }




}
