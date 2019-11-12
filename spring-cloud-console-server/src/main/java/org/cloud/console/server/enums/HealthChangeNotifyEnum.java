package org.cloud.console.server.enums;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/5/9<br>
 * <br>
 */
public enum HealthChangeNotifyEnum {
    ALL("ALL","所有状态变化都记录"),
    UP_TO_DOWN("upToDown","只记录up变成down的"),
    DOWN_TO_UP("downToUp","端口检测"),
    ;

    String key;
    String value;

    HealthChangeNotifyEnum(String key, String value) {
        this.key=key;
        this.value=value;
    }

    public String getKey() {
        return key;
    }
    public String getValue() {
        return value;
    }
    public static HealthChangeNotifyEnum toEnum(String s) {
        for (HealthChangeNotifyEnum e : HealthChangeNotifyEnum.values()) {
            if (e.getKey().equalsIgnoreCase(s)) {
                return e;
            }
        }
        return ALL;
    }
}
