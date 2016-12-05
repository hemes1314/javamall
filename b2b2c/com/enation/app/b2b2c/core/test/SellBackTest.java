package com.enation.app.b2b2c.core.test;

import org.junit.Before;
import org.junit.Test;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.test.SpringTestSupport;
import com.enation.framework.util.DateUtil;

public class SellBackTest extends SpringTestSupport{
	private IOrderManager orderManager;
	private IMemberManager memberManager;
	private ISellBackManager sellBackManager;
	@Before
	public void mock(){
		memberManager = this.getBean("memberManager");
		sellBackManager = this.getBean("sellBackManager");
		orderManager = this.getBean("orderManager");
       
	}
	@Test
	public void test(){
		//创建退货单
		SellBackList sellBackList=new SellBackList();
		//记录会员信息
		Member member = memberManager.get(1);
		sellBackList.setMember_id(member.getMember_id());
		sellBackList.setSndto(member.getName());
		//订单信息
		Order order = orderManager.get(2007);
		sellBackList.setAdr("收货地址");
		sellBackList.setTradeno(com.enation.framework.util.DateUtil.toString(DateUtil.getDateline(),"yyMMddhhmmss"));//退货单号
		sellBackList.setOrdersn(order.getSn());
		sellBackList.setRegoperator("会员");
		sellBackList.setTel("010-62322212");
		sellBackList.setZip("100000");
		sellBackList.setTradestatus(0);
		sellBackList.setRegtime(DateUtil.getDateline());
		sellBackList.setDepotid(1);
		sellBackList.setRemark("退货申请备注");
		sellBackList.setRefund_way("支付宝");
		sellBackList.setReturn_account("100000");
		//sellBackList.setBill_status(0);
		sellBackList.setAlltotal_pay(1000.00);
		Integer sid = this.sellBackManager.save(sellBackList, true);
		
		SellBackGoodsList sellBackGoods = new SellBackGoodsList();
		//退货申请商品
		sellBackGoods.setRecid(sid);
		sellBackGoods.setPrice(20.00);
		sellBackGoods.setReturn_num(1);
		sellBackGoods.setShip_num(1);
		sellBackGoods.setGoods_id(335);	
		sellBackGoods.setGoods_remark("备注");
		sellBackGoods.setProduct_id(298);
		this.sellBackManager.saveGoodsList(sellBackGoods);
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}
	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}
}
