package org.cloud.console.server.enums;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/10<br>
 * <br>
 */
public enum NotifyTypeEnum {
    ALL("all","所有渠道通知"),
    EMAIL("email","邮件通知"),
    WEBSOCKET("websocket","网页通知"),
    ;


    String key;
    String value;

    NotifyTypeEnum(String key, String value) {
        this.key=key;
        this.value=value;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    public static NotifyTypeEnum toEnum(String s) {
        for (NotifyTypeEnum e : NotifyTypeEnum.values()) {
            if (e.getKey().equalsIgnoreCase(s)) {
                return e;
            }
        }
        return ALL;
    }
}
