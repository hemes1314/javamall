package com.enation.eop.sdk.database;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.enation.framework.database.IDBRouter;
import com.enation.framework.util.StringUtil;

/**
 * 简单的分表方式SAAS数据路由器<br/>
 * 由模块名连接上用户id形成表名
 * 
 * @author kingapex
 *         <p>
 *         2009-12-31 下午12:10:05
 *         </p>
 * @version 1.0
 */
public class DBRouter implements IDBRouter {
	
	public static final String EXECUTECHAR = "!-->";
	protected final Logger logger = Logger.getLogger(getClass());
	private JdbcTemplate jdbcTemplate;

	// 表前缀
	private String prefix;

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	 

	public String getTableName(String moudle) {
		String result = StringUtil.addPrefix(moudle, prefix);
		return result;
 
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

}
