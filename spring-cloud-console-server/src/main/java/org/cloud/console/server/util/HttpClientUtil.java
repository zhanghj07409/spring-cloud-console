package org.cloud.console.server.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.HttpClientErrorException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * 功能说明: <br>
 * 系统版本: 1.0 <br>
 * 开发人员: huanghaiyun
 * 开发时间: 2018/6/13<br>
 * <br>
 */
public class HttpClientUtil {
    private static final Logger log = LoggerFactory.getLogger(HttpClientUtil.class);
    /**
     * 创建https 客户端
     * @return
     * @throws Exception
     */
    public static CloseableHttpClient createSSLClientDefault() throws Exception {
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            HostnameVerifier hostnameVerifier = NoopHostnameVerifier.INSTANCE;
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (Exception e) {
            log.error("创建https client 异常",e);
            throw e;
        }

    }
    /**
     *
     * @param url url
     * @param requestParam request param
     * @param header 头信息
     * @return
     */
    public static String get(String url, Map<String, String> requestParam,Map<String, String> header) throws HttpClientErrorException,Exception {
        return send(url,requestParam,null,header,RequestMethod.GET);
    }
    /**
     *
     * @param url url
     * @param requestParam request param
     * @return
     */
    public static String get(String url, Map<String, String> requestParam) throws HttpClientErrorException,Exception {
        return send(url,requestParam,null,null,RequestMethod.GET);
    }
    /**
     *
     * @param url url
     * @param requestParam request param
     * @return
     */
    public static String post(String url, Map<String, String> requestParam) throws HttpClientErrorException,Exception {
        return send(url,requestParam,null,null,RequestMethod.POST);
    }
    /**
     *
     * @param url url
     * @param requestBody requestBody
     * @return
     */
    public static String post(String url,Object requestBody) throws HttpClientErrorException,Exception {
        return send(url,null,requestBody,null,RequestMethod.POST);
    }
    /**
     *
     * @param url url
     * @param requestParam request param
     * @param requestBody request body,只有requestMethod 为post 时生效 否则无效
     * @param header 头信息
     * @param requestMethod RequestMethod.GET/RequestMethod.POST 其他默认为GET
     * @return
     */
    public static String send(String url, Map<String, String> requestParam, Object requestBody, Map<String, String> header, RequestMethod requestMethod) throws HttpClientErrorException,Exception{
        // 创建Httpclient对象
        CloseableHttpClient httpclient=null;
        if(isHttps(url)){
            httpclient = createSSLClientDefault();
        }else{
            httpclient = HttpClients.createDefault();
        }
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);
            if (requestParam != null) {
                for (String key : requestParam.keySet()) {
                    builder.addParameter(key, requestParam.get(key));
                }
            }
            URI uri = builder.build();
            HttpRequestBase httpRequestBase=null;
            switch (requestMethod){
                case GET:{
                    httpRequestBase= new HttpGet(uri);break;
                }case HEAD:{
                    httpRequestBase= new HttpHead(uri);break;
                }case POST:{
                    httpRequestBase= new HttpPost(uri);
                    if(requestBody!=null){
                        StringEntity entity = new StringEntity(JSONObject.toJSONString(requestBody), ContentType.APPLICATION_JSON);
                        ((HttpPost) httpRequestBase).setEntity(entity);
                    }
                    break;
                }case PUT:{
                    httpRequestBase= new HttpPut(uri);
                    if(requestBody!=null){
                        StringEntity entity = new StringEntity(JSONObject.toJSONString(requestBody), ContentType.APPLICATION_JSON);
                        ((HttpPut) httpRequestBase).setEntity(entity);
                    }
                    break;
                }case PATCH:{
                    httpRequestBase= new HttpPatch(uri);
                    if(requestBody!=null){
                        StringEntity entity = new StringEntity(JSONObject.toJSONString(requestBody), ContentType.APPLICATION_JSON);
                        ((HttpPatch) httpRequestBase).setEntity(entity);
                    }
                    break;
                }case DELETE:{
                    httpRequestBase= new HttpDelete(uri);break;
                }case OPTIONS:{
                    httpRequestBase= new HttpOptions(uri);break;
                }case TRACE:{
                    httpRequestBase= new HttpTrace(uri);break;
                }default:{
                    httpRequestBase= new HttpGet(uri);break;
                }
            }
            if(header!=null){
                for(String key:header.keySet()){
                    httpRequestBase.setHeader(key,header.get(key));
                }
            }
            // 执行请求
            response = httpclient.execute(httpRequestBase);
            // 判断返回状态是否为200
            if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
                resultString = EntityUtils.toString(response.getEntity(), "UTF-8");
            }else {
                throw  new HttpClientErrorException(HttpStatus.valueOf(response.getStatusLine().getStatusCode()));
            }
        } catch (HttpClientErrorException e) {
            log.error("返回状态码不对",e);
            throw e;
        }catch (Exception e) {
            log.error("发送http请求失败",e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (Exception e) {
                log.error("发送http请求失败",e);
            }
        }

        return resultString;
    }

    /**
     *
     * @param url url
     * @return
     */
    public static String getHealth(String url) throws Exception{
        // 创建Httpclient对象
        CloseableHttpClient httpclient=null;
        if(isHttps(url)){
            httpclient = createSSLClientDefault();
        }else{
            httpclient = HttpClients.createDefault();
        }
        String resultString = "";
        CloseableHttpResponse response = null;
        try {
            // 创建uri
            URIBuilder builder = new URIBuilder(url);

            URI uri = builder.build();
            HttpRequestBase httpRequestBase=null;

            httpRequestBase= new HttpGet(uri);
            // 执行请求
            response = httpclient.execute(httpRequestBase);
            resultString = EntityUtils.toString(response.getEntity(), "UTF-8");

        }catch (Exception e) {
            log.error("发送http请求失败",e);
            throw e;
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                httpclient.close();
            } catch (Exception e) {
                log.error("发送http请求失败",e);
            }
        }

        return resultString;
    }
    /**
     *  将string 转成对应的 java Bean
     * @param src string
     * @param resultClass java
     * @param <T>
     * @return
     */
    public static <T>T stringToObject(String src,Class<T> resultClass){
        return JSONObject.toJavaObject(JSON.parseObject(src),resultClass);
    }
    private static boolean isHttps(String url){
        return url.startsWith("https");
    }

}
