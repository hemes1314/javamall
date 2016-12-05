package com.enation.app.cms;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.enation.app.cms.core.model.DataModel;
import com.enation.app.cms.core.service.IDataModelManager;
import com.enation.app.cms.core.service.impl.cache.DataCatCacheProxy;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.App;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.ISqlFileExecutor;

/**
 * CMS应用
 * @author kingapex
 *
 */
public class CmsApp extends App {
	private IDBRouter baseDBRouter;
	private IDataModelManager dataModelManager ;
	private SimpleJdbcTemplate simpleJdbcTemplate;
	private ISqlFileExecutor sqlFileExecutor;

	public CmsApp() {
		super();
		tables.add("data_cat");
		tables.add("data_model");
		tables.add("data_field");
	}
	
 
	
	private static  String replaceTbName(String sql,String tbName ){
		
		Pattern p = Pattern.compile("CREATE TABLE `(.*?)`(.*)", 2 | Pattern.DOTALL);
		Matcher m = p.matcher(sql);
		if(m.find()){
			sql = m.replaceAll("CREATE TABLE `"+ tbName + "`$2");
		}
		return sql;
	}

	/**
	 * session失效事件
	 */
	public void sessionDestroyed(String seesionid,EopSite site) {
		//do noting
	}
 
	@Override
	public String dumpXml( ) {
		String xml = super.dumpXml( );
		StringBuffer sql = new StringBuffer();
		List<DataModel> modelList = this.dataModelManager.list();
		List<String> oldTable = new ArrayList<String>();
		oldTable.addAll(tables);
		tables.clear();
		for(DataModel model:modelList){
			tables.add(model.getEnglish_name());
		}
		dataOnly = false;
		xml = xml + super.dumpXml( );
		tables.clear();
		tables.addAll(oldTable);
		return xml;
	}
	
	public String getId() {
		
		return "cms";
	}

	
	public String getName() {
		
		return "cms应用";
	}

	
	public String getNameSpace() {
		
		return "/cms";
	}

	
	public void install() {
		this.doInstall("file:com/enation/app/cms/cms.xml");
	}

	protected void cleanCache(){
		//清除挂件缓存
		CacheFactory.getCache(DataCatCacheProxy.cacheName).remove(DataCatCacheProxy.cacheName+"_"+ userid +"_"+siteid);
	}
	
	
	public ISqlFileExecutor getSqlFileExecutor() {
		return sqlFileExecutor;
	}

	public void setSqlFileExecutor(ISqlFileExecutor sqlFileExecutor) {
		this.sqlFileExecutor = sqlFileExecutor;
	}
	public static void main(String[] args){
		String sql  = "CREATE TABLE `es_table_1_3_4` (  \n `id` mediumint(8) NOT NULL AUTO_INCREMENT,\n`add_time` datetime DEFAULT NULL) \n ENGINE=MyISAM AUTO_INCREMENT=168 DEFAULT CHARSET=utf8;";
		sql = replaceTbName(sql,"es_table_1_<userid>_<siteid>");
		
	}
	
	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}


	public IDataModelManager getDataModelManager() {
		return dataModelManager;
	}

	public void setDataModelManager(IDataModelManager dataModelManager) {
		this.dataModelManager = dataModelManager;
	}
 

	public SimpleJdbcTemplate getSimpleJdbcTemplate() {
		return simpleJdbcTemplate;
	}

	public void setSimpleJdbcTemplate(SimpleJdbcTemplate simpleJdbcTemplate) {
		this.simpleJdbcTemplate = simpleJdbcTemplate;
	}
}
