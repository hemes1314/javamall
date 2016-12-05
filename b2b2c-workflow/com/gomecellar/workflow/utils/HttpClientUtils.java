package com.gomecellar.workflow.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

import com.enation.framework.util.JsonUtil;

public class HttpClientUtils {
	/**
	 * Httpclient get 请求
	 * @param url
	 * @param paramMap
	 * @return
	 */
	public static String get(String url, Map<String, String> paramMap) {
		//参数变为Json格式
		String json = JsonUtil.MapToJson(paramMap);
		//参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		/*for (String key : paramMap.keySet()) {
			params.add(new BasicNameValuePair(key, paramMap.get(key)));
		}*/
		params.add(new BasicNameValuePair("param", json));
		
		url = url +  "?" + URLEncodedUtils.format(params, HTTP.UTF_8);
		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		String resultJson = "";
		try {
			HttpResponse res = client.execute(get);
			if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = res.getEntity();
				InputStreamReader inputStreamReader =new InputStreamReader(entity.getContent(), HTTP.UTF_8);
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
				String read = new String();
				while ((read = bufferedReader.readLine()) != null) {
					resultJson = read;
				}
				bufferedReader.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			//关闭连接 ,释放资源
			client.getConnectionManager().shutdown();
		}
		return resultJson;
	}
	
	/**
     * Httpclient get 请求 带cookie
     * @param url
     * @param paramMap
     * @return
     */
    public static String get(String url,String cookie, Map<String, String> paramMap) {
    	if (paramMap != null && !paramMap.isEmpty()) {
    		//参数变为Json格式
            String json = JsonUtil.MapToJson(paramMap);
            //参数
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            /*for (String key : paramMap.keySet()) {
                params.add(new BasicNameValuePair(key, paramMap.get(key)));
            }*/
            params.add(new BasicNameValuePair("param", json));
            url = url +  "?" + URLEncodedUtils.format(params, HTTP.UTF_8);
    	}
        HttpClient client = new DefaultHttpClient();
        HttpGet get = new HttpGet(url);
        get.setHeader("Cookie",cookie);
        String resultJson = "";
        try {
            HttpResponse res = client.execute(get);
            if (res.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = res.getEntity();
                InputStreamReader inputStreamReader =new InputStreamReader(entity.getContent(), HTTP.UTF_8);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String read = new String();
                while ((read = bufferedReader.readLine()) != null) {
                    resultJson = read;
                }
                bufferedReader.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally{
            //关闭连接 ,释放资源
            client.getConnectionManager().shutdown();
        }
        return resultJson;
    }
}
