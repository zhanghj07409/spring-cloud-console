package org.cloud.console.server.config;

/**
 * 功能说明: 插件配置信息<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
public class PluginType {

    private String plugin;
    private String url;
    private String username;
    private String password;
    private String menuname;
    private String menuaddress;
    private PluginSubMenu[] pluginSubMenus;

    public String getPlugin() {
        return plugin;
    }

    public void setPlugin(String plugin) {
        this.plugin = plugin;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMenuname() {
        return menuname;
    }

    public void setMenuname(String menuname) {
        this.menuname = menuname;
    }

    public String getMenuaddress() {
        return menuaddress;
    }

    public void setMenuaddress(String menuaddress) {
        this.menuaddress = menuaddress;
    }

    public PluginSubMenu[] getPluginSubMenus() {
        return pluginSubMenus;
    }

    public void setPluginSubMenus(PluginSubMenu[] pluginSubMenus) {
        this.pluginSubMenus = pluginSubMenus;
    }
}
