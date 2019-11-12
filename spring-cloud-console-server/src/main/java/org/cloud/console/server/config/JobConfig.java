package org.cloud.console.server.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/16<br>
 * <br>
 */
@Configuration
@ConfigurationProperties(prefix = "jobScheduler")
public class JobConfig {
    private String jobFilePath;

    public String getJobFilePath() {
        return jobFilePath;
    }

    public void setJobFilePath(String jobFilePath) {
        this.jobFilePath = jobFilePath;
    }
}
