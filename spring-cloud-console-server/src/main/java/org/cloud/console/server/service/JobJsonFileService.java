package org.cloud.console.server.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.cloud.console.server.config.JobConfig;
import org.cloud.console.server.entity.JobEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2019/4/16<br>
 * <br>
 */
@Service
public class JobJsonFileService {
    private static Logger log = LoggerFactory.getLogger(JobJsonFileService.class);
    @Autowired
    private JobConfig jobConfig;

    /**
     * 查询所有数据
     * @return
     * @throws IOException
     */
    public List<JobEntity> selectAll() throws IOException {
        List<JobEntity> list=new ArrayList<>();
        String jsonString=read(jobConfig.getJobFilePath());
        if(StringUtils.isNotBlank(jsonString)){
            list=JSON.parseArray(jsonString,JobEntity.class);
        }
        return list;
    }

    /**
     * 删除某个元素
     * @param jobName
     * @throws IOException
     */
    public void delete(String jobName) throws IOException {
        write(remove(jobName),jobConfig.getJobFilePath());
    }

    /**
     * 从文件中读取所有数据 ,移除指定的任务,并返回 ,不改变文件内容
     * @param jobName
     * @return
     * @throws IOException
     */
    private List<JobEntity> remove(String jobName) throws IOException {
        List<JobEntity> src=selectAll();
        if(jobName==null){
            return src;
        }
        List<JobEntity> dst=new ArrayList<>();
        for(JobEntity jobEntity:src){
            if(!jobName.equals(jobEntity.getJobName())){
                dst.add(jobEntity);
            }
        }
        return dst;
    }

    /**
     * 新增或更新某个任务信息
     * @param jobEntity
     * @throws IOException
     */
    public void addOrUpdate(JobEntity jobEntity) throws IOException {
        List<JobEntity> src=remove(jobEntity.getJobName());
        src.add(jobEntity);
        write(src,jobConfig.getJobFilePath());
    }

    /**
     * 从指定文件读取所有数据
     * @param path
     * @return
     * @throws IOException
     */
    private String read(String path) throws IOException {
        fileCheck(path);
        StringBuilder laststr = new StringBuilder();
        try(FileInputStream fileInputStream = new FileInputStream(path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            BufferedReader reader = new BufferedReader(inputStreamReader)){
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr.append(tempString);
            }
        }catch(IOException e){
            log.error("json 文件读取异常",e);
            throw e;
        }
        return laststr.toString();
    }

    /**
     * 将指定信息写入文件 中
     * @param src
     * @param path
     * @throws IOException
     */
    private void write(Object src, String path) throws IOException {
        fileCheck(path);
        try (FileWriter fw= new FileWriter(path);
             PrintWriter out = new PrintWriter(fw);){
            out.write(JSONObject.toJSONString(src));
            out.println();
        } catch (IOException e) {
            log.error("json 文件读取异常",e);
            throw e;
        }
    }
    private void fileCheck(String path) throws IOException {
        File file=new File(path);
        if(!file.exists()){
            File parentFile=file.getParentFile();
            if(!parentFile.exists()){
                parentFile.mkdirs();
            }
            file.createNewFile();
        }
    }
}
