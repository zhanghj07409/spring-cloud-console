package org.cloud.console.server.enums;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/10<br>
 * <br>
 */
public enum JobTypeEnum {
    PING("ping","ping"),
    GET("get","网址检测"),
    SOCKET("socket","端口检测"),
    ;


    String key;
    String value;

    JobTypeEnum(String key, String value) {
        this.key=key;
        this.value=value;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    public static JobTypeEnum toEnum(String s) {
        for (JobTypeEnum e : JobTypeEnum.values()) {
            if (e.getKey().equalsIgnoreCase(s)) {
                return e;
            }
        }
        return PING;
    }
}
