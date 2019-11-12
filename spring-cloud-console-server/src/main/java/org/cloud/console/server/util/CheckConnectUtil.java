package org.cloud.console.server.util;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/9<br>
 * <br>
 */
public class CheckConnectUtil {
    public static boolean ping(String ipAddress) throws Exception {
        int timeOut = 3000; // 超时应该在3钞以上
        boolean status = InetAddress.getByName(ipAddress).isReachable(timeOut);
        // 当返回值是true时，说明host是可用的，false则不可。
        return status;
    }
    public static boolean isHostConnectable(String host, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(host, port));
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            System.out.println(ping("192.168.2.33"));
            System.out.println(isHostConnectable("www.baidu.com",80));
            System.out.println(HttpClientUtil.get("http://192.168.6.31",null));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
