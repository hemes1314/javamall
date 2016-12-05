package com.enation.app.b2b2c.core.test;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.b2b2c.component.plugin.bill.BillStorePlugin;
import com.enation.framework.test.SpringTestSupport;

public class CreateBillTest extends SpringTestSupport{
	private BillStorePlugin billStorePlugin;
	@Before
	public void mock(){
		billStorePlugin = this.getBean("billStorePlugin");
	}
	@Test
	public void test(){
	    BillStorePlugin billStorePlugin = new BillStorePlugin();
		billStorePlugin.everyMonthFifteen();
	}
}
