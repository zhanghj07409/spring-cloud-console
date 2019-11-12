package org.cloud.console.server.job;

import com.alibaba.fastjson.JSONObject;
import org.cloud.console.server.enums.NotifyTypeEnum;
import org.cloud.console.server.enums.WebsocketTypeEnum;
import org.cloud.console.server.service.JobService;
import org.cloud.console.server.service.NotifiyService;
import org.cloud.console.server.util.HttpClientUtil;
import org.cloud.console.server.util.SpringUtil;
import org.cloud.console.server.vo.NotifyMsgVo;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/10<br>
 * <br>
 */
public class HtmlJob implements Job {
    private static Logger log = LoggerFactory.getLogger(HtmlJob.class);
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        NotifiyService notifiyService=SpringUtil.getBean(NotifiyService.class);
        JobDataMap jobDetail=jobExecutionContext.getMergedJobDataMap();
        String jobName=jobDetail.getString("jobName");
        String param=jobDetail.getString("param");
        if(!paramCheck(param)){
            sendParamMissMsg(param,jobName);
        }
        Map map= (Map) JSONObject.parse(param);
        String url=map.get("url").toString();
        try {
            String result=HttpClientUtil.get(url,null);
            JobService jobService=SpringUtil.getBean(JobService.class);
            jobService.refreshResult(jobName,"200");
           sendSuccessMsg(jobName,result);
            log.debug("Html 任务执行\turl:"+url+"\tresult:"+result);
        } catch (HttpClientErrorException e) {
            log.error("Html 任务执行失败\turl:"+url,e);
            JobService jobService=SpringUtil.getBean(JobService.class);
            jobService.refreshResult(jobName,e.getRawStatusCode());
            sendErrorMsg(e,jobName,url);
        }catch (Exception e) {
            JobService jobService=SpringUtil.getBean(JobService.class);
            jobService.refreshResult(jobName,e.getMessage());
            log.error("Html 任务执行失败\turl:"+url,e);
            sendErrorMsg(e,jobName,url);
        }
    }
    private boolean paramCheck(String param){
        if(param==null){
            return false;
        }
        Map map= (Map) JSONObject.parse(param);
        if(map.get("url")==null){
            return false;
        }
        return true;
    }
    private void sendParamMissMsg(String param,String jobName){
        NotifiyService notifiyService=SpringUtil.getBean(NotifiyService.class);
        NotifyMsgVo vo=new NotifyMsgVo();
        vo.setTitle("计划任务 执行参数缺失");
        vo.setBusinessKey(jobName);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("jobName",jobName);
        resultMap.put("param",param);
        resultMap.put("description","计划任务 执行参数缺失");
        vo.setMsg(JSONObject.toJSONString(resultMap));
        vo.setSuccess(false);
        vo.setNotifyTypeEnum(NotifyTypeEnum.ALL);
        vo.setSubtype(WebsocketTypeEnum.JOB_DETAIL.getKey());
        notifiyService.sendMsg(vo);

    }
    private void sendErrorMsg(Exception e,String jobName,String url){
        NotifiyService notifiyService=SpringUtil.getBean(NotifiyService.class);
        NotifyMsgVo vo=new NotifyMsgVo();
        vo.setTitle(jobName+"执行失败");
        vo.setBusinessKey(jobName);
        Map<String,Object> resultMap=new HashMap<>();
        resultMap.put("jobName",jobName);
        resultMap.put("url",url);
        resultMap.put("exception",e);
        resultMap.put("description","计划任务执行异常");
        vo.setMsg(JSONObject.toJSONString(resultMap));
        vo.setNotifyTypeEnum(NotifyTypeEnum.ALL);
        vo.setSuccess(false);
        vo.setSubtype(WebsocketTypeEnum.JOB_DETAIL.getKey());
        notifiyService.sendMsg(vo);

    }
    private void sendSuccessMsg(String jobName,String result){
        NotifiyService notifiyService=SpringUtil.getBean(NotifiyService.class);
        NotifyMsgVo vo=new NotifyMsgVo();
        vo.setTitle(jobName+"执行成功");
        vo.setBusinessKey(jobName);
        vo.setMsg(result);
        vo.setNotifyTypeEnum(NotifyTypeEnum.WEBSOCKET);
        vo.setSubtype(WebsocketTypeEnum.JOB_DETAIL.getKey());
        notifiyService.sendMsg(vo);
    }
}
