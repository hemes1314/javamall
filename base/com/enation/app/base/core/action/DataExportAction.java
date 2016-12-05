package com.enation.app.base.core.action;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.SystemSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 数据导出action
 * @author kingapex
 *2015-5-6下午3:07:46
 */

@ParentPackage("eop_default")
@Namespace("/core/admin")

@Results({
	@Result(name="input", type="freemarker", location="/core/admin/data/export.html")
})
public class DataExportAction extends WWAction {
	
	private String[] b2b2c_tables=new String[]{"es_adv","es_adcolumn","es_smtp","es_goods_cat","es_brand","es_goods_type","es_type_brand","es_goods","es_goods_gallery","es_product","es_product_store","es_store_log","es_goods_spec","es_tags","es_tag_rel","es_member","es_member_lv","es_dly_type","es_logi_company","es_payment_cfg","es_print_tmpl","es_depot","es_data_cat","es_data_model","es_data_field","es_hot_keyword","es_helpcenter","es_store","es_store_silde","es_groupbuy_active","es_groupbuy_area","es_groupbuy_cat","es_groupbuy_goods","es_groupbuy_price","es_store_cat","es_store_template","es_site_menu"};
	private String[] b2c_tables=new String[]{"es_adv","es_adcolumn","es_smtp","es_goods_cat","es_brand","es_goods_type","es_type_brand","es_goods","es_goods_gallery","es_product","es_product_store","es_store_log","es_goods_spec","es_tags","es_tag_rel","es_member","es_member_lv","es_dly_type","es_logi_company","es_payment_cfg","es_print_tmpl","es_depot","es_data_cat","es_data_model","es_data_field","es_hot_keyword","es_helpcenter","es_site_menu"};
	
	
	private int type;
	/**
	 * 转到页面
	 */
	public String execute(){
		
		return this.INPUT;
	}
	
	private String data;
	public String doExport(){
		
		String[] tables =null;
		if(type==0){
			tables= b2c_tables;
		}else{ 
			tables=b2b2c_tables;
		}
		data= DBSolutionFactory.dbExport(tables, true, "");
		data = this.getSettingXml() + data; 
		data ="<?xml version=\"1.0\" encoding=\"UTF-8\"?><dbsolution>"+data+"</dbsolution>";
		data = data.replaceAll(SystemSetting.getStatic_server_domain(), "fs:");
		return "download";
		
	}
	
	
	/**
	 * 
	 * @return
	 */
	private String getSettingXml(){
		
		return	FileUtil.read(StringUtil.getRootPath()+"/core/admin/data/setting.xml", null);
	}
	
	
	 public InputStream getInputStream() {
		 InputStream   in   =   new   ByteArrayInputStream(data.getBytes());  
		 return in;
	 }
	 
	 
	 public String getFileName(){
		 
		 return "data.xml";
	 }

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	 
	 
	 
}
