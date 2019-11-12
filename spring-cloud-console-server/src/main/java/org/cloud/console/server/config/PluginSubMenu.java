package org.cloud.console.server.config;

/**
 * 功能说明: 插件配置信息<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
public class PluginSubMenu {

    private String id;
    private String submenuname;
    private String submenuaddress;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSubmenuname() {
        return submenuname;
    }

    public void setSubmenuname(String submenuname) {
        this.submenuname = submenuname;
    }

    public String getSubmenuaddress() {
        return submenuaddress;
    }

    public void setSubmenuaddress(String submenuaddress) {
        this.submenuaddress = submenuaddress;
    }
}
