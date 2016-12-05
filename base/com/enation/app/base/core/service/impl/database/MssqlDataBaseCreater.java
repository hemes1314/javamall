package com.enation.app.base.core.service.impl.database;

import com.enation.app.base.core.service.IDataBaseCreater;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.ISqlFileExecutor;


/**
 * mssql数据库创建者
 * @author kingapex
 *
 */
public class MssqlDataBaseCreater implements IDataBaseCreater {
	private  ISqlFileExecutor sqlFileExecutor;
	public void create() {
			
		sqlFileExecutor.execute("file:com/enation/eop/eop_mssql.sql");
		sqlFileExecutor.execute("file:com/enation/app/shop/javashop_mssql.sql");
		sqlFileExecutor.execute("file:com/enation/app/cms/cms_mssql.sql");		
	}
	public ISqlFileExecutor getSqlFileExecutor() {
		return sqlFileExecutor;
	}
	public void setSqlFileExecutor(ISqlFileExecutor sqlFileExecutor) {
		this.sqlFileExecutor = sqlFileExecutor;
	}
}
