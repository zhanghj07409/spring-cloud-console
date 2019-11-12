package org.cloud.console.server.service;

import com.alibaba.fastjson.JSONObject;
import org.cloud.console.server.config.WebsockeConfig;
import org.cloud.console.server.enums.NotifyTypeEnum;
import org.cloud.console.server.enums.WebsocketTypeEnum;
import org.cloud.console.server.vo.NotifyMsgVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/22<br>
 * <br>
 */
@Component
@ServerEndpoint(value = "/webSocket", configurator = WebsockeConfig.class)
public class WebsocketService {
    private static final Logger log=LoggerFactory.getLogger(WebsocketService.class);
    private static CopyOnWriteArraySet<Session> defaultSession=new CopyOnWriteArraySet<>();
    private static CopyOnWriteArraySet<Session> jobDetailSession=new CopyOnWriteArraySet<>();

    public void sendMsg(NotifyMsgVo vo) throws IOException {
        if(vo==null||vo.getNotifyTypeEnum().equals(NotifyTypeEnum.EMAIL)){
            return;
        }
        switch (WebsocketTypeEnum.toEnum(vo.getSubtype())){
            case JOB_DETAIL:jobDetailSend(vo);break;
            case DEFAULT:defaultSend(vo);break;
            default:defaultSend(vo);;
        }
    }
    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        try {
            String user= (String) config.getUserProperties().get("LoginUser");
            if(user==null){
                session.getBasicRemote().sendText("用户未登录关闭连接!!!");
                log.info("用户未登录关闭连接!!sessionid:"+session.getId());
                session.close();
                return;
            }
            log.info("onOpen:"+user);
            addSesson(session);
        } catch (IOException e) {
            log.error("onOpen error",e);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("onError",error);
    }


    @OnClose
    public void onClose(Session session) {
        log.info("session 关闭,sessionId:"+session.getId()+"\tuser:"+getUser(session));
        removeSesson(session);
    }

    @OnMessage
    public void onMessage(Session session,String message) {
        log.info("user:"+getUser(session)+ "\t发来消息:"+ message);
    }

    private void addSesson(Session session) {
        List<String> typeList=session.getRequestParameterMap().get("type");
        if(typeList!=null&&!typeList.isEmpty()){
            String type=typeList.get(0);
            switch (WebsocketTypeEnum.toEnum(type)){
                case DEFAULT: defaultSession.add(session);break;
                case JOB_DETAIL: jobDetailSession.add(session);break;
                default: defaultSession.add(session);break;
            }
        }else{
            defaultSession.add(session);
        }
    }
    private void removeSesson(Session session) {
        defaultSession.remove(session);
        jobDetailSession.remove(session);
    }
    private String getUser(Session session){
        String userId= (String) session.getUserProperties().get("LoginUser");
        if(userId!=null){
            return userId.toString();
        }else{
            return null;
        }
    }

    private void defaultSend(NotifyMsgVo vo) throws IOException {
        for(Session session:defaultSession){
            String msg=JSONObject.toJSONString(vo);
            session.getBasicRemote().sendText(msg);
            log.info("sendMsg:"+msg);
        }

    }
    private void jobDetailSend(NotifyMsgVo vo) throws IOException {
        String msg=JSONObject.toJSONString(vo);
        for(Session session:jobDetailSession){
            List<String> joiName=session.getRequestParameterMap().get("jobName");
            if(joiName!=null&&!joiName.isEmpty()&&joiName.contains(vo.getBusinessKey())){
                session.getBasicRemote().sendText(msg);
                log.info("jobDetail sendMsg:"+msg);
            }
        }
    }

}
