package org.cloud.console.plugins.zookeeper.util;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.cloud.console.plugins.zookeeper.config.ZooKeeperConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * 功能说明: ZooKeeper客户端<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@Component
public class SimpleZkClient {

    private static final int SESSION_TIMEOUT = 3000;

    private static ZooKeeperConfig zooKeeperConfig;

    @Autowired
    public void setZooKeeperConfig(ZooKeeperConfig zooKeeperConfig) {
        this.zooKeeperConfig = zooKeeperConfig;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleZkClient.class);


    /**
     * 使用Zookeeper 原生Api 进行操作
     */
    public static ZooKeeper createZookeeperConnection() throws IOException{
        return createZookeeperConnection(zooKeeperConfig.getUrl(),SESSION_TIMEOUT, null);
    }
    
    private static ZooKeeper createZookeeperConnection(String url) throws IOException{
        return createZookeeperConnection(StringUtils.hasText(url)?url:zooKeeperConfig.getUrl(),SESSION_TIMEOUT, null);
    }

    private static ZooKeeper createZookeeperConnection(String url, int sessionTimeout) throws IOException{
        return createZookeeperConnection(StringUtils.hasText(url)?url:zooKeeperConfig.getUrl(),sessionTimeout, null);
    }

    public static ZooKeeper createZookeeperConnection(String zkNodes, int sessionTimeout, Watcher watcher){
        ZooKeeper zooKeeper = null;
        try {
            zooKeeper =  new ZooKeeper(zkNodes, sessionTimeout, watcher == null?null:new Watcher() {
                public void process(WatchedEvent watchedEvent) {
                    System.out.println("zookeeper defaultWatcher is not realization !");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = "创建ZooKeeper 客户端失败：" + e.getMessage();
            LOGGER.error(errMsg);
            throw new RuntimeException("zk 初始化失败：" + errMsg);
        }
        return zooKeeper;
    }

    /**
     * 获取zookeeper目录下子目录
     * @param path
     * @param watcher
     * @return
     * @throws Exception
     */
    public static List<String> getChildren(ZooKeeper zooKeeper, String path, boolean watcher) throws Exception{
        return zooKeeper.getChildren(path, watcher);
    }

    /**
     * 获取zookeeper目录下子目录
     * @param path
     * @return
     * @throws Exception
     */
    public static List<String> getChildren(ZooKeeper zooKeeper, String path) throws Exception{
        return zooKeeper.getChildren(path, false);
    }

    /**
     *
     * @param path
     * @param watcher
     * @return
     * @throws Exception
     */
    public static List<String> getChildren(ZooKeeper zooKeeper, String path, Watcher watcher) throws Exception{
        return zooKeeper.getChildren(path, watcher);
    }

    /**
     * 自定义节点类型创建
     * @param path
     * @param bytes
     * @param createMode
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void createNode(ZooKeeper zooKeeper, String path, byte[] bytes, CreateMode createMode) throws KeeperException, InterruptedException {
        zooKeeper.create(path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    /**
     * 默认节点类型创建
     * @param path
     * @param bytes
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static void createNode(ZooKeeper zooKeeper, String path, byte[] bytes) throws KeeperException, InterruptedException {
        zooKeeper.create(path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
    }

    /**
     * 删除节点
     * @param path
     * @param version
     * @throws InterruptedException
     * @throws KeeperException
     */
    public static void deleteZkNode(ZooKeeper zooKeeper, String path, int version) throws InterruptedException, KeeperException{
        zooKeeper.delete(path, version);
    }

    /**
     * 默认删除节点方法
     * @param path
     * @throws InterruptedException
     * @throws KeeperException
     */
    public static void deleteZkNode(ZooKeeper zooKeeper, String path) throws InterruptedException, KeeperException{
        zooKeeper.delete(path, -1);
    }

    /**
     * 获取节点数据
     * @param path
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static byte[] getData(ZooKeeper zooKeeper, String path) throws KeeperException, InterruptedException {
        return zooKeeper.getData(path, false, null);
    }

    /**
     * 修改节点数据
     * @param path
     * @param bytes
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static Stat setData(ZooKeeper zooKeeper, String path, byte[] bytes) throws KeeperException, InterruptedException {
        return zooKeeper.setData(path, bytes, -1);
    }

    /**
     * 判断是否存在zkNode节点
     * @param path
     * @param watcher
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static boolean existsZkNode(ZooKeeper zooKeeper, String path, boolean watcher) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, watcher)==null?false:true;
    }

    /**
     * 判断是否存在zkNode节点
     * @param path
     * @param watcher
     * @return
     * @throws KeeperException
     * @throws InterruptedException
     */
    public static boolean existsZkNode(ZooKeeper zooKeeper, String path, Watcher watcher) throws KeeperException, InterruptedException {
        return zooKeeper.exists(path, watcher)==null?false:true;
    }

    /**
     * 关闭zooKeeper连接
     */
    public static void releaseConnection(ZooKeeper zooKeeper) {
        if (zooKeeper != null) {
            try {
                zooKeeper.close();
            } catch (InterruptedException e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException("zk 关闭失败：" + e.getMessage());
            }
        }
    }

    /**
     * 创建ZkClient 进行操作
     */
    public static ZkClient createZkClientConnection() {
        return createZkClientConnection(zooKeeperConfig.getUrl(), SESSION_TIMEOUT);
    }

    public static ZkClient createZkClientConnection(int sessionTimeout) {
        return createZkClientConnection(zooKeeperConfig.getUrl(), sessionTimeout);
    }

    public static ZkClient createZkClientConnection(String zkNodes, int sessionTimeout){
        ZkClient zkClient = null;
        try {
            zkClient =  new ZkClient(new ZkConnection(zkNodes), SESSION_TIMEOUT);
        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = "创建ZooKeeper 客户端失败：" + e.getMessage();
            LOGGER.error(errMsg);
            throw new RuntimeException("zk 初始化失败：" + errMsg);
        }
        return zkClient;
    }

    public static List<String> getChildren(ZkClient zkClient, String path){
        return zkClient.getChildren(path);
    }

    public static String createNode(ZkClient zkClient, String path, byte[] bytes, CreateMode createMode){
        return zkClient.create(path, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, createMode);
    }

    public static void deleteZkNode(ZkClient zkClient, String path){
        zkClient.delete(path, -1);
    }

    public static void deleteZkNode(ZkClient zkClient, String path, int version){
        zkClient.delete(path, version);
    }

    public static byte[] getData(ZkClient zkClient, String path){
        return zkClient.readData(path);
    }

    public static void setData(ZkClient zkClient, String path, byte[] bytes){
        zkClient.writeData(path, bytes);
    }

    public static boolean existsZkNode(ZkClient zkClient, String path){
        return zkClient.exists(path);
    }

    /**
     * 关闭ZkClient连接
     */
    public static void releaseConnection(ZkClient zk) {
        if (zk != null) {
            try {
                zk.close();
            } catch (ZkInterruptedException e) {
                e.printStackTrace();
                LOGGER.error(e.getMessage(), e);
                throw new RuntimeException("zk 关闭失败：" + e.getMessage());
            }
        }
    }



}
