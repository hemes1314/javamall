package com.enation.app.shop.mobile.util;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.util.EntityUtils;

/**
 * HTTP操作工具类.
 * 
 * @author baoxiufeng
 */
@SuppressWarnings("deprecation")
public class HttpUtils {

    /** HttpClient客户端 */
	private static DefaultHttpClient httpClient;

    /**
     * 获取HTTP客户端工具.
     * 
     * @return HTTP客户端工具
     */
    public static DefaultHttpClient getClient() {
        if (httpClient == null) {
            synchronized(DefaultHttpClient.class) {
                if (httpClient == null) {
                    httpClient = new DefaultHttpClient();
                    httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(0, false));
                }
            }
        }
        return httpClient;
    }
    
    /**
     * 返回JSONP格式串.
     * 
     * @param json 原始JSON串
     * @return JSONP格式串
     */
    public static String jsonp(String callback, String json) {
        return String.format(callback + "(%s)", json);
    }
    
    /**
     * 调用HTTP-POST请求.
     * 
     * @param httpPost POST请求对象
     * @return POST请求应答
     * @throws IOException IO异常
     */
    public static HttpResponse execute(HttpPost httpPost) throws Exception {
        return HttpUtils.getClient().execute(httpPost);
    }
    
    /**
     * POST请求处理.
     *
     * @param url 请求URL
     * @param params 请求参数
     * @return 请求结果
     * @throws IOException IO异常
     */
    public static String post(String url, String params) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Content-type", "application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(params, "UTF-8"));
        HttpResponse response = getClient().execute(httpPost);
        return EntityUtils.toString(response.getEntity());
    }

    /**
     * GET请求处理.
     *
     * @param url 请求URL
     * @param params 请求参数
     * @return 请求结果
     * @throws IOException IO异常
     */
    public static String get(String url, List<NameValuePair> params) throws Exception {
        HttpGet httpGet = new HttpGet(url + "?" + URLEncodedUtils.format(params, "UTF-8"));
        httpGet.addHeader("Content-type", "application/json; charset=utf-8");
        httpGet.setHeader("Accept", "application/json");
        HttpResponse response = getClient().execute(httpGet);
        return EntityUtils.toString(response.getEntity());
    }
    
    /**
     * 取WAP客户端IP.
     *
     * @param request 客户端请求
     * @return 客户端IP
     */
    public static String getIpAddr(HttpServletRequest request) {
        if (request == null) return "127.0.0.1";
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        if (StringUtils.isNotBlank(ip)) {
            String[] ipArr = ip.split(" |,");
            if (ipArr != null && ipArr.length > 0) {
                ip = ipArr[0];
            }
        }
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }
}
