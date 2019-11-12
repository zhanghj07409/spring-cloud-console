package org.cloud.console.server.vo;

import org.cloud.console.server.entity.JobEntity;
import org.cloud.console.server.enums.JobTypeEnum;
import org.cloud.console.server.job.HtmlJob;
import org.cloud.console.server.job.PingJob;
import org.cloud.console.server.job.SocketJob;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/10<br>
 * <br>
 */
public class JobVo {
    private String jobName;
    private String cronExpression;
    private String type;
    private String param;

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }


    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParam() {
        return param;
    }

    public void setParam(String param) {
        this.param = param;
    }
    public JobEntity toJobEntity(){
        JobEntity entity=new JobEntity();
        JobTypeEnum jobTypeEnum=JobTypeEnum.toEnum(type);
        entity.setCronExpression(cronExpression);
        entity.setJobName(jobName);
        entity.setParam(param);
        entity.setType(jobTypeEnum);
        switch (jobTypeEnum){
            case GET:entity.setJobClass(HtmlJob.class);break;
            case PING:entity.setJobClass(PingJob.class);break;
            case SOCKET:entity.setJobClass(SocketJob.class);break;
        }
        entity.setGroupName(jobTypeEnum.getKey());
        return  entity;
    }
}
