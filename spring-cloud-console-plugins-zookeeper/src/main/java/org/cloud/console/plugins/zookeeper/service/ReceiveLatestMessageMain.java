package org.cloud.console.plugins.zookeeper.service;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.DescribeTopicsResult;
import org.apache.kafka.clients.admin.TopicDescription;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.Node;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.TopicPartitionInfo;

import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * 本例子试着读取当前topic每个分区内最新的30条消息(如果topic额分区内有没有30条，就获取实际消息)
 * className: ReceiveLatestMessageMain
 *
 * @author EricYang
 * @version 2019/01/10 11:30
 */

public class ReceiveLatestMessageMain {
    private static final int COUNT = 30;
    private static final String server = "127.0.0.1:9092";

    public static void main(String... args) throws Exception {
        Properties properties = new Properties();

        properties.put("bootstrap.servers", server);
        properties.put("group.id", "test");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");


        System.out.println("create KafkaConsumer");
        final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties);
        AdminClient adminClient = AdminClient.create(properties);
        String topic = "PAYABLE";
        adminClient.describeTopics(Arrays.asList(topic));
        try {
            DescribeTopicsResult topicResult = adminClient.describeTopics(Arrays.asList(topic));
            Map<String, KafkaFuture<TopicDescription>> descMap = topicResult.values();
            Iterator<Map.Entry<String, KafkaFuture<TopicDescription>>> itr = descMap.entrySet().iterator();
            while(itr.hasNext()) {
                Map.Entry<String, KafkaFuture<TopicDescription>> entry = itr.next();
                System.out.println("生成key: " + entry.getKey());
                List<TopicPartitionInfo> topicPartitionInfoList = entry.getValue().get().partitions();
                for(TopicPartitionInfo e:topicPartitionInfoList){
                    int partitionId = e.partition();
                    Node node  = e.leader();
                    TopicPartition topicPartition = new TopicPartition(topic, partitionId);
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
                    long expectedOffSet = lastOffset - COUNT;
                    expectedOffSet = expectedOffSet > 0? expectedOffSet : 1;
                    System.out.println("张华君的时间Leader of partitionId: " + partitionId + "  is " + node + ".  expectedOffSet:"+ expectedOffSet
                            + "，  beginOffset:" + beginOffset + ", lastOffset:" + lastOffset);
                    consumer.commitSync(Collections.singletonMap(topicPartition, new OffsetAndMetadata(expectedOffSet -1 )));
                }
            }

            //consumer.subscribe(Arrays.asList(topic));
            //while (true) {
                /*ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.printf("张华君的时间read offset =%d, key=%s , value= %s, partition=%s\n",
                            record.offset(), record.key(), record.value(), record.partition());
                }*/
            //}
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("when calling kafka output error." + ex.getMessage());
        } finally {
            adminClient.close();
            consumer.close();
        }
    }
}