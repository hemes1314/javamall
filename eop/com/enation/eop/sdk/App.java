package com.enation.eop.sdk;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;

/**
 * App基类，提供基础变量及函数
 * 
 * @author lzy
 * 
 */

public abstract class App implements IApp {
	protected final Logger logger = Logger.getLogger(getClass());

	protected boolean dataOnly = true; // 导出建表语句开关，默认仅导出数据

	protected List<String> tables;

	protected int userid;

	protected int siteid;

	/**
	 * 判断是否需要导出数据，继承类可重写
	 * 
	 * @param table
	 * @return
	 */
	protected boolean exceptTable(String table) {
		table = table.toLowerCase();

		if (table.startsWith("eop_"))
			return true;
		if (table.endsWith("menu") && !table.endsWith("site_menu"))
			return true;
		if (table.endsWith("themeuri"))
			return true;
		if (table.endsWith("theme"))
			return true;
		if (table.endsWith("admintheme"))
			return true;

		return false;
	}

	/**
	 * 字符串列表转成数组
	 * 
	 * @param list
	 * @return
	 */
	protected String[] toArray(List<String> list) {
		String[] values = new String[list.size()];
		return list.toArray(values);
	}

	public App() {
		tables = new ArrayList<String>();
	}

	protected void doInstall(String xmlFile) {
		if (this.logger.isDebugEnabled()) {
			this.logger.debug(getName() + " 开始执行 " + xmlFile + "...");
		}
		DBSolutionFactory.dbImport(xmlFile, "es_");
		/*
		 * if("1".equals(EopSetting.DBTYPE))
		 * sqlFileExecutor.execute("file:com/enation/app/base/base_mysql.sql");
		 * else if("2".equals(EopSetting.DBTYPE))
		 * sqlFileExecutor.execute("file:com/enation/app/base/base_oracle.sql");
		 */
		if (this.logger.isDebugEnabled()) {
			this.logger.debug(getName() + " 执行 " + xmlFile + "成功！");
		}
 
	}

 

	public String dumpXml() {
		List<String> dataTable = new ArrayList<String>();
		for (int i = 0, len = tables.size(); i < len; i++) {
			String table = tables.get(i);
			if (!exceptTable(table))
				dataTable.add(table);
		}
		StringBuffer xml = new StringBuffer();
		xml.append(DBSolutionFactory.dbExport(toArray(dataTable), dataOnly,	"es_"));
		return xml.toString();
	}
}
