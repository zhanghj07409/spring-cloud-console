package org.cloud.console.plugins.zookeeper.service;

/**
 * Created by huajun.zhang on 2019/3/20.
 */
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

public class SpringConsumerMain {
    public static void main(String[] args) throws InterruptedException {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("classpath:spring-consumer.xml");
        TimeUnit.HOURS.sleep(1);
    }
}