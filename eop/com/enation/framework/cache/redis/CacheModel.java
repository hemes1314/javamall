package com.enation.framework.cache.redis;

/**
 * 为业务系统提供数据接口的参数模型  
 * @author Administrator
 * @version 1.0
 * @created 24-9月-2015 13:15:53
 */
public class CacheModel implements java.io.Serializable {

	/**
	 * 缓存关键字<font color="#010101">key</font>
	 */
	private String key;
	/**
	 * 0,数据类型缓存:1文件类型缓存
	 */
	private String cacheType;
	/**
	 * 需要被缓存的<font color="#010101"> </font>对象json类型
	 */
	private String jsonClazz;
	/**
	 * 0：否 1： 是 根据cacheType 当缓存类型为1；即文件缓存时判断是否上传到文件服务器
	 */
	private String isUpload;
	
 	

	public CacheModel(){

	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getCacheType() {
		return cacheType;
	}

	public void setCacheType(String cacheType) {
		this.cacheType = cacheType;
	}

	public String getJsonClazz() {
		return jsonClazz;
	}

	public void setJsonClazz(String jsonClazz) {
		this.jsonClazz = jsonClazz;
	}

	public String getIsUpload() {
		return isUpload;
	}

	public void setIsUpload(String isUpload) {
		this.isUpload = isUpload;
	}

	 

	 

}