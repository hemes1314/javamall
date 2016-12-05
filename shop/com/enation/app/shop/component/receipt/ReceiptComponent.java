package com.enation.app.shop.component.receipt;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.ISqlFileExecutor;

/**
 * 发票组件
 * @author kingapex
 *2012-2-6下午9:23:48
 */
@Component
public class ReceiptComponent implements IComponent {
	@Override
	public void install() {
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/receipt/receipt_install.xml","es_");
	}

	@Override
	public void unInstall() {
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/receipt/receipt_uninstall.xml","es_");

	}
}
