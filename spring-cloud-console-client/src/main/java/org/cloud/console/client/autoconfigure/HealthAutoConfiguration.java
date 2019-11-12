package org.cloud.console.client.autoconfigure;

import org.cloud.console.client.health.KafkaConsumerHealthIndicator;
import org.cloud.console.client.health.KafkaProducerHealthIndicator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.CompositeHealthIndicatorConfiguration;
import org.springframework.boot.actuate.autoconfigure.ConditionalOnEnabledHealthIndicator;
import org.springframework.boot.actuate.autoconfigure.EndpointAutoConfiguration;
import org.springframework.boot.actuate.health.CompositeHealthIndicator;
import org.springframework.boot.actuate.health.HealthAggregator;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Iterator;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/22<br>
 * <br>
 */
@AutoConfigureBefore({ EndpointAutoConfiguration.class })
@AutoConfigureAfter({KafkaAutoConfiguration.class })
public class HealthAutoConfiguration {
    @Configuration
    @ConditionalOnClass(ConsumerFactory.class)
    @ConditionalOnBean(ConsumerFactory.class)
    @ConditionalOnEnabledHealthIndicator("kafkaConsumer")
    public static class KafkaConsumerHealthIndicatorConfiguration extends
            CompositeHealthIndicatorConfiguration<KafkaConsumerHealthIndicator, ConsumerFactory> {

        private final Map<String, ConsumerFactory> kafkaConsumerFactories;
        @Autowired
        private HealthAggregator healthAggregator;

        public KafkaConsumerHealthIndicatorConfiguration(
                Map<String, ConsumerFactory> kafkaConsumerFactories) {
            this.kafkaConsumerFactories = kafkaConsumerFactories;
        }

        @Bean
        @ConditionalOnMissingBean(name = "kafkaConsumerHealthIndicator")
        public HealthIndicator kafkaConsumerHealthIndicator() {
            if (kafkaConsumerFactories.size() == 1) {
                return  new KafkaConsumerHealthIndicator((kafkaConsumerFactories.values().iterator().next()));
            } else {
                CompositeHealthIndicator composite = new CompositeHealthIndicator(healthAggregator);
                Iterator var3 = kafkaConsumerFactories.entrySet().iterator();

                while(var3.hasNext()) {
                    Map.Entry<String, ProducerFactory> entry = (Map.Entry)var3.next();
                    composite.addHealthIndicator((String)entry.getKey(), new KafkaProducerHealthIndicator(entry.getValue()));
                }

                return composite;
            }
        }
    }
    @Configuration
    @ConditionalOnClass(ProducerFactory.class)
    @ConditionalOnBean(ProducerFactory.class)
    @ConditionalOnEnabledHealthIndicator("kafkaProductor")
    public static class KafkaProductorHealthIndicatorConfiguration extends
            CompositeHealthIndicatorConfiguration<KafkaConsumerHealthIndicator, ProducerFactory> {

        private final Map<String, ProducerFactory> kafkaProcuctories;
        @Autowired
        private HealthAggregator healthAggregator;

        public KafkaProductorHealthIndicatorConfiguration(
                Map<String, ProducerFactory> kafkaProductorFactories) {
            this.kafkaProcuctories = kafkaProductorFactories;
        }

        @Bean
        @ConditionalOnMissingBean(name = "kafkaProductorHealthIndicator")
        public HealthIndicator kafkaProductorHealthIndicator() {
            if (kafkaProcuctories.size() == 1) {
                return  new KafkaProducerHealthIndicator((kafkaProcuctories.values().iterator().next()));
            } else {
                CompositeHealthIndicator composite = new CompositeHealthIndicator(healthAggregator);
                Iterator var3 = kafkaProcuctories.entrySet().iterator();

                while(var3.hasNext()) {
                    Map.Entry<String, ProducerFactory> entry = (Map.Entry)var3.next();
                    composite.addHealthIndicator((String)entry.getKey(), new KafkaProducerHealthIndicator(entry.getValue()));
                }
                return composite;
            }
        }
    }
}
