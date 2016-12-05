package com.enation.app.base.core.service.dbsolution;

import java.sql.Connection;
import java.sql.SQLException;

import org.springframework.jdbc.core.JdbcTemplate;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;

/**
 * 数据解决方案工厂类
 * 
 * @author liuzy
 * 
 */
public class DBSolutionFactory {
	public static IDBSolution getDBSolution() {
		IDBSolution result = null;
		if (EopSetting.DBTYPE.equals("1")) {
			result = SpringContextHolder.getBean("mysqlSolution");
		} else if (EopSetting.DBTYPE.equals("2")) {
			result = SpringContextHolder.getBean("oracleSolution");
		} else if (EopSetting.DBTYPE.equals("3")) {
			result = SpringContextHolder.getBean("sqlserverSolution");
		} else
			throw new RuntimeException("未知的数据库类型");

		return result;
	}
	
	public static Connection getConnection(JdbcTemplate jdbcTemplate){
		if(jdbcTemplate==null)
			jdbcTemplate =  SpringContextHolder.getBean("jdbcTemplate");
		
		try {
			return jdbcTemplate.getDataSource().getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static boolean dbImport(String xml, String prefix) {
		 
		IDBSolution dbsolution = getDBSolution();
		dbsolution.setPrefix(prefix);
	 
		boolean result;
		result = dbsolution.dbImport(xml);
	 
		return result;
	}
	
	public static String dbExport(String[] tables,boolean dataOnly,String prefix) {
		Connection conn = getConnection(null);
		IDBSolution dbsolution = getDBSolution();
		dbsolution.setPrefix(prefix);
 
		String result = "";
		//if(EopSetting.RUNMODE.equals("1")){
			result = dbsolution.dbExport(tables,dataOnly);
//	/	}else{
//			EopSite site = EopContext.getContext().getCurrentSite();
//			Integer userid = site.getUserid();
//			Integer siteid = site.getId();
//			result = dbsolution.dbSaasExport(tables, dataOnly, userid, siteid);
//		}
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
//	public static boolean dbSaasImport(String xml, int userid, int siteid, String prefix){
//		Connection conn = getConnection(null);
//		IDBSolution dbsolution = getDBSolution();
//		dbsolution.setPrefix(prefix);
//		dbsolution.setConnection(conn);
//		boolean result = dbsolution.dbSaasImport(xml, userid, siteid);
//		try {
//			conn.close();
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return false;
//		}
//		return result;
//	}
}
