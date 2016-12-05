package com.enation.app.shop.core.service.impl;

import java.util.List;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.model.MemberLv;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.core.model.FreezePoint;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.PointHistory;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPointHistoryManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员积分管理
 * @author kingapex
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
public class MemberPointManger extends BaseSupport implements IMemberPointManger {
	private IPointHistoryManager pointHistoryManager;
	private IMemberManager memberManager;
	private IMemberLvManager memberLvManager;
	private ISettingService settingService ;
	private IOrderManager orderManager;
	
	/**
	 * 解冻积分
	 */
	public void thaw(FreezePoint fp,boolean ismanual){
		
		String reson  = "";
		if( IMemberPointManger.TYPE_REGISTER_LINK.equals( fp.getType()) ){
				reson ="推荐会员购买商品完成，积分解冻";	
		}

		if( IMemberPointManger.TYPE_BUYGOODS.equals( fp.getType()) ){
			if(ismanual){
				reson ="购买商品,用户提前解冻积分";
			}else{
				reson ="购买商品满15天积分解冻";	
			}
			
		}
		
		if( IMemberPointManger.TYPE_ONLINEPAY.equals( fp.getType()) ){
			if(ismanual){
				reson ="在线支付购买商品,用户提前解冻积分";
			}else{
				reson ="在线支付购买商品满15天积分解冻";
			}
		}
		
		this.add(fp.getMemberid(), fp.getPoint(),reson, fp.getOrderid(), fp.getMp()); //给会员增加相应积分
		this.baseDaoSupport.execute("delete from freeze_point where id=?", fp.getId()); //删除冻结积分
		
	
		if( IMemberPointManger.TYPE_REGISTER_LINK.equals(fp.getType() )){
			//更新被推荐人为推荐积分完成状态
			this.baseDaoSupport.execute("update member set recommend_point_state=1 where member_id=?", fp.getChildid());
		}
		
 
		
	}
	
	/**
	 * 为某个订单解冻积分
	 * @param orderId
	 */
	public void thaw(Integer orderId){
		
		Order order = orderManager.get(orderId);
		OrderLog orderLog = new OrderLog();
		if(order == null){
			throw new RuntimeException("对不起，此订单不存在！");
		}
		//是否为未支付
        if(order.getPay_status()==OrderStatus.PAY_NO){
            throw new RuntimeException("对不起，订单未付款不能解冻！");
        }
		if(order.getStatus() == null || ( order.getStatus().intValue() != OrderStatus.ORDER_ROG  &&   order.getStatus().intValue() !=  OrderStatus.ORDER_COMPLETE ) ){
			throw new RuntimeException("对不起，此订单不能解冻！");
		}
		if(UserConext.getCurrentAdminUser()==null){
			Member member = UserConext.getCurrentMember();
			if(order.getMember_id().intValue() != member.getMember_id().intValue()){
				throw new RuntimeException("对不起，您没有权限进行此项操作！");
			}
			orderLog.setMessage("用户["+member.getUname()+"]解冻订单["+orderId+"]积分，并将订单置为完成状态");
			orderLog.setOp_name(member.getUname());
		}else{
			orderLog.setMessage("管理员["+UserConext.getCurrentAdminUser().getRealname()+"]解冻订单["+orderId+"]积分，并将订单置为完成状态");
		}
		List<FreezePoint> list = this.listByOrderId(orderId);
		for(FreezePoint fp:list){
			
			//手动解冻收货订单的冻结积分
			if( fp.getOrder_status() == OrderStatus.ORDER_ROG  || order.getStatus().intValue() ==  OrderStatus.ORDER_COMPLETE ) {
				this.thaw(fp,true);
			}
		}
	
		orderLog.setOp_id(0L);
		if(orderLog.getOp_name()==null){ 
	        orderLog.setOp_name("admin");
		}
		orderLog.setOp_time(DateUtil.getDateline());
		orderLog.setOrder_id(orderId);
		this.baseDaoSupport.insert("order_log", orderLog);
		long unix_timestamp = DateUtil.getDateline();
		String sql = "update order set status ="+OrderStatus.ORDER_COMPLETE+",complete_time="+unix_timestamp+" where order_id ="+orderId;
		////System.out.println(sql);
		this.baseDaoSupport.execute(sql);
	}
	
	
	
	private List<FreezePoint>  listByOrderId(Integer orderId){
		String sql ="select fp.*,o.status as order_status from "+this.getTableName("freeze_point")+"  fp inner join "+this.getTableName("order")+" o on fp.orderid= o.order_id  where o.order_id=?";
		return this.daoSupport.queryForList(sql, FreezePoint.class,orderId);  
	}
	
	
	
	/**
	 * 读取由当前日期起多少天前的冻结积分
	 * @param beforeDayNum 天数
	 * @return 冻结积分列表
	 */
	public List<FreezePoint> listByBeforeDay(int beforeDayNum){
		int f = beforeDayNum *24*60*60;
//		int f = 1*60*60; //测试改成一个小时后 正是上线后将上面一行恢复即可
		long now  = DateUtil.getDateline(); // DateUtil.getDateline("2011-11-16", "yyyy-MM-dd") ;  //
		String sql ="select fp.*,o.status as order_status from es_freeze_point  fp inner join es_order o on fp.orderid= o.order_id  where  "+now+"-dateline>="+f;
		////System.out.println(sql);
		return this.daoSupport.queryForList(sql, FreezePoint.class);  
	}
		   
	
	/**
	 * 增加会员冻结积分
	 */
	public void addFreezePoint(FreezePoint freezePoint,String memberName){
		
		if(freezePoint== null ) throw new  IllegalArgumentException("param freezePoint is NULL");
		if(freezePoint.getMemberid()==0 ) throw new  IllegalArgumentException("param freezePoint.memberid is zero");
//		if(freezePoint.getMp()==0 && freezePoint.getPoint()==0 ) throw new  IllegalArgumentException("param freezePoint.mp and freezePoint.point is zero");
		
		
		String reson  = "";
		if( IMemberPointManger.TYPE_REGISTER_LINK.equals( freezePoint.getType()) ){
//			reson ="推荐会员购买商品完成，增加冻结积分";
			reson= IMemberPointManger.TYPE_REGISTER_LINK;
		}

		if( IMemberPointManger.TYPE_BUYGOODS.equals( freezePoint.getType()) ){
//			reson ="购买商品增加冻结积分";
			reson = IMemberPointManger.TYPE_BUYGOODS;
		}
		
		if( IMemberPointManger.TYPE_ONLINEPAY.equals( freezePoint.getType()) ){
//			reson ="在线购买商品冻结积分";
			reson = IMemberPointManger.TYPE_ONLINEPAY;
		}
		
		//不用写记录
//		PointHistory pointHistory = new  PointHistory();
//		pointHistory.setMember_id(freezePoint.getMemberid());
//		pointHistory.setOperator(memberName);
//		pointHistory.setPoint(freezePoint.getPoint());
//		pointHistory.setReason(reson);
//		pointHistory.setType(1);
//		pointHistory.setTime(System.currentTimeMillis());
//		pointHistory.setRelated_id(freezePoint.getOrderid());
//		pointHistory.setMp(freezePoint.getMp());		
//		pointHistoryManager.addPointHistory(pointHistory);
		
		freezePoint.setDateline(DateUtil.getDateline());
		this.baseDaoSupport.insert("freeze_point", freezePoint);
		
	}
	
	/**
	 * 查询某个会员的所有冻结积分 
	 * @param memberid
	 * @return
	 */
	public int getFreezeMpByMemberId(long memberid){
		return this.baseDaoSupport.queryForInt("SELECT SUM(mp) FROM freeze_point WHERE memberid=?", memberid);
	}
	
	public int getFreezePointByMemberId(long memberid){
		return this.baseDaoSupport.queryForInt("SELECT SUM(point) FROM freeze_point WHERE memberid=?", memberid);
	}
	
	public boolean checkIsOpen(String itemname) {
		String value = settingService.getSetting("point", itemname);
		
		return "1".equals(value);
	}

	 
	public int getItemPoint(String itemname) {
		String value = settingService.getSetting("point", itemname);
		if(StringUtil.isEmpty(value)){
			return 0;
		}
		return Integer.valueOf(value);
	}


	
	@Transactional(propagation = Propagation.REQUIRED)  
	public void add(long memberid, int point, String itemname,Integer relatedId, int mp) {
		PointHistory pointHistory = new  PointHistory();
		pointHistory.setMember_id(memberid);
		pointHistory.setOperator("member");
		pointHistory.setPoint(point);
		pointHistory.setReason(itemname);
		pointHistory.setType(1);
		pointHistory.setTime(DateUtil.getDateline());
		pointHistory.setRelated_id(relatedId);
		pointHistory.setMp(0);
		pointHistory.setPoint_type(0);
        //lxl 添加剩余积分   
        pointHistory.setTotal_point(memberManager.get(memberid).getPoint());
        pointHistoryManager.addPointHistory(pointHistory);//添加等级积分
        pointHistory.setPoint_type(1); 
        pointHistory.setPoint(0);
        pointHistory.setMp(mp);
        pointHistoryManager.addPointHistory(pointHistory);//添加消费积分
        
		
		Member member  = this.memberManager.get(memberid);
		if(member==null){
			logger.debug("获取用户失败 memberid is "+memberid);
			//System.out.println("获取用户失败memberid is"+memberid);
			this.baseDaoSupport.execute("delete from freeze_point where memberid=?", memberid); //删除冻结积分 免得下次扫描到
		}else{
			Integer memberpoint  = member.getPoint();
	
			
			this.baseDaoSupport.execute("update member set point=point+?,mp=mp+? where member_id=?", point, mp, memberid); 
			
			//改变会员等级
			if(memberpoint!=null ){
				MemberLv lv =  this.memberLvManager.getByPoint(memberpoint + point);
				if(lv!=null ){
					if((member.getLv_id()==null ||lv.getLv_id().intValue()>member.getLv_id().intValue())){
						this.memberManager.updateLv(memberid, lv.getLv_id());
					} 
				}
			}
			
		}
	}
	
	/**
	 * 根据订单 删除冻结积分
	 * @param orderId
	 */
	public void deleteByOrderId(Integer orderId){
		String sql = "delete from freeze_point where orderid="+orderId;
		this.baseDaoSupport.execute(sql);
	}


	public IPointHistoryManager getPointHistoryManager() {
		return pointHistoryManager;
	}


	public void setPointHistoryManager(IPointHistoryManager pointHistoryManager) {
		this.pointHistoryManager = pointHistoryManager;
	}


	public ISettingService getSettingService() {
		return settingService;
	}


	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}


	public IMemberManager getMemberManager() {
		return memberManager;
	}


	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}


	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}


	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}


	@Override
	@Transactional(propagation = Propagation.REQUIRED)  
	public void useMarketPoint(long memberid,int point,String reson,Integer relatedId){
		PointHistory pointHistory = new  PointHistory();
		pointHistory.setMember_id(memberid);
		pointHistory.setOperator("member");
		pointHistory.setPoint(point);
		pointHistory.setReason(reson);
		pointHistory.setType(0);
		pointHistory.setPoint_type(1);
		pointHistory.setTime(DateUtil.getDateline());
		pointHistory.setRelated_id(relatedId);
		
		pointHistoryManager.addPointHistory(pointHistory);
		this.baseDaoSupport.execute("update member set mp=mp-? where member_id=?", point,memberid); 
	}


	@Override
	public Double pointToPrice(int point) {
		
		return Double.valueOf(point);
	}
	
	@Override
	public int priceToPoint(Double price) {
		if(price == null) return 0;
		return price.intValue();
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	
	

}
