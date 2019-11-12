package org.cloud.console.server.enums;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/10<br>
 * <br>
 */
public enum WebsocketTypeEnum {
    DEFAULT("default","默认"),
    JOB_DETAIL("jobDetail","任务详情")
    ;


    String key;
    String value;

    WebsocketTypeEnum(String key, String value) {
        this.key=key;
        this.value=value;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    public static WebsocketTypeEnum toEnum(String s) {
        for (WebsocketTypeEnum e : WebsocketTypeEnum.values()) {
            if (e.getKey().equalsIgnoreCase(s)) {
                return e;
            }
        }
        return DEFAULT;
    }

    public static void main(String[] args) {
        System.out.println(WebsocketTypeEnum.toEnum(null).getKey());
    }
}
