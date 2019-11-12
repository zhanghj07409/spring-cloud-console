package org.cloud.console.plugins.zookeeper.listener;

/**
 * Created by huajun.zhang on 2019/3/20.
 */
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Component;

@Component("kafkaConsumerListener")
public class KafkaConsumerListener implements MessageListener<String, String> {
    public void onMessage(ConsumerRecord<String, String> record) {
        String topic = record.topic();
        String key = record.key();
        String val = record.value();
        long offset = record.offset();
        int partition = record.partition();
        System.out.printf("消息中间件receive msg -- topic:%s   key:%s  val:%s  offset:%s  partition:%s \r\n",topic,key,val,offset,partition);

    }
}