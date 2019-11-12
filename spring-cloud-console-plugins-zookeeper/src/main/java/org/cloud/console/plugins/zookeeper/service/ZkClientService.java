package org.cloud.console.plugins.zookeeper.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;
import org.cloud.console.plugins.zookeeper.util.SimpleZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能说明: ZK服务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/28<br>
 * <br>
 */
@Component
public class ZkClientService {

    private static final Logger log = LoggerFactory.getLogger(ZkClientService.class);
    private static SimpleZkClient simpleZkClient;

    @Autowired
    public void setSimpleZkClient(SimpleZkClient simpleZkClient) {
        this.simpleZkClient = simpleZkClient;
    }

    public JSONArray listNodes(String path){
        ZkClient zkClient = simpleZkClient.createZkClientConnection();
        JSONArray jsonArray = new JSONArray();
        try {
            String parentpath = path;
            if(parentpath==null){
                parentpath = "/";
                path="";
            }
            if(zkClient.exists(parentpath)){
                List<String> childrens = simpleZkClient.getChildren(zkClient,parentpath);
                for(String clild:childrens){
                    String childpath = path+"/"+clild;
                    JSONObject jNewObject = new JSONObject();
                    jNewObject.put("pId",parentpath);
                    jNewObject.put("id",clild);
                    jNewObject.put("name",clild);
                    jNewObject.put("path",childpath);
                    if(!zkClient.getChildren(childpath).isEmpty()){
                        jNewObject.put("isParent",true);
                    }else {
                        jNewObject.put("isParent",false);
                        jNewObject.put("pathurl","/zkClient/listNodesInfo?path="+childpath);
                    }
                    jsonArray.add(jNewObject);
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            simpleZkClient.releaseConnection(zkClient);
        }
        return jsonArray;
    }

    public String listNodesInfo(String path){
        ZkClient zkClient = simpleZkClient.createZkClientConnection();
        ZooKeeper zooKeeper = null;
        String result = "";
        try {
            if(zkClient.exists(path)){
                zooKeeper = simpleZkClient.createZookeeperConnection();
                byte[] bytes = zooKeeper.getData(path,null, null);
                if(bytes!=null&&bytes.length>0){
                    result = new String(bytes);
                }
            }
        }catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            simpleZkClient.releaseConnection(zooKeeper);
            simpleZkClient.releaseConnection(zkClient);
        }

        return result;
    }
}
