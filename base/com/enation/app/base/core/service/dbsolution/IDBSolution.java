package com.enation.app.base.core.service.dbsolution;

import java.sql.Connection;
/**
 * 
 * @author liuzy
 * 
 * 数据库导入导出解决方案接口
 * 
 */
public interface IDBSolution {
	
 
	
	public boolean dbImport(String xml);
	public boolean dbExport(String[] tables,String xml);
	public String dbExport(String[] tables,boolean dataOnly);
	public int dropTable(String table);
	public void setPrefix(String prefix);
	public String toLocalType(String type, String size) ;
}
