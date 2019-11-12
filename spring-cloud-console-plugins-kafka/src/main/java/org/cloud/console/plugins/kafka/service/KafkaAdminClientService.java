package org.cloud.console.plugins.kafka.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.I0Itec.zkclient.ZkClient;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;
import org.apache.zookeeper.ZooKeeper;
import org.cloud.console.plugins.kafka.config.KafkaConfig;
import org.cloud.console.plugins.zookeeper.util.SimpleZkClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能说明: Kafka服务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@Component
public class KafkaAdminClientService {

    private static final Logger log = LoggerFactory.getLogger(KafkaAdminClientService.class);

    private static KafkaConfig kafkaConfig;

    @Autowired
    public void setZkConfig(KafkaConfig kafkaConfig) {
        this.kafkaConfig = kafkaConfig;
    }

    public JSONObject getTopicsInfo(String groupId,String topic,int partition,long offset){
        JSONObject jsonObject = new JSONObject();
        KafkaConsumer<String, String> kafkaConsumer = null;
        try {
            kafkaConsumer = new KafkaConsumer<String, String>(KafkaAdminClientService.getProperties(groupId));
            TopicPartition topicPartition = new TopicPartition(topic,partition);
            kafkaConsumer.assign(Arrays.asList(topicPartition));
            kafkaConsumer.seek(topicPartition,offset);
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            List<Map> topicsList = new ArrayList<Map>();
            Map topicsInfoMap = new HashMap<String,Object>();

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (ConsumerRecord<String, String> record : records) {
                Map topicsMap = new HashMap<String,Object>();
                topicsMap.put("topic",record.topic());
                topicsMap.put("partitionId",record.partition());
                topicsMap.put("offset",record.offset());
                //topicsMap.put("key",record.key());
                topicsMap.put("value",record.value());
                Date date = new Date(record.timestamp());//用Date构造方法，将long转Date
                topicsMap.put("timestamp",format.format(date));
                topicsList.add(topicsMap);
            }

            Map recordKeysMap = new HashMap<String,Object>();
            recordKeysMap.put("recordId","topic");
            recordKeysMap.put("recordAttribute1","partitionId");
            recordKeysMap.put("recordAttribute2","offset");
            recordKeysMap.put("recordAttribute3","value");
            recordKeysMap.put("recordAttribute4","timestamp");
            topicsInfoMap.put("recordKeys",recordKeysMap);
            topicsInfoMap.put("recordRows",topicsList);
            jsonObject.put("rows",topicsInfoMap);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            kafkaConsumer.close();
        }

        return jsonObject;
    }


    public JSONObject getBrokers(String path){
        ZkClient zkClient = SimpleZkClient.createZkClientConnection();
        JSONObject jsonObject = null;
        ZooKeeper zooKeeper = null;
        try {
            if(zkClient.exists(path)){
                jsonObject = new JSONObject();
                List<String> childrens = SimpleZkClient.getChildren(zkClient,path);
                zooKeeper = SimpleZkClient.createZookeeperConnection();
                JSONArray jsonArray = new JSONArray();
                for(String clild:childrens){
                    String childpath = path+"/"+clild;
                    byte[] bytes = zooKeeper.getData(childpath,null, null);
                    String result = new String(bytes);
                    JSONObject jObject = JSONObject.parseObject(result);
                    JSONObject jNewObject = new JSONObject();
                    jNewObject.put("parentId",jObject.get("host")+":"+jObject.get("port"));
                    jNewObject.put("parentmenuaddress","/kafkaClient/listBrokersInfo?childpath="+childpath);
                    jsonArray.add(jNewObject);
                }
                jsonObject.put("rows",jsonArray);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            SimpleZkClient.releaseConnection(zooKeeper);
            SimpleZkClient.releaseConnection(zkClient);
        }
        return jsonObject;
    }

    public JSONObject getBrokersInfo(String childpath){
        ZkClient zkClient = SimpleZkClient.createZkClientConnection();
        JSONObject jsonObject = null;
        ZooKeeper zooKeeper = null;
        try {
            if(zkClient.exists(childpath)){
                jsonObject = new JSONObject();
                zooKeeper = SimpleZkClient.createZookeeperConnection();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                byte[] bytes = zooKeeper.getData(childpath,null, null);
                String result = new String(bytes);
                JSONObject jObject = JSONObject.parseObject(result);
                Map brokersInfoMap = new HashMap<String,Object>();
                brokersInfoMap.put("host",jObject.get("host"));
                brokersInfoMap.put("port",jObject.get("port"));
                brokersInfoMap.put("version",jObject.get("version"));
                brokersInfoMap.put("jmx_port",jObject.get("jmx_port"));
                Date date = new Date(Long.valueOf(jObject.get("timestamp").toString()));//用Date构造方法，将long转Date
                brokersInfoMap.put("timestamp",format.format(date));

                Map brokersMap = new HashMap<String,Object>();
                List<Map> brokersInfoList = new ArrayList<Map>();
                brokersInfoList.add(brokersInfoMap);

                Map recordKeysMap = new HashMap<String,Object>();
                recordKeysMap.put("recordId","host");
                recordKeysMap.put("recordAttribute1","port");
                recordKeysMap.put("recordAttribute2","version");
                recordKeysMap.put("recordAttribute3","jmx_port");
                recordKeysMap.put("recordAttribute4","timestamp");
                brokersMap.put("recordKeys",recordKeysMap);
                brokersMap.put("recordRows",brokersInfoList);

                jsonObject.put("rows",brokersMap);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            SimpleZkClient.releaseConnection(zooKeeper);
            SimpleZkClient.releaseConnection(zkClient);
        }
        return jsonObject;
    }

    public JSONObject getTopics(){
        AdminClient adminClient = KafkaAdminClientService.getAdminClient();
        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(true);
        ListTopicsResult result = adminClient.listTopics(listTopicsOptions);
        List<Map> topicsList = new ArrayList<Map>();
        try {
            Collection<TopicListing> topicListingCollection = result.listings().get();
            for(TopicListing topicListing:topicListingCollection){
                Map topicsMap = new HashMap<String,Object>();
                topicsMap.put("parentId",topicListing.name());
                DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Arrays.asList(topicListing.name()));
                Map<String,TopicDescription> topicDescriptionMap =  describeTopicsResult.all().get();
                Iterator<TopicDescription> topicDescriptionIterator = topicDescriptionMap.values().iterator();
                List<Map> topicPartitionList = new ArrayList<Map>();
                while(topicDescriptionIterator.hasNext()) {
                    TopicDescription topicDescription = topicDescriptionIterator.next();
                    List<TopicPartitionInfo> topicPartitionInfoList = topicDescription.partitions();
                    for(TopicPartitionInfo topicPartitionInfo: topicPartitionInfoList){
                        Map topicPartitionMap = new HashMap<String, Object>();
                        topicPartitionMap.put("childrenId","partition-"+topicPartitionInfo.partition());
                        topicPartitionMap.put("childrenAttribute1",topicPartitionInfo.leader().host()+":"+topicPartitionInfo.leader().port());
                        topicPartitionMap.put("childrenAttribute2",topicPartitionInfo.replicas().toString());
                        topicPartitionMap.put("childrenAttribute3",topicPartitionInfo.isr().toString());
                        topicPartitionMap.put("childrenmenuaddress","/kafkaClient/listTopicsInfo?groupId=0&topic="+topicListing.name()+"&partition="+topicPartitionInfo.partition()+"&offset=0");
                        topicPartitionList.add(topicPartitionMap);
                    }
                }
                topicsMap.put("childrenRows",topicPartitionList);
                topicsList.add(topicsMap);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            adminClient.close();
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray)JSONObject.toJSON(topicsList);
        jsonObject.put("rows",jsonArray);
        return jsonObject;
    }

    public JSONObject getConsumers(){
        List<Map> list = new ArrayList<Map>();
        AdminClient adminClient = KafkaAdminClientService.getAdminClient();
        ListConsumerGroupsResult listConsumerGroupsResult = adminClient.listConsumerGroups();
        try {
            Collection<ConsumerGroupListing> consumerGroupListingCollection = listConsumerGroupsResult.all().get();
            for(ConsumerGroupListing consumerGroupListing:consumerGroupListingCollection){
                Map consumersMap = new HashMap<String,Object>();
                consumersMap.put("parentId",consumerGroupListing.groupId());
                consumersMap.put("parentmenuaddress","/kafkaClient/listConsumersInfo?groupId="+consumerGroupListing.groupId());
                list.add(consumersMap);
            }
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            adminClient.close();
        }
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = (JSONArray)JSONObject.toJSON(list);
        jsonObject.put("rows",jsonArray);
        return jsonObject;
    }

    public JSONObject getConsumersInfo(String groupId){
        AdminClient adminClient = KafkaAdminClientService.getAdminClient();
        Map consumersMap = new HashMap<String,Object>();
        KafkaConsumer<String, String> consumer = null;
        try {
            consumer = new KafkaConsumer<String, String>(KafkaAdminClientService.getProperties(groupId));
            ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(groupId);
            Map<TopicPartition,OffsetAndMetadata> map = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();
            Iterator<Map.Entry<TopicPartition,OffsetAndMetadata>> itr = map.entrySet().iterator();
            List<Map> listConsumersTopicMap = new ArrayList<Map>();
            while(itr.hasNext()) {
                Map consumersTopicMap = new HashMap<String,Object>();
                Map.Entry<TopicPartition,OffsetAndMetadata> entry = itr.next();
                TopicPartition topicPartition =entry.getKey();
                OffsetAndMetadata offsetAndMetadata = entry.getValue();
                long offset = offsetAndMetadata.offset();
                String topic = topicPartition.topic();
                int partitionId = topicPartition.partition();

                Map<TopicPartition, Long> mapBeginning = consumer.beginningOffsets(Arrays.asList(topicPartition));
                Iterator<Map.Entry<TopicPartition, Long>> itrBeginning = mapBeginning.entrySet().iterator();
                long beginningOffset = 0;
                //mapBeginning只有一个元素，因为Arrays.asList(topicPartition)只有一个topicPartition
                while(itrBeginning.hasNext()) {
                    Map.Entry<TopicPartition, Long> entryBeginning = itrBeginning.next();
                    beginningOffset =  entryBeginning.getValue();
                }
                Map<TopicPartition, Long> mapEnd = consumer.endOffsets(Arrays.asList(topicPartition));
                Iterator<Map.Entry<TopicPartition, Long>> itrEnd = mapEnd.entrySet().iterator();
                long endOffset = 0;
                while(itrEnd.hasNext()) {
                    Map.Entry<TopicPartition, Long> entryEnd = itrEnd.next();
                    endOffset = entryEnd.getValue();
                }
                consumersTopicMap.put("topic",topic);
                consumersTopicMap.put("partitionId","partition-"+partitionId);
                consumersTopicMap.put("offset",offset);
                consumersTopicMap.put("beginningOffset",beginningOffset);
                consumersTopicMap.put("endOffset",endOffset);
                listConsumersTopicMap.add(consumersTopicMap);
            }

            Map recordKeysMap = new HashMap<String,Object>();
            recordKeysMap.put("recordId","topic");
            recordKeysMap.put("recordAttribute1","partitionId");
            recordKeysMap.put("recordAttribute2","offset");
            recordKeysMap.put("recordAttribute3","beginningOffset");
            recordKeysMap.put("recordAttribute4","endOffset");
            consumersMap.put("recordKeys",recordKeysMap);
            consumersMap.put("recordRows",listConsumersTopicMap);
        }catch (Exception e){
            log.error(e.getMessage());
            e.printStackTrace();
        }finally {
            consumer.close();
            adminClient.close();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rows",consumersMap);
        return jsonObject;
    }

    public static AdminClient getAdminClient(){
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, kafkaConfig.getUrl());
        AdminClient adminClient = AdminClient.create(props);
        return adminClient;
    }

    public static Properties getProperties(String groupId){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", kafkaConfig.getUrl());
        properties.put("group.id", groupId);
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer","org.apache.kafka.common.serialization.StringDeserializer");
        return properties;
    }
}


