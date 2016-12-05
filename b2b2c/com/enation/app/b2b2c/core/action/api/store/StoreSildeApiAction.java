package com.enation.app.b2b2c.core.action.api.store;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.store.IStoreSildeManager;
import com.enation.framework.action.WWAction;
/**
 * 店铺幻灯片API
 * @author LiFenLong
 *
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("storeSilde")
public class StoreSildeApiAction extends WWAction{
	private IStoreSildeManager storeSildeManager;
	private String[] silde_url;
	private String[] store_fs;
	private Integer[] silde_id;
	/**
	 * 修改店铺幻灯片设置
	 * @param silde_id 幻灯片Id,Integer[]
	 * @param store_fs 图片地址,String[]
	 * @param silde_url 映射地址,String[]
	 * @return 返回json串
	 * result 	为1表示调用成功0表示失败
	 */
	public String editStoreSilde(){
		try {
			String homeUrl = "http://www.gomecellar.com/";
			String defaultImg = "fs:/images/s_side.jpg";
		    int errorcount = 0;
		    if(silde_url!=null && silde_url.length>0){
		       for(int i=0; i<silde_url.length; i++){
		    	   if(defaultImg.equals(store_fs[i]) && StringUtils.isBlank(silde_url[i])) {
		        	   silde_url[i] = homeUrl;
		           }
		           if((StringUtils.isNotBlank(silde_url[i]) && silde_url[i].indexOf(homeUrl) != 0)
		        		   || (StringUtils.isBlank(silde_url[i]) && StringUtils.isNotBlank(store_fs[i]))){
	        		   errorcount++;
		               break;
		           }
		       }
		    }
		    // 如果所有轮播图全部清除，则第一张图显示默认图
		    boolean flag = true;
		    for(String storeFs : store_fs) {
		    	if(StringUtils.isNotBlank(storeFs)) {
		    		flag = false;
		    	}
		    }
		    if(flag) {
		    	store_fs[0] = defaultImg;
		    	silde_url[0] = homeUrl;
		    }
		    if(errorcount>0){
		        this.showErrorJson("请检查链接格式！");
		        return this.JSON_MESSAGE;
		    }
			this.storeSildeManager.edit(silde_id, store_fs, silde_url);
			this.showSuccessJson("修改成功");
		} catch (Exception e) {
			this.showErrorJson("修改失败");
		}
		return this.JSON_MESSAGE;
	}
	public IStoreSildeManager getStoreSildeManager() {
		return storeSildeManager;
	}
	public void setStoreSildeManager(IStoreSildeManager storeSildeManager) {
		this.storeSildeManager = storeSildeManager;
	}
	public String[] getSilde_url() {
		return silde_url;
	}
	public void setSilde_url(String[] silde_url) {
		this.silde_url = silde_url;
	}
	public String[] getStore_fs() {
		return store_fs;
	}
	public void setStore_fs(String[] store_fs) {
		this.store_fs = store_fs;
	}
	public Integer[] getSilde_id() {
		return silde_id;
	}
	public void setSilde_id(Integer[] silde_id) {
		this.silde_id = silde_id;
	}
}
