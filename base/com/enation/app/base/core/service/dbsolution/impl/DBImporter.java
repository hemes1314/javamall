package com.enation.app.base.core.service.dbsolution.impl;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;

/**
 * 数据库导入类
 * 
 * @author liuzy
 * 
 */
public class DBImporter extends DBPorter {
	private Document xmlDoc;

	public DBImporter(DBSolution solution) {
		super(solution);
	}

	/**
	 * 加载xml文件
	 * 
	 * @param xmlFile
	 * @return
	 */
	private Document loadDocument(String xmlFile) throws DocumentException {
		Document document = null;
		SAXReader saxReader = new SAXReader();
		File file = new File(xmlFile);
		if (file.exists())
			document = saxReader.read(new File(xmlFile));
		return document;
	}

	private List<Object> prepareValue(String values) {
		List<Object> objects = new ArrayList<Object>();
		String[] value = values.split(",");
		for (int i = 0; i < value.length; i++) {
			objects.add(solution.getFuncValue(solution.decodeValue(value[i]
					.replaceAll("'", ""))));
		}

		return objects;
	}

	private void doExecute(Statement state, String sql){
		try {
			if(sql!=null)
				state.execute(sql);
		} catch (SQLException e) {

		}
	}
	


	private Object parseValue(String value) {
		return solution.getFuncValue(solution.decodeValue(value
				.replaceAll("'", "")));
	}

	
	private boolean doInsert(Element action) {
		
		final String table = solution.getTableName(action.elementText("table"));
		String fields = action.elementText("fields");
		String values = action.elementText("values");
		

		final String[] field_ar= fields.split(",");
		final String[] value_ar= values.split(",");
		Map data = new HashMap();
		
		IDaoSupport daoSupport =  SpringContextHolder.getBean("daoSupport");
		try{
		for (int i = 0; i < field_ar.length; i++){
			data.put(field_ar[i], this.parseValue( value_ar[i]));
			
		}
		
		if (solution.beforeInsert(table, fields, values)) {
			String sql =solution.getSqlExchange();
			if(!StringUtil.isEmpty(sql)){
				daoSupport.execute(sql);
			}
			
			daoSupport.insert(table, data);
			solution.afterInsert(table, fields, values);
			
			sql =solution.getSqlExchange();
			if(!StringUtil.isEmpty(sql)){
				daoSupport.execute(sql);
			}
			
		} else {
			return false;
		}
		
		
		} catch (RuntimeException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private boolean doTruncate(Element action) {
		String table = solution.getTableName(action.elementText("table"));
		String sql="" ;
		sql+="truncate table "+table;
		return solution.executeSqls(sql);
	}
	private boolean doDrop(Element action) {
		String table = solution.getTableName(action.elementText("table"));
		String sql = solution.getDropSQL(table);
		return solution.executeSqls(sql);
	}
	
	private boolean doCreate(Element action) {
		String sql = solution.getCreateSQL(action);
		
		/*
        File sqlFile = new File("d:\\1.sql");
        try {
            //记录所有的简表语句，注意DBSolution。EXECUTECHAR，也就是!-->是用来分割的，没有用。在最后的sql里面替换掉即可。
            FileUtils.writeStringToFile(sqlFile, sql + "\n", true);
        } catch(IOException e) {
            //e.printStackTrace();
        }//*/
		
        return solution.executeSqls(sql);
	}
	
	private boolean doIndex(Element action) {
		return doIndex(action,0,0);
	}
	private boolean doUnindex(Element action) {
		return doUnindex(action,0,0);
	}
	
	
	
	private boolean doTruncate(Element action, int userid, int siteid) {
		String table = solution.getSaasTableName(action.elementText("table"), userid, siteid);
		String sql="" ;
		sql+="truncate table "+table;
		return solution.executeSqls(sql);
	}

	private boolean doDrop(Element action, int userid, int siteid) {
		String table = solution.getSaasTableName(action.elementText("table"), userid, siteid);
		String sql = solution.getDropSQL(table);
		return solution.executeSqls(sql);
	}
	
	private boolean doCreate(Element action, int userid, int siteid) {
		String sql = solution.getSaasCreateSQL(action, userid, siteid);
		return solution.executeSqls(sql);
	}
	

	
	
	@SuppressWarnings("unchecked")
	private boolean doIndex(Element action, int userid, int siteid) {
		String sql = "create index ";
		String table;
		if(userid==0 && siteid==0)
			table = solution.getTableName(action.elementText("table"));
		else
			table = solution.getSaasTableName(action.elementText("table"), userid, siteid);
		List<Element> fields = action.elements("field");
		String field = " (";
		String name = "_";
		for(int i=0,len=fields.size();i<len;i++) {
			Element element = fields.get(i);
			field = field + element.elementText("name") + ",";
			name = name + element.elementText("name") + "_";
		}
		field = field.substring(0,field.length()-1) + ")";
		name = name.substring(0,name.length()-1);
		
		sql = sql + "idx" + name + " on " + table + field;
		return solution.executeSqls(sql);
	}
	
	
	private boolean doAlter(Element action,int userid,int siteid){
		
		try{
			String table;
			String sql="" ;
			if(userid==0 && siteid==0)
				table = solution.getTableName(action.elementText("table"));
			else
				table = solution.getSaasTableName(action.elementText("table"), userid, siteid);
			
			List<Element> fields = action.elements("field");
			for(int i=0,len=fields.size();i<len;i++) {
				Element element = fields.get(i);
				String type = element.attributeValue("type");
				String name = element.elementText("name") ;
				String size = element.elementText("size") ;
				
				if(i!=0){
					sql+=",";
				}
				if("add".equals(type)){
					String datatype = element.elementText("type") ;
					
					//oracle和sqlserver的都不写column关键字
					//区分oracle 和 sqlserver 和 mysql， alter语句的不同
					
					if( EopSetting.DBTYPE.equals("2") || EopSetting.DBTYPE.equals("3")){
						sql+=" "+ name +" " ;	
					}else{
						sql+=" add column "+ name +" " ;
					}
					
					sql+=solution.toLocalType(datatype, size);
					
					String def = element.elementText("default") ;
					if(!StringUtil.isEmpty(def)){
						sql+=" default "+def;
					}
				}
				if("drop".equals(type)){
					sql+=" drop column "+ name ;
				} 
			
			}
			
			if( EopSetting.DBTYPE.equals("2") || EopSetting.DBTYPE.equals("3")){
				//System.out.println("修改之前："+sql);
				sql=" add ("+sql+")";
			}
			
			sql ="alter table "+table +" "+sql;
			//System.out.println(sql);
			solution.executeSqls(sql);
			return true;
		}catch(RuntimeException e){
			e.printStackTrace();
			return false;
		}
	}
	
	
	


	private boolean doUnindex(Element action, int userid, int siteid) {
		/*	由于Oracle的drop index 不使用on table，存在差异，加上使用几率低，暂时放弃
		String sql = "drop index ";
		String table;
		if(userid==0 && siteid==0)
			table = solution.getTableName(action.elementText("table"));
		else
			table = solution.getSaasTableName(action.elementText("table"), userid, siteid);
		List<Element> fields = action.elements("field");
		String name = "_";
		for(int i=0;i<fields.size();i++) {
			Element element = fields.get(i);
			name = name + element.elementText("name") + "_";
		}
		name = name.substring(0,name.length()-1);
		sql = sql + "idx" + name + " on " + table;
		return solution.executeSqls(sql);
		*/
		return true;
	}
	/**
	 * 执行action内容
	 * 
	 * @param action
	 * @return
	 */
	private boolean doAction(Element action) {
		String command = action.elementText("command").toLowerCase();
		if ("create".equals(command)) {
			return doCreate(action);
		} else if ("insert".equals(command)) {
			return doInsert(action);
		} else if ("drop".equals(command)) {
			return doDrop(action);
		} else if ("index".equals(command)) {
			return doIndex(action);
		} else if ("unindex".equals(command)) {
			return doUnindex(action);
		} else if("alter".equals(command)){
			return doAlter(action, 0, 0);			
		} else if ("truncate".equals(command)) {
			return doTruncate(action);
		} 
		return true;
	}
	
	
	/**
	 * 导入一个xml文件到数据库中
	 * 
	 * @param xml
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean doImport(String xml) {
		solution.beforeImport();
		try {
			if (xml.startsWith("file:")) {
				xml = FileUtil.readFile(xml.replaceAll("file:", ""));
				xmlDoc = DocumentHelper.parseText(xml);
			} else if (xml.startsWith("<?xml version")) {
				xmlDoc = DocumentHelper.parseText(xml);
			}
			else {
				xmlDoc = loadDocument(xml);
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}
		List<Element> actions = xmlDoc.getRootElement().elements("action");
		for (Element action : actions) {
			if (!doAction(action))
				return false;
		}
		solution.afterImport();
		return true;
	}
	
	
}