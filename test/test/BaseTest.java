package test;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class BaseTest {
    public static final String SITE_URL = "http://localhost:8080";
    
    protected static HttpClient httpClient;
    
    
    protected static PostMethod getPostMethod(String path) {
        PostMethod postMethod = new PostMethod(SITE_URL + path);
        return postMethod;
    }
    
    protected static GetMethod getGetMethod(String path) {
        GetMethod getMethod = new GetMethod(SITE_URL + path);
        return getMethod;
    }
    
    protected static HttpClient getHttpClient() {
        if (httpClient == null) {
            httpClient = buildHttpClient();
        }
        return httpClient;
    }
    
    protected static JSONObject getJson(HttpMethod method) {
        HttpClient httpClient = getHttpClient();
        try {
            httpClient.executeMethod(method);
            String body = new String(method.getResponseBody(), "UTF-8");
            System.out.println(body);
            JSONObject object = JSON.parseObject(body);
            return object;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    protected static HttpClient buildHttpClient() {
        HttpClient httpClient = new HttpClient();
        return httpClient;
    }
}
