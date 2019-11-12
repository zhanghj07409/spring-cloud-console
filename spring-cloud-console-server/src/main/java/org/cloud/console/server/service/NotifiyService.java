package org.cloud.console.server.service;

import org.cloud.console.server.vo.NotifyMsgVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;

/**
 * 功能说明: 消息通知类<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/3/15<br>
 * <br>
 */
@Service
public class NotifiyService {
    Logger log=LoggerFactory.getLogger(NotifiyService.class);
    @Autowired
    private EmailService emailService;
    @Autowired
    private WebsocketService websocketService;

    @Autowired
    private ThreadPoolTaskExecutor notifyTaskThread;

//    /**
//     * 发送通知消息
//     * @param msg
//     * @param type
//     */
//    public void sendMsg(String msg,String type){
//        //TODO 发送通知消息
//        log.info("通知消息:"+msg+"\ttype:"+type);
//    }
    /**
     * 发送通知消息
     * @param vo
     */
    public void sendMsg(NotifyMsgVo vo){
        if(vo==null){
            return;
        }
        switch (vo.getNotifyTypeEnum()){
            case WEBSOCKET:webNotify(vo);break;
            case ALL:allNotify(vo);break;
            case EMAIL:enmailNotify(vo);break;
            default:allNotify(vo);
        }
    }
    public void allNotify(NotifyMsgVo vo){
        webNotify(vo);
        enmailNotify(vo);
    }
    public void enmailNotify(NotifyMsgVo vo){
        notifyTaskThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    emailService.sendSimpleMail(vo.getTitle(),vo.getMsg());
                } catch (MessagingException e) {
                    log.error("邮件通知失败",e);
                }
            }
        });

    }
    public void webNotify(NotifyMsgVo vo){
        notifyTaskThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    websocketService.sendMsg(vo);
                } catch (IOException e) {
                    log.error("网页通知失败",e);
                }
            }
        });
    }
}
