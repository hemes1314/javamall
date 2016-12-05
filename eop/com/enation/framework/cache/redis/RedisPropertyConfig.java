package com.enation.framework.cache.redis;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

public  class RedisPropertyConfig {
	private static Map<String,String> authMap= new HashMap<String,String>();
	static{ 
 
		try{
			InputStream in  = FileUtil.getResourceAsStream("redis.properties");
			Properties props = new Properties();
			props.load(in);
				
			String url= props.getProperty("redis.url");
			String on = props.getProperty("redis.on");
	 	
			authMap.put("redis.url", url); //redis url
			authMap.put("redis.on", on); //是否开启redis
 			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static String getRedisInfo(String type){
		return authMap.get(type);
	}
	
}
