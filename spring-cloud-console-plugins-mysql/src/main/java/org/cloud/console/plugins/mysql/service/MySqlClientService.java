package org.cloud.console.plugins.mysql.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.cloud.console.plugins.mysql.config.MySqlConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * 功能说明: MySql服务<br>
 * 系统版本: 1.0 <br>
 * 开发人员: huajun.zhang
 * 开发时间: 2019/3/26<br>
 * <br>
 */
@Component
public class MySqlClientService {

    private static final Logger log = LoggerFactory.getLogger(MySqlClientService.class);

    private static MySqlConfig mySqlConfig;

    @Autowired
    public void setMySqlConfig(MySqlConfig mySqlConfig) {
        this.mySqlConfig = mySqlConfig;
    }

    public static Connection getConnection(){
        Connection conn = null;
        String url = mySqlConfig.getUrl();
        String username = mySqlConfig.getUsername();
        String password = mySqlConfig.getPassword();
        String driver = mySqlConfig.getDriver();
        try {
            Class.forName(driver);// 动态加载mysql驱动
            conn = DriverManager.getConnection(url,username,password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return conn;
    }

    public JSONObject getMySql() {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject jNewObject = new JSONObject();
        jNewObject.put("parentId","全局变量");
        jNewObject.put("parentmenuaddress","/mySqlClient/listGlobalVariables");
        jsonArray.add(jNewObject);
        jsonObject.put("rows",jsonArray);
        return jsonObject;
    }

    public JSONObject getGlobalVariables(String queryParam){
        if(queryParam==null){
            queryParam="";
        }
        JSONObject jsonObject = null;
        Connection conn = MySqlClientService.getConnection();
        Statement stmt=null;
        try {
            stmt = conn.createStatement();
            String sql="show global variables like '%"+queryParam+"%'";
            ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
            List<Map> brokersInfoList = new ArrayList<Map>();
            jsonObject = new JSONObject();
            while (rs.next()) {
                Map brokersInfoMap = new HashMap<String,Object>();
                brokersInfoMap.put("variable_name",rs.getString(1));
                brokersInfoMap.put("value",rs.getString(2));
                brokersInfoList.add(brokersInfoMap);
                log.info(rs.getString(1) + "\t" + rs.getString(2));// 入如果返回的是int类型可以用getInt()
            }
            Map brokersMap = new HashMap<String,Object>();
            Map recordKeysMap = new HashMap<String,Object>();
            recordKeysMap.put("recordId","variable_name");
            recordKeysMap.put("recordAttribute1","value");
            brokersMap.put("recordKeys",recordKeysMap);
            brokersMap.put("recordRows",brokersInfoList);
            jsonObject.put("rows",brokersMap);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    public void updateGlobalVariable(String key,String value){
        Connection conn = MySqlClientService.getConnection();
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            String sql ="set global "+key+"="+value;
            int result = stmt.executeUpdate(sql);
            log.info("executeUpdate："+sql+"，执行结果："+result);
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                log.error(e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /*public static void main(String[] args) throws Exception {
        Connection conn = null;
        String sql;
        // MySQL的JDBC URL编写方式：jdbc:mysql://主机名称：连接端口/数据库的名称?参数=值
        // 避免中文乱码要指定useUnicode和characterEncoding
        // 执行数据库操作之前要在数据库管理系统上创建一个数据库，名字自己定，
        // 下面语句之前就要先创建javademo数据库
        String url = "jdbc:mysql://192.168.6.253:3306/scyp-system?useUnicode=true&characterEncoding=utf8&useSSL=false";
        String username = "scyp";
        String password = "Ceying2019";
        try {
            // 之所以要使用下面这条语句，是因为要使用MySQL的驱动，所以我们要把它驱动起来，
            // 可以通过Class.forName把它加载进去，也可以通过初始化来驱动起来，下面三种形式都可以
            Class.forName("com.mysql.jdbc.Driver");// 动态加载mysql驱动
            // or:
            // com.mysql.jdbc.Driver driver = new com.mysql.jdbc.Driver();
            // or：
            // new com.mysql.jdbc.Driver();

            System.out.println("成功加载MySQL驱动程序");
            // 一个Connection代表一个数据库连接
            conn = DriverManager.getConnection(url,username,password);
            // Statement里面带有很多方法，比如executeUpdate可以实现插入，更新和删除等
            Statement stmt = conn.createStatement();
            //sql = "create table student(NO char(20),name varchar(20),primary key(NO))";
            //int result = stmt.executeUpdate(sql);// executeUpdate语句会返回一个受影响的行数，如果返回-1就没有成功
            //if (result != -1) {
                //System.out.println("创建数据表成功");
                //sql = "insert into student(NO,name) values('2012001','陶伟基')";
                //result = stmt.executeUpdate(sql);
                //sql = "insert into student(NO,name) values('2012002','周小俊')";
                //result = stmt.executeUpdate(sql);
                //sql = "show global variables";
            String queryParam="max_connections";
            sql="show global variables like '%"+queryParam+"%'";
            ResultSet rs = stmt.executeQuery(sql);// executeQuery会返回结果的集合，否则返回空值
            while (rs.next()) {
                System.out.println(rs.getString(1) + "\t" + rs.getString(2));// 入如果返回的是int类型可以用getInt()
            }

            *//*String updateParam = "1001";
            sql ="set global max_connections="+updateParam;
            int result = stmt.executeUpdate(sql);
            System.out.println(result);*//*

            //}
        } catch (SQLException e) {
            System.out.println("MySQL操作错误");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.close();
        }
    }*/
}


