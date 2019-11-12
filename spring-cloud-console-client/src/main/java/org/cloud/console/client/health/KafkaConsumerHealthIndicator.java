package org.cloud.console.client.health;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.common.PartitionInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/22<br>
 * <br>
 */
public class KafkaConsumerHealthIndicator extends AbstractHealthIndicator {
    Logger log=LoggerFactory.getLogger(KafkaConsumerHealthIndicator.class);
    private ConsumerFactory consumerFactory;
    private Consumer consumer;
    private ExecutorService exec;

    public KafkaConsumerHealthIndicator(ConsumerFactory consumerFactory) {
		this.consumerFactory = consumerFactory;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        if(consumer==null){
            consumer = consumerFactory.createConsumer();

        }
        if(exec==null){
            exec = Executors.newFixedThreadPool(1);
        }
        Map<String, Object> config=((DefaultKafkaConsumerFactory) consumerFactory).getConfigurationProperties();
        try {
            for(String key:config.keySet()){
                builder.withDetail(key,config.get(key));
            }
            Callable<Map<String, List<PartitionInfo>>> call = new Callable<Map<String, List<PartitionInfo>>>() {
                public Map<String, List<PartitionInfo>> call() throws Exception {
                    //开始执行耗时操作
                    return consumer.listTopics();
                }
            };
            Future<Map<String, List<PartitionInfo>>> future = exec.submit(call);
            Map<String, List<PartitionInfo>> topcMap=future.get(5, TimeUnit.SECONDS);//5秒没获取到数据设置为超时
            builder.up();
        } catch (TimeoutException ex) {
            builder.down();
            builder.withException(ex);
            log.error("kafka 连接超时",ex);
        } catch (Exception e) {
            builder.down();
            builder.withException(e);
            log.error("kafka 检查检查失败",e);
        }
    }
}
