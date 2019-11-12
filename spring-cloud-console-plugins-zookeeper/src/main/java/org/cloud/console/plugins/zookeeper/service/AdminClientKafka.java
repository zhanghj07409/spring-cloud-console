package org.cloud.console.plugins.zookeeper.service;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.admin.*;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by huajun.zhang on 2019/3/21.
 */
public class AdminClientKafka {

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        AdminClient adminClient = AdminClient.create(props);

        /*DescribeTopicsResult topicResult = adminClient.describeTopics(Arrays.asList("PAYABLE"));
        Map<String, KafkaFuture<TopicDescription>> descMap = topicResult.values();
        Iterator<Map.Entry<String, KafkaFuture<TopicDescription>>> itr = descMap.entrySet().iterator();
        while(itr.hasNext()) {
            Map.Entry<String, KafkaFuture<TopicDescription>> entry = itr.next();
            System.out.println("key: " + entry.getKey());
            //List<TopicPartitionInfo> topicPartitionInfoList = entry.getValue().get().partitions();
        }*/

        //ListConsumerGroupsResult list = adminClient.listConsumerGroups();
        //ListTopicsResult list2 = adminClient.listTopics();
        ListTopicsOptions listTopicsOptions = new ListTopicsOptions();
        listTopicsOptions.listInternal(true);
        ListTopicsResult result = adminClient.listTopics(listTopicsOptions);
        try {
            Collection<TopicListing> list = result.listings().get();
            System.out.println(list);
            DescribeTopicsResult describeTopicsResult = adminClient.describeTopics(Arrays.asList("PAYABLE"));
            System.out.println(describeTopicsResult.all().get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        /*ListConsumerGroupsOptions listConsumerGroupsOptions = new ListConsumerGroupsOptions();
        //listConsumerGroupsOptions.listInternal(true);
        ListConsumerGroupsResult result = adminClient.listConsumerGroups(listConsumerGroupsOptions);
        try {
            Collection<ConsumerGroupListing> list = result.all().get();
            for(ConsumerGroupListing consumerGroupListing:list){

                KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(AdminClientKafka.getProperties(consumerGroupListing.groupId()));

                System.out.println(consumerGroupListing.groupId());
                ListConsumerGroupOffsetsResult listConsumerGroupOffsetsResult = adminClient.listConsumerGroupOffsets(consumerGroupListing.groupId());
                Map<TopicPartition,OffsetAndMetadata> map = listConsumerGroupOffsetsResult.partitionsToOffsetAndMetadata().get();
                System.out.println(map);
                Iterator<Map.Entry<TopicPartition,OffsetAndMetadata>> itr = map.entrySet().iterator();
                while(itr.hasNext()) {
                    Map.Entry<TopicPartition,OffsetAndMetadata> entry = itr.next();
                    TopicPartition topicPartition =entry.getKey();
                    OffsetAndMetadata offsetAndMetadata = entry.getValue();
                    long offset = offsetAndMetadata.offset();
                    //String topic = topicPartition.topic();
                    //int partitionId = topicPartition.partition();

                    //TopicPartition topicPartition = new TopicPartition(topic, partitionId);
                    Map<TopicPartition, Long> mapBeginning = consumer.beginningOffsets(Arrays.asList(topicPartition));
                    Iterator<Map.Entry<TopicPartition, Long>> itr2 = mapBeginning.entrySet().iterator();
                    long beginOffset = 0;
                    //mapBeginning只有一个元素，因为Arrays.asList(topicPartition)只有一个topicPartition
                    while(itr2.hasNext()) {
                        Map.Entry<TopicPartition, Long> tmpEntry = itr2.next();
                        beginOffset =  tmpEntry.getValue();
                    }
                    Map<TopicPartition, Long> mapEnd = consumer.endOffsets(Arrays.asList(topicPartition));
                    Iterator<Map.Entry<TopicPartition, Long>> itr3 = mapEnd.entrySet().iterator();
                    long lastOffset = 0;
                    while(itr3.hasNext()) {
                        Map.Entry<TopicPartition, Long> tmpEntry2 = itr3.next();
                        lastOffset = tmpEntry2.getValue();
                    }
                    System.out.println("张华君的时间Leader of partitionId: " + topicPartition.partition() + " groupId " + consumerGroupListing.groupId() + " , topic " + topicPartition.topic() + ".  offset:"+ offset
                            + "，  beginOffset:" + beginOffset + ", lastOffset:" + lastOffset);

                }
                DescribeConsumerGroupsResult describeConsumerGroupsResult = adminClient.describeConsumerGroups(Arrays.asList(consumerGroupListing.groupId()));
                System.out.println(describeConsumerGroupsResult);


            }
            System.out.println(list);

        } catch (Exception e) {
            e.printStackTrace();
        }*/


       /* ConsumerGroupSummary consumerGroupSummary =  adminClient.describeConsumerGroup("kafkatest");
        if(consumerGroupSummary.state().equals("Empty")){
            System.out.println("niaho");
        }
        Option<List<ConsumerSummary>> consumerSummaryOption =  consumerGroupSummary.consumers();

        List<ConsumerSummary> ConsumerSummarys = consumerSummaryOption.get();//获取组中的消费者
        KafkaConsumer consumer = getNewConsumer();
        for(int i=0;i<ConsumerSummarys.size();i++){ //循环组中的每一个消费者

            ConsumerSummary consumerSummary = ConsumerSummarys.apply(i);
            String consumerId  = consumerSummary.consumerId();//获取消费者的id
            scala.collection.immutable.Map<TopicPartition, Object> maps =
                    adminClient.listGroupOffsets("kafkatest");//或者这个组消费的所有topic，partition和当前消费到的offset
            List<TopicPartition> topicPartitions= consumerSummary.assignment();//获取这个消费者下面的所有topic和partion
            for(int j =0;j< topicPartitions.size();j++){ //循环获取每一个topic和partion
                TopicPartition topicPartition = topicPartitions.apply(j);
                String CURRENToFFSET = maps.get(topicPartition).get().toString();
                long endOffset =getLogEndOffset(topicPartition);
                System.out.println("topic的名字为："+topicPartition.topic()+"====分区为："+topicPartition.partition()+"===目前消费offset为："+CURRENToFFSET+"===,此分区最后offset为："+endOffset);
            }
        }*/
    }

    public static Properties getProperties(String groupId){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");
        properties.put("group.id", groupId);
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        return properties;
    }
}


