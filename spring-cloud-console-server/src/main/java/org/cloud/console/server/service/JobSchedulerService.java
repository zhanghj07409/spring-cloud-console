package org.cloud.console.server.service;

import org.cloud.console.server.controller.JobController;
import org.cloud.console.server.entity.JobEntity;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/10<br>
 * <br>
 */
@Service
public class JobSchedulerService {

    @Autowired
    @Qualifier("scheduler")
    private Scheduler scheduler;
    private static Logger log = LoggerFactory.getLogger(JobController.class);

    /**
     * 新增任务
     * @param jobEntity
     * @throws Exception
     */
    public void addAndStartJob(JobEntity jobEntity) throws Exception {
        // 启动调度器
        scheduler.start();
        //构建job信息
        JobDetail jobDetail = JobBuilder.newJob(jobEntity.getJobClass()).withIdentity(jobEntity.getJobName(), jobEntity.getGroupName()).build();
        jobDetail.getJobDataMap().put("param",jobEntity.getParam());
        jobDetail.getJobDataMap().put("jobName",jobEntity.getJobName());
        //表达式调度构建器(即任务执行的时间)
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobEntity.getCronExpression());
        //按新的cronExpression表达式构建一个新的trigger
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(jobEntity.getJobName(), jobEntity.getGroupName())
                .withSchedule(scheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, trigger);
    }

    /**
     * 暂停任务
     * @param jobEntity
     * @throws Exception
     */
    public void jobPause(JobEntity jobEntity) throws Exception {
        scheduler.pauseJob(JobKey.jobKey(jobEntity.getJobName(),jobEntity.getGroupName()));
    }

    /**
     * 启动任务
     * @param jobEntity
     * @throws Exception
     */
    public void jobresume(JobEntity jobEntity) throws Exception {
        scheduler.resumeJob(JobKey.jobKey(jobEntity.getJobName(), jobEntity.getGroupName()));
    }

    /**
     * 更新任务执行时间
     * @param jobEntity
     * @throws Exception
     */
    public void jobreschedule(JobEntity jobEntity) throws Exception {
        TriggerKey triggerKey = TriggerKey.triggerKey(jobEntity.getJobName(), jobEntity.getGroupName());
        // 表达式调度构建器
        CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(jobEntity.getCronExpression());
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
        // 按新的cronExpression表达式重新构建trigger
        trigger = trigger.getTriggerBuilder().withIdentity(triggerKey).withSchedule(scheduleBuilder).build();
        trigger.getJobDataMap().put("param",jobEntity.getParam());
        trigger.getJobDataMap().put("jobName",jobEntity.getJobName());
        // 按新的trigger重新设置job执行
        scheduler.rescheduleJob(triggerKey, trigger);
    }

    /**
     * 删除任务
     * @param jobEntity
     * @throws Exception
     */
    public void jobdelete(JobEntity jobEntity) throws Exception {
        scheduler.pauseTrigger(TriggerKey.triggerKey(jobEntity.getJobName(), jobEntity.getGroupName()));
        scheduler.unscheduleJob(TriggerKey.triggerKey(jobEntity.getJobName(), jobEntity.getGroupName()));
        scheduler.deleteJob(JobKey.jobKey(jobEntity.getJobName(), jobEntity.getGroupName()));
    }

   /* private void test(){
        GroupMatcher<JobKey> gm = GroupMatcher.groupEquals("job-group");
        Set<JobKey> set = scheduler.getJobKeys(gm);
        int state = scheduler.getTriggerState(triggerName, triggerGroup);
        state的值代表该任务触发器的状态：
        STATE_BLOCKED 	4   阻塞
        STATE_COMPLETE 	2   完成
        STATE_ERROR 	3   错误
        STATE_NONE 	-1   不存在
        STATE_NORMAL 	0  正常
        STATE_PAUSED 	1  暂停
    }*/
}
