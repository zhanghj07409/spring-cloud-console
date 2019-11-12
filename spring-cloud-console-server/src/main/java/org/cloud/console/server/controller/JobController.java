package org.cloud.console.server.controller;

import org.cloud.console.server.service.JobService;
import org.cloud.console.server.vo.JobVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/10<br>
 * <br>
 */
@RestController
@RequestMapping("/job")
public class JobController {
    @Autowired
    private JobService jobService;

    private static Logger log = LoggerFactory.getLogger(JobController.class);


    @PostMapping(value = "/addjob")
    public Map addjob(JobVo vo){
        Map map=new HashMap();
        try {
            jobService.addAndStartJob(vo.toJobEntity());
            map.put("success",true);
        } catch (Exception e) {
            log.error("新增任务失败",e);
            map.put("success",false);
            map.put("msg",e.getMessage());
        }
        return map;
    }


    @PostMapping(value = "/pausejob")
    public Map pausejob( @RequestBody String[] jobNames) throws Exception {
        Map map=new HashMap();
        map.put("success",true);
        int succ=0;
        int fail=0;
        for(String jobName:jobNames){
            try {
                jobService.jobPause(jobName);
                succ++;
            } catch (Exception e) {
                log.error("暂停任务失败",e);
                fail++;
            }
        }
        map.put("msg","成功暂停:"+succ+"个任务\t暂停失败:"+fail+"个任务");
        return map;
    }

    @PostMapping(value = "/resumejob")
    public Map resumejob( @RequestBody String[] jobNames) throws Exception {
        Map map=new HashMap();
        int succ=0;
        int fail=0;
        map.put("success",true);
        for(String jobName:jobNames){
            try {
                jobService.jobresume(jobName);
                succ++;
            } catch (Exception e) {
                log.error("启动任务失败",e);
                fail++;
            }
        }
        map.put("msg","成功启动:"+succ+"个任务\t启动失败:"+fail+"个任务");
        return map;
    }


    @PostMapping(value = "/reschedulejob")
    public Map rescheduleJob(JobVo vo){
        Map map=new HashMap();
        try {
            jobService.jobreschedule(vo.toJobEntity());
            map.put("success",true);
        } catch (Exception e) {
            log.error("更新任务失败",e);
            map.put("success",false);
            map.put("msg",e.getMessage());
        }
        return map;
    }


    @PostMapping(value = "/deletejob")
    public Map deletejob(@RequestBody String[] jobNames) throws Exception {
        Map map=new HashMap();
        int succ=0;
        int fail=0;
        map.put("success",true);
        for(String jobName:jobNames){
            try {
                jobService.jobdelete(jobName);
                succ++;
            } catch (Exception e) {
                log.error("删除任务失败",e);
                fail++;
            }
        }
        map.put("msg","成功删除:"+succ+"个任务\t删除失败:"+fail+"个任务");
        return map;
    }
    @GetMapping(value = "/getAllJob")
    public Map getAllJob() throws Exception {
        return jobService.getAllJob();
    }
}
