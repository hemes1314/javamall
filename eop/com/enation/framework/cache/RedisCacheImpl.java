package com.enation.framework.cache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.alibaba.fastjson.JSON;
import com.enation.framework.cache.redis.ByteUtil;
import com.enation.framework.cache.redis.CacheModel;
import com.enation.framework.cache.redis.JsonSatausModel;
import com.enation.framework.cache.redis.ObjectBytesExchangeHelper;
import com.enation.framework.cache.redis.RedisPropertyConfig;

/**
 * Rediscache缓存实现
 * 
 * @author hp
 *
 */
public class RedisCacheImpl implements ICache {

	private net.sf.ehcache.Cache cache;
	private String prekey;

	/**
	 * 
	 */
	public RedisCacheImpl(String name) {
		prekey = name;
	}

	/**
	 * Gets a value of an element which matches the given key.
	 * 
	 * @param key
	 *            the key of the element to return.
	 * @return The value placed into the cache with an earlier put, or null if
	 *         not found or expired
	 * @throws CacheException
	 */
	public Object get(Object key) {

		Object obj = null;
		String str = getUrl(prekey + ":" + (String) key, "getStrObject");
		if (str != null) {
			byte[] bytes = ByteUtil.base64strToBytes(str);
			obj = (Object) ObjectBytesExchangeHelper.toObject(bytes);
		}
		return obj;
	}

	/**
	 * Puts an object into the cache.
	 * 
	 * @param key
	 *            a {@link Serializable} key
	 * @param value
	 *            a {@link Serializable} value
	 * @throws CacheException
	 *             if the parameters are not {@link Serializable}, the
	 *             {@link CacheManager} is shutdown or another {@link Exception}
	 *             occurs.
	 */
	public void put(Object key, Object value) {

		String sValue = "";
		try {
			byte[] by = ObjectBytesExchangeHelper.toByteArray(value);
			sValue = ByteUtil.getBase64Content(by);
			String str = postUrl(prekey + ":" + (String) key, sValue,
					"setStrObject");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Removes the element which matches the key.
	 * <p>
	 * If no element matches, nothing is removed and no Exception is thrown.
	 * 
	 * @param key
	 *            the key of the element to remove
	 * @throws CacheException
	 */
	public void remove(Object key) {
		String str = getUrl(prekey + ":" + (String) key, "delStrObjectByKey");

	}

	/**
	 * Remove all elements in the cache, but leave the cache in a useable state.
	 * 
	 * @throws CacheException
	 */
	public void clear() {
		String str = getUrl(prekey + ":", "delStrObjectByType");

	}

	public static void main(String[] args) {

		RedisCacheImpl cache = new RedisCacheImpl("queryCache");
		cache.put("test", "dddd");
		System.out.println(cache.get("test"));
	}

	public static String postUrl(String key, String value, String method) {
		String url = (String) RedisPropertyConfig.getRedisInfo("redis.url");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url + method);
		//System.out.println(url + method);
		String result = null;
		try {
			result = postHttp(httpClient, postRequest, setStr(key, value));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public static String getUrl(String key, String method) {
		String url = (String) RedisPropertyConfig.getRedisInfo("redis.url");
		DefaultHttpClient httpClient = new DefaultHttpClient();
		HttpGet getRequest = new HttpGet(url + method + "/" + key);
		//System.out.println(url + method + "/" + key);
		String result = null;
		try {
			result = getHttp(httpClient, getRequest);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result != null) {
			JsonSatausModel objectJson = JSON.parseObject(result,
					JsonSatausModel.class);
			if(objectJson!=null){
			    return objectJson.getData();
			}
			else{
				return null;
			}
		} else {
			return null;
		}
	}

	public static String setStr(String key, String value) {
		CacheModel cacheModel = new CacheModel();
		cacheModel.setKey(key);
		cacheModel.setJsonClazz(value);
		String objectJson = JSON.toJSONString(cacheModel);
		return objectJson;
	}
	// get请求
	public static String postHttp(DefaultHttpClient httpClient,
			HttpPost postRequest, String objectJson)
			throws UnsupportedEncodingException, IOException,
			ClientProtocolException {
 		StringEntity input = new StringEntity(objectJson, "UTF-8");
		input.setContentType("application/json;charset=UTF-8");
		input.setContentEncoding("UTF-8");

		postRequest
				.setHeader("Content-Type", "application/json; charset=UTF-8");
		postRequest.setEntity(input);
		HttpResponse response = httpClient.execute(postRequest);
		//System.out.println(response.getAllHeaders());
		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));

		String output;
		String result = "";
		//System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			output = new String(output.getBytes(), "UTF-8");
			//System.out.println(output);
			result += output;
		}

		httpClient.getConnectionManager().shutdown();
		JsonSatausModel objectJsonpost = JSON.parseObject(result,
				JsonSatausModel.class);
		//System.out.println(objectJsonpost.getData() + "xxxxxs");
		return objectJsonpost.getMsg();
	}

	// get请求
	public static String getHttp(DefaultHttpClient httpClient,
			HttpGet getRequest) throws UnsupportedEncodingException,
			IOException, ClientProtocolException {
		getRequest.setHeader("Content-Type", "application/json; charset=utf-8");
		HttpResponse response = httpClient.execute(getRequest);

		if (response.getStatusLine().getStatusCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ response.getStatusLine().getStatusCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(response.getEntity().getContent())));

		String output;
		String result = "";
		//System.out.println("Output from Server .... \n");
		while ((output = br.readLine()) != null) {
			output = new String(output.getBytes(), "UTF-8");
 			result += output;
		}

		httpClient.getConnectionManager().shutdown();
		return result;
	}

}
