package org.cloud.console.plugins.kafka.controller;

import com.alibaba.fastjson.JSONObject;
import org.cloud.console.plugins.kafka.service.KafkaAdminClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 功能说明: Kafka客户端<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@RestController
@RequestMapping("/kafkaClient")
public class KafkaClientController {

    @Autowired
    private KafkaAdminClientService kafkaAdminClientService;

    @RequestMapping(value = "/listBrokers", method = RequestMethod.GET)
    public JSONObject listBrokers() {
        return kafkaAdminClientService.getBrokers("/brokers/ids");
    }

    @RequestMapping(value = "/listBrokersInfo", method = RequestMethod.GET)
    public JSONObject listBrokersInfo(String childpath) {
        return kafkaAdminClientService.getBrokersInfo(childpath);
    }

    @RequestMapping(value = "/listTopics", method = RequestMethod.GET)
    public JSONObject listTopics() {
        return kafkaAdminClientService.getTopics();
    }

    @RequestMapping(value = "/listConsumers", method = RequestMethod.GET)
    public JSONObject listConsumers() {
        return kafkaAdminClientService.getConsumers();
    }

    @RequestMapping(value = "/listConsumersInfo", method = RequestMethod.GET)
    public JSONObject listConsumersInfo(String groupId) {
        return kafkaAdminClientService.getConsumersInfo(groupId);
    }

    @RequestMapping(value = "/listTopicsInfo", method = RequestMethod.GET)
    public JSONObject listTopicsInfo(String groupId,String topic,int partition,long offset) {
        return kafkaAdminClientService.getTopicsInfo(groupId,topic,partition,offset);
    }


}
