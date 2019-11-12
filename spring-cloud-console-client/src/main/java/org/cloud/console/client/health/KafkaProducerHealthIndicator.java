package org.cloud.console.client.health;

import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.core.ProducerFactory;

import java.util.concurrent.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/22<br>
 * <br>
 */
public class KafkaProducerHealthIndicator extends AbstractHealthIndicator {
    Logger log=LoggerFactory.getLogger(KafkaProducerHealthIndicator.class);
    private ProducerFactory producerFactory;
    private Producer producer;

    public KafkaProducerHealthIndicator(ProducerFactory producerFactory) {
		this.producerFactory = producerFactory;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        if(producer==null){
            producer = producerFactory.createProducer();
        }
        try {
            Future<RecordMetadata> future = producer.send(new ProducerRecord("producerHealthCheck", ""));
            RecordMetadata recordMetadata=future.get(5,TimeUnit.SECONDS);//30秒没获取到数据设置为超时
            builder.up();
        } catch (TimeoutException ex) {
            builder.down().withException(ex);
            log.error("kafka 连接超时",ex);
        } catch (Exception e) {
            builder.down().withException(e);
            log.error("kafka 检查检查失败",e);
        }
    }
}
