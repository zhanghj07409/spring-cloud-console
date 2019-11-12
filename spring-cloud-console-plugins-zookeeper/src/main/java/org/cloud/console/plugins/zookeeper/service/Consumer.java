package org.cloud.console.plugins.zookeeper.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.util.*;

/**
 * Created by huajun.zhang on 2019/3/20.
 */
public class Consumer {
    private static final String server = "127.0.0.1:9092";

    public static void main(String[] args) {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", server);
        properties.put("group.id", "0");
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        //final KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(properties, new StringDeserializer(), new StringDeserializer());

        // 2、创建KafkaConsumer
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(properties);
        // 3、订阅数据，这里的topic可以是多个
        //kafkaConsumer.subscribe(Arrays.asList("PAYABLE"));

        TopicPartition topicPartition = new TopicPartition("TASK_ADD",0);
        Collection<TopicPartition> partitions = Arrays.asList(topicPartition);
        //kafkaConsumer.assignment();
        kafkaConsumer.assign(Arrays.asList(topicPartition));
        kafkaConsumer.seek(topicPartition,0L);
        //kafkaConsumer.seekToBeginning(kafkaConsumer.assignment());
        //kafkaConsumer.seekToBeginning(partitions);
        //Map<TopicPartition,Long> beginningOffset = kafkaConsumer.beginningOffsets(partitions);

        //读取历史数据 --from-beginning
        //for(Map.Entry<TopicPartition,Long> entry : beginningOffset.entrySet()){
            // 基于seek方法
            //TopicPartition tp = entry.getKey();
            //long offset = entry.getValue();
            //consumer.seek(tp,offset);

            // 基于seekToBeginning方法
        //    kafkaConsumer.seekToBeginning(partitions);
        //}
        // 4、获取数据
        //while (true) {
            ConsumerRecords<String, String> records = kafkaConsumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                System.out.printf("topic = %s,offset = %d, key = %s, value = %s%n",record.topic(), record.offset(), record.key(), record.value());
            }
        //}

        /*consumer.subscribe(Collections.singletonList("0"), new ConsumerRebalanceListener() {
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {

            }

            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                Map<TopicPartition,Long> beginningOffset = consumer.beginningOffsets(collection);

                //读取历史数据 --from-beginning
                for(Map.Entry<TopicPartition,Long> entry : beginningOffset.entrySet()){
                    // 基于seek方法
                    //TopicPartition tp = entry.getKey();
                    //long offset = entry.getValue();
                    //consumer.seek(tp,offset);

                    // 基于seekToBeginning方法
                    consumer.seekToBeginning(collection);
                }
            }
        });

        try {
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println("partition:" + record.partition() + ",key:" + record.key() + ",value:" + record.value());
                    consumer.commitAsync();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }*/
    }
}
