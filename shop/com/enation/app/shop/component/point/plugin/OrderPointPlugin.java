package com.enation.app.shop.component.point.plugin;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.Delivery;
import com.enation.app.shop.core.model.DeliveryItem;
import com.enation.app.shop.core.model.FreezePoint;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.model.support.OrderPrice;
import com.enation.app.shop.core.plugin.cart.ICountPriceEvent;
import com.enation.app.shop.core.plugin.order.IAfterOrderCreateEvent;
import com.enation.app.shop.core.plugin.order.IOrderCanelEvent;
import com.enation.app.shop.core.plugin.order.IOrderPayEvent;
import com.enation.app.shop.core.plugin.order.IOrderRestoreEvent;
import com.enation.app.shop.core.plugin.order.IOrderReturnsEvent;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;

/**
 * 订单积分插件
 * @author kingapex
 * @date 2011-10-27 上午10:32:52 
 * @version V1.0
 */

@Component
public class OrderPointPlugin extends AutoRegisterPlugin implements
		IAfterOrderCreateEvent ,IEveryDayExecuteEvent,IOrderPayEvent,IOrderCanelEvent,IOrderRestoreEvent,IOrderReturnsEvent,ICountPriceEvent {

	private IDaoSupport daoSupport; 
	private IMemberPointManger memberPointManger;
	private IMemberManager memberManager;
	
	
	/**
	 * 下单之后写入冻结积分表
	 */
	@Override
	public void onAfterOrderCreate(Order order,List<CartItem>   itemList, String sessionid) {
		//如果是会员购买发放积分
		if(order.getMember_id()!=null){
			//如果是b2b2c
			if(EopSetting.PRODUCT.equals("b2b2c")){
				Map map = this.daoSupport.queryForMap("select parent_id from es_order where order_id = ? ",order.getOrder_id());
				//如果是父订单
				if(map.get("parent_id")==null||map.get("parent_id").equals(0)){
					return;
				}
			}
			Member member  = memberManager.get(order.getMember_id());
			/**
			 * --------------------------------------
			 * 增加会员积分--商品价格*倍数(倍数在设置处指定)
			 * --------------------------------------
			 */
			if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_BUYGOODS) ){
				int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_BUYGOODS+"_num");
				int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_BUYGOODS+"_num_mp");
				point = order.getGoods_amount().intValue() * point;
				mp = order.getGoods_amount().intValue() * mp;
				FreezePoint freezePoint= new FreezePoint();
				freezePoint.setMemberid(order.getMember_id());
				freezePoint.setPoint(point);
				freezePoint.setMp(mp);
				freezePoint.setType(IMemberPointManger.TYPE_BUYGOODS);
				freezePoint.setOrderid(order.getOrder_id()); 
				this.memberPointManger.addFreezePoint(freezePoint,member.getName());
			}
			
			/**
			 * --------------------------------------
			 * 增加会员积分-商品设定的积分)
			 * --------------------------------------
			 */
//			Map map  = this.getGoodsPoint(sessionid);
//			int goodspoint = 0;
//			if(EopSetting.DBTYPE.equals("1")){
//				goodspoint = ((Double)map.get("point")).intValue(); 
//			}else{
//				goodspoint = ((Integer)map.get("point")).intValue();
//			}
//			
//			FreezePoint freezeGoodsPoint= new FreezePoint();
//			freezeGoodsPoint.setMemberid(order.getMember_id());
//			freezeGoodsPoint.setPoint(goodspoint);
//			freezeGoodsPoint.setMp(goodspoint);
//			freezeGoodsPoint.setType(IMemberPointManger.TYPE_BUYGOODS);
//			freezeGoodsPoint.setOrderid(order.getOrder_id()); 
//			this.memberPointManger.addFreezePoint(freezeGoodsPoint,member.getName());
//			
			/**
			 * -----------------------------------------------------------
			 * 如果此会员是被推荐的，找到推荐人，并给增加推广冻结消费积分 (积分数在设置处指定)
			 * -----------------------------------------------------------
			 */
			if(memberPointManger.checkIsOpen(IMemberPointManger.TYPE_REGISTER_LINK) ){
				if(member.getParentid()!=0 && member.getRecommend_point_state()==0){ //有推荐人并且还没有完成推荐积分
					int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_REGISTER_LINK+"_num");
					int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_REGISTER_LINK+"_num_mp");
					FreezePoint freezePoint= new FreezePoint();
					freezePoint.setMemberid(member.getParentid());
					freezePoint.setChildid(order.getMember_id());
					freezePoint.setPoint(point);
					freezePoint.setMp(mp);
					freezePoint.setType(IMemberPointManger.TYPE_REGISTER_LINK);
					freezePoint.setOrderid(order.getOrder_id());
					this.memberPointManger.addFreezePoint(freezePoint,member.getName());
				}
			}
		}

	}
	
	/**
	 * 15天之后，如果此订单已经收货，则解冻积分(从冻结积分表删掉记录，将积分增加到会员表，否则，光删除冻结积分表的记录即可)
	 */
	@Override
	public void everyDay() {
		
		//查出小当前日期 15天的冻结积分
		List<FreezePoint> list = this.memberPointManger.listByBeforeDay(15);
		for(FreezePoint fp:list){
			
			//如果订单状态为已收货或已完成，发放冻结积分给相应会员
			if( fp.getOrder_status() == OrderStatus.ORDER_ROG ||fp.getOrder_status() == OrderStatus.ORDER_COMPLETE ){
				this.memberPointManger.thaw(fp,false);
			}
		}		
		
	}
	
	
	/**
	 * 选择网上支付１０积分。
	 * 下单之后写入冻结积分表，15天之后，如果此订单已经收货，则解冻积分(从冻结积分表删掉记录，将积分增加到会员表，否则，光删除冻结积分表的记录即可)
	 */
	@Override
	public void pay(Order order, boolean isOnline) {
		if(order.getMember_id()!=null){
			Member member  = memberManager.get(order.getMember_id());
			if(isOnline){
				 
				if ( memberPointManger.checkIsOpen(IMemberPointManger.TYPE_ONLINEPAY) ){
					int point = memberPointManger.getItemPoint(IMemberPointManger.TYPE_ONLINEPAY+"_num");
					int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_ONLINEPAY+"_num_mp");
					FreezePoint freezePoint= new FreezePoint();
					freezePoint.setMemberid(order.getMember_id());
					freezePoint.setPoint(point);
					freezePoint.setMp(mp);
					freezePoint.setType(IMemberPointManger.TYPE_ONLINEPAY);
					freezePoint.setOrderid(order.getOrder_id()); 
					this.memberPointManger.addFreezePoint(freezePoint,member.getName());
				}
			}
		}
	}
	/**
	 * 取消订单 删除冻结积分
	 * @param order
	 */
	@Override
	public void canel(Order order) {
		if(order!=null && order.getOrder_id()!=null){
			this.memberPointManger.deleteByOrderId(order.getOrder_id());
		}
	}
	

	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}


	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}


	public IMemberManager getMemberManager() {
		return memberManager;
	}


	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
 

 

	@Override
	public void restore(Order order) {
		//如果是会员购买发放积分
				if(order.getMember_id()!=null){
					Member member  = memberManager.get(order.getMember_id());
					/**
					 * --------------------------------------
					 * 增加会员积分--商品价格*倍数(倍数在设置处指定)
					 * --------------------------------------
					 */
					if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_BUYGOODS) ){
						int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_BUYGOODS+"_num");
						int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_BUYGOODS+"_num_mp");
						point = order.getGoods_amount().intValue() * point;
						mp = order.getGoods_amount().intValue() * mp;
						FreezePoint freezePoint= new FreezePoint();
						freezePoint.setMemberid(order.getMember_id());
						freezePoint.setPoint(point);
						freezePoint.setMp(mp);
						freezePoint.setType(IMemberPointManger.TYPE_BUYGOODS);
						freezePoint.setOrderid(order.getOrder_id()); 
						this.memberPointManger.addFreezePoint(freezePoint,member.getName());
					}
					
					
				
					
					
					/**
					 * -----------------------------------------------------------
					 * 如果此会员是被推荐的，找到推荐人，并给增加推广冻结消费积分 (积分数在设置处指定)
					 * -----------------------------------------------------------
					 */
					if(memberPointManger.checkIsOpen(IMemberPointManger.TYPE_REGISTER_LINK) ){
						if(member.getParentid()!=0 && member.getRecommend_point_state()==0){ //有推荐人并且还没有完成推荐积分
							int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_REGISTER_LINK+"_num");
							int mp = memberPointManger.getItemPoint(IMemberPointManger.TYPE_REGISTER_LINK+"_num_mp");
							FreezePoint freezePoint= new FreezePoint();
							freezePoint.setMemberid(member.getParentid());
							freezePoint.setChildid(order.getMember_id());
							freezePoint.setPoint(point);
							freezePoint.setMp(mp);
							freezePoint.setType(IMemberPointManger.TYPE_REGISTER_LINK);
							freezePoint.setOrderid(order.getOrder_id());
							this.memberPointManger.addFreezePoint(freezePoint,member.getName());
						}
					}
					
					
					
				}
	}
	
	
	private int getGoodsPricePoint( Double goodsPrice)
	{
		/**
		 * --------------------------------------
		 * 增加会员积分--商品价格*倍数(倍数在设置处指定)
		 * --------------------------------------
		 */
		if (memberPointManger.checkIsOpen(IMemberPointManger.TYPE_BUYGOODS) ){
			
			int point =memberPointManger.getItemPoint(IMemberPointManger.TYPE_BUYGOODS+"_num");
			point = goodsPrice.intValue() * point;
			return point;
		}
		
		return 0;
	}	
	
	@Override
	public OrderPrice countPrice(OrderPrice orderprice) {
		String sessionid = ThreadContextHolder.getHttpRequest().getSession().getId();
		Map map  = this.getGoodsPoint(sessionid);
		double point = NumberUtils.toDouble(map.get("point").toString()); //商品本身的积分
		double goodsprice = NumberUtils.toDouble(map.get("price").toString()); //商品价格
		
		
		int price_point  = this.getGoodsPricePoint(goodsprice); //商品价格积分
		point = point +price_point;
		orderprice.setPoint((int)point);
		return orderprice;
	}
	
	
	private Map getGoodsPoint(String sessionid){
		StringBuffer sql = new StringBuffer();
		sql.append("select  sum(g.point * c.num) point ,sum( c.price *c.num ) price from  es_cart  c,es_product  p ,es_goods g  where    c.product_id=p.product_id and p.goods_id= g.goods_id and c.session_id=?");

		Map map  = daoSupport.queryForMap(sql.toString(), sessionid);
		return map;
	}
	
	
	@Override
	public void returned(Delivery delivery, List<DeliveryItem> itemList) {
		 this.memberPointManger.deleteByOrderId(  delivery.getOrder_id() );
		 
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
	
	
}
