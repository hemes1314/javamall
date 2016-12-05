package com.enation.app.b2b2c.component;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.IDaoSupport;
@Component
/**
 * b2b2c组件
 */
public class B2b2cComponent implements IComponent{
	private IDaoSupport daoSupport;
	@Override
	public void install() {
		DBSolutionFactory.dbImport("file:com/enation/app/b2b2c/component/b2b2c_install.xml", "es_");
		
		//修改后台显示菜单
		daoSupport.execute("update es_index_item set url='/b2b2c/admin/b2b2cIndexItem!order.do' where id=2 ");
	}

	@Override
	public void unInstall() {
		DBSolutionFactory.dbImport("file:com/enation/app/b2b2c/component/b2b2c_uninstall.xml", "es_");
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
