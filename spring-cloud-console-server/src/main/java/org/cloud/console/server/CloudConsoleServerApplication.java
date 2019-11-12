package org.cloud.console.server;


import com.netflix.appinfo.EurekaClientIdentity;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.resolver.DefaultEndpoint;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.jersey.JerseyEurekaHttpClientFactory;
import org.cloud.console.server.service.ServiceInfoService;
import org.cloud.console.server.servlet.MyDruidProxyServlet;
import org.mitre.dsmiley.httpproxy.ProxyServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import zipkin.autoconfigure.ui.ZipkinUiAutoConfiguration;
import zipkin.server.EnableZipkinServer;

import java.util.concurrent.ThreadPoolExecutor;

@ComponentScan(basePackages={"org.cloud.console.server","org.cloud.console.plugins"})
@EnableEurekaClient
@ServletComponentScan
@SpringBootApplication
@EnableZipkinServer
@EnableAutoConfiguration(exclude={ZipkinUiAutoConfiguration.class})
public class CloudConsoleServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudConsoleServerApplication.class, args);
    }
    @Bean
    public ThreadPoolTaskExecutor serviceTaskThread(){
        ThreadPoolTaskExecutor taskExecutor=new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1);//核心线程数
        taskExecutor.setMaxPoolSize(1);//最大线程数
        taskExecutor.setQueueCapacity(2000);//队列最大长度
        taskExecutor.setKeepAliveSeconds(60);//线程池维护线程所允许的空闲时间
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//线程池对拒绝任务(无线程可用)的处理策略
        return taskExecutor;
    }
    @Bean
    public ThreadPoolTaskExecutor healthTaskThread(ServiceInfoService serviceInfoService){
        ThreadPoolTaskExecutor taskExecutor=new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);//核心线程数
        taskExecutor.setMaxPoolSize(20);//最大线程数
        taskExecutor.setQueueCapacity(2000);//队列最大长度
        taskExecutor.setKeepAliveSeconds(60);//线程池维护线程所允许的空闲时间
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//线程池对拒绝任务(无线程可用)的处理策略
        return taskExecutor;
    }
    @Bean
    public ThreadPoolTaskExecutor notifyTaskThread(){
        ThreadPoolTaskExecutor taskExecutor=new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(5);//核心线程数
        taskExecutor.setMaxPoolSize(20);//最大线程数
        taskExecutor.setQueueCapacity(2000);//队列最大长度
        taskExecutor.setKeepAliveSeconds(60);//线程池维护线程所允许的空闲时间
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());//线程池对拒绝任务(无线程可用)的处理策略
        return taskExecutor;
    }

    @Bean
    public EurekaHttpClient eurekaHttpClient(@Autowired EurekaClient eurekaClient, @Value("${eureka.client.serviceUrl.defaultZone}")String eurekaUrl){
        InstanceInfo my=eurekaClient.getApplicationInfoManager().getInfo();
        JerseyEurekaHttpClientFactory jerseyEurekaHttpClientFactory=JerseyEurekaHttpClientFactory.create(eurekaClient.getEurekaClientConfig(),
                null,my,new EurekaClientIdentity(my.getId(),my.getInstanceId()));
        return jerseyEurekaHttpClientFactory.newClient(new DefaultEndpoint(eurekaUrl));
    }

    @Bean
    public ServletRegistrationBean servletRegistration2Bean(){
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new MyDruidProxyServlet(),"/proxy/druid/*");
        servletRegistrationBean.addInitParameter("targetUri","http://localhost/druid");
        servletRegistrationBean.addInitParameter(ProxyServlet.P_LOG, "true");
        return servletRegistrationBean;
    }
    @Bean
    public FilterRegistrationBean registration(HiddenHttpMethodFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setEnabled(false);
        return registration;
    }

}

