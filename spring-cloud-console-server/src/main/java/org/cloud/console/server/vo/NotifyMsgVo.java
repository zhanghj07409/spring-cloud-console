package org.cloud.console.server.vo;

import org.cloud.console.server.enums.NotifyTypeEnum;

import java.time.LocalDateTime;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/23<br>
 * <br>
 */
public class NotifyMsgVo {
    private NotifyTypeEnum notifyTypeEnum=NotifyTypeEnum.ALL;
    private String subtype;
    private String title;
    private String msg;
    private String date=LocalDateTime.now().toString();
    private String businessKey;
    private boolean isSuccess =true;
    public NotifyTypeEnum getNotifyTypeEnum() {
        return notifyTypeEnum;
    }

    public void setNotifyTypeEnum(NotifyTypeEnum notifyTypeEnum) {
        this.notifyTypeEnum = notifyTypeEnum;
    }
    public void setNotifyTypeEnum(String notifyTypeEnum) {
        this.notifyTypeEnum =NotifyTypeEnum.toEnum(notifyTypeEnum);
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBusinessKey() {
        return businessKey;
    }

    public void setBusinessKey(String businessKey) {
        this.businessKey = businessKey;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
