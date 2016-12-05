package com.enation.framework.resource;

import java.util.HashMap;
import java.util.Map;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;


/**
 * 资源状态管理
 * @author kingapex
 *2012-3-16下午5:56:51
 */
public class ResourceStateManager {
	
	
	
	
	/**
	 * 单机版时站点部署状态标识
	 * 1为有新的部署，0没有新部署
	 */
	private static int DISPLOY_STATE; 
	
	
	private static Map<String,String>  disployStateMap;
	
	static{
		disployStateMap = new HashMap<String, String>();
	}
	
	/**
	 * 获取是否有新的部署状态
	 * @return
	 */
	public static boolean getHaveNewDisploy(){
			return DISPLOY_STATE==1;
	}

	
	
	/**
	 * 设置部署状态 
	 * @param state
	 */
	public  static void setDisplayState(int state){
			DISPLOY_STATE= state;
	}
	
	
	
}
