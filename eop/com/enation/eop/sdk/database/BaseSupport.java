package com.enation.eop.sdk.database;

import org.apache.log4j.Logger;

import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.IDaoSupport;

public abstract class BaseSupport<T> {
	protected final Logger logger = Logger.getLogger(getClass());

	private IDBRouter baseDBRouter;
	protected IDaoSupport<T> baseDaoSupport;
	protected IDaoSupport<T> daoSupport;

	/**
	 * 获取表名
	 * 
	 * @return
	 */
	protected String getTableName(String moude) {
		return baseDBRouter.getTableName(moude);

	}
 

	public IDaoSupport<T> getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport<T> baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}

	public void setDaoSupport(IDaoSupport<T> daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}
}
