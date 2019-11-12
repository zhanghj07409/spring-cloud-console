package org.cloud.console.server.service;

import org.cloud.console.server.constant.JobStatusConstants;
import org.cloud.console.server.entity.JobEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/16<br>
 * <br>
 */
@Service
public class JobService {

    @Autowired
    private JobJsonFileService jobJsonFileService;
    @Autowired
    private JobSchedulerService jobSchedulerService;

    private Map<String,JobEntity> map=new HashMap<>();
    /**
     * 新增任务
     * @param jobEntity
     * @throws Exception
     */
    public void addAndStartJob(JobEntity jobEntity) throws Exception {
        JobEntity entity=map.get(jobEntity.getJobName());
        if(entity!=null){
            throw new Exception("任务名重复");
        }
        jobSchedulerService.addAndStartJob(jobEntity);
        jobEntity.setJobStatus(JobStatusConstants.START);
        map.put(jobEntity.getJobName(),jobEntity);
        jobJsonFileService.addOrUpdate(jobEntity);
    }

    /**
     * 暂停任务
     * @param jobName
     * @throws Exception
     */
    public void jobPause(String jobName) throws Exception {
        JobEntity entity=map.get(jobName);
        if(entity==null){
            throw new Exception("任务不存在");
        }
        entity.setJobStatus(JobStatusConstants.STOP);
        jobSchedulerService.jobPause(entity);
    }

    /**
     * 启动任务
     * @param jobName
     * @throws Exception
     */
    public void jobresume(String jobName) throws Exception {
        JobEntity entity=map.get(jobName);
        if(entity==null){
            throw new Exception("任务不存在");
        }
        entity.setJobStatus(JobStatusConstants.START);
        jobSchedulerService.jobresume(entity);
    }

    /**
     * 更新任务
     * @param jobEntity
     * @throws Exception
     */
    public void jobreschedule(JobEntity jobEntity) throws Exception {
        JobEntity entity=map.get(jobEntity.getJobName());
        if(entity==null){
            throw new Exception("任务不存在");
        }
        entity.setParam(jobEntity.getParam());
        entity.setCronExpression(jobEntity.getCronExpression());
        jobSchedulerService.jobreschedule(entity);
        jobJsonFileService.addOrUpdate(entity);
    }

    /**
     * 删除任务
     * @param jobName
     * @throws Exception
     */
    public void jobdelete(String jobName) throws Exception {
        JobEntity entity=map.get(jobName);
        if(entity==null){
            throw new Exception("任务不存在");
        }
        jobSchedulerService.jobdelete(entity);
        map.remove(jobName);
        jobJsonFileService.delete(jobName);
    }

    /**
     * 将json 文件内容加载到进任务中
     * @throws Exception
     */
    public void init() throws Exception {
        List<JobEntity>list= jobJsonFileService.selectAll();
        for(JobEntity entity:list){
            jobSchedulerService.addAndStartJob(entity);
            entity.setJobStatus(JobStatusConstants.START);
            map.put(entity.getJobName(),entity);
        }
    }

    /**
     * 更新 任务执行结果
     * @param jobName
     * @param result
     */
    public void refreshResult(String jobName,Object result){
        JobEntity jobEntity= map.get(jobName);
        if(jobEntity!=null){
            jobEntity.setResult(result);
            jobEntity.setLastRunTime(new Date());
        }
    }

    public Map getAllJob() {
        Map resultMap=new HashMap();
        List<JobEntity> list=new ArrayList<>();
        Map<String,JobEntity> copyMap=new HashMap();
        copyMap.putAll(map);
        for(String key:copyMap.keySet()){
            list.add(copyMap.get(key));
        }
        resultMap.put("rows",list);
        resultMap.put("total",list.size());
        return resultMap;
    }
}
