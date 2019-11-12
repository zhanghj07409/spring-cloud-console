package org.cloud.console.plugins.zookeeper.service;

import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.ZooKeeper;
import org.cloud.console.plugins.zookeeper.util.SimpleZkClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by huajun.zhang on 2019/3/20.
 */
@Service
public class ZkCommonService {

    public static void main(String[] args) {
        /*ZkClient zkClient = SimpleZkClient.createZkClientConnection();
        List<String> list = zkClient.getChildren("/brokers");
       // System.out.println(list);
       // System.out.println(zkClient.getChildren("/brokers/ids"));
       // System.out.println(zkClient.getChildren("/brokers/topics"));
        //System.out.println(zkClient.getChildren("/brokers/seqid"));
        //System.out.println(zkClient.getChildren("/brokers/topics/TASK_ADD/partitions/0/state"));

        String result = null;
        try {
            ZooKeeper zooKeeper = SimpleZkClient.createZookeeperConnection();
            byte[] bytes = zooKeeper.getData("/brokers/topics/TASK_ADD/partitions/0/state",null, null);
            result = new String(bytes);
            JSONObject jsonObject =JSONObject.parseObject(result);

            System.out.println("获取节点数据：" + jsonObject);

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("获取节点数据：" + result);*/

        ApplicationContext ctx = new ClassPathXmlApplicationContext("classpath:spring-producer.xml");
        KafkaTemplate kafkaTemplate = ctx.getBean("kafkaTemplate", KafkaTemplate.class);
        for (int i = 1; i < 5; i++) {
            String msg = "TASK_ADD这款产品目前性价比还是很高的。觉得贵的话，医疗报销这一块可以先去掉。不过如果去掉了，就不能做到有什么事保险都能保的了。-" + i;
            //向topicOne发送消息
            kafkaTemplate.send("TASK_ADD", msg);
            System.out.println("send msg  : " + msg);
        }

        //KafkaMsgProducer  kafkaMsgProducer = new KafkaMsgProducer();
        //kafkaMsgProducer.sendMessage("PAYABLE","这款产品目前性价比还是很高的。觉得贵的话，医疗报销这一块可以先去掉。不过如果去掉了，就不能做到有什么事保险都能保的了。");
    }
}
