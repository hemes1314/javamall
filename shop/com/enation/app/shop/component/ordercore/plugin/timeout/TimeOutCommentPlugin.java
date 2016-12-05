package com.enation.app.shop.component.ordercore.plugin.timeout;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.plugin.job.IEveryDayExecuteEvent;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.MemberComment;
import com.enation.app.shop.core.model.MemberOrderItem;
import com.enation.app.shop.core.service.IMemberCommentManager;
import com.enation.app.shop.core.service.IMemberOrderItemManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.DateUtil;
@Component
/**
 * 收货后7*24小时没有评价的订单自动好评
 * @author LiFenLong
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class TimeOutCommentPlugin extends AutoRegisterPlugin implements IEveryDayExecuteEvent {
	private IDaoSupport daoSupport;
	private IOrderManager orderManager;
	private IMemberCommentManager memberCommentManager;
	private IMemberOrderItemManager memberOrderItemManager;
	private IMemberManager memberManager;
	@Override
	public void everyDay() {
		
		String sql="SELECT tr.* from es_member_order_item mo INNER JOIN es_transaction_record tr ON mo.order_id=tr.order_id WHERE mo.commented=0  and tr.rog_time+?<? GROUP BY tr.record_id ";
		List<Map> list= daoSupport.queryForList(sql,604800,DateUtil.getDateline());
		this.commentOrder(list);
		
	}
	/**
	 * 评论订单
	 * @param list
	 */
	private void commentOrder(List<Map> list){
		MemberComment memberComment = new MemberComment();
		
		for(Map map :list){
			Integer goods_id = NumberUtils.toInt(map.get("goods_id").toString());
			Long member_id = NumberUtils.toLong(map.get("member_id").toString());
			memberComment.setGoods_id(goods_id);
			memberComment.setGrade(5);
			memberComment.setImg(null);
			memberComment.setMember_id(member_id );
			memberComment.setDateline(DateUtil.getDateline());
			memberComment.setType(1);
			memberComment.setContent("真好吃");
			memberComment.setStatus(1);
			try {
				memberCommentManager.add(memberComment);
				//更新为已经评论过此商品
				MemberOrderItem memberOrderItem = memberOrderItemManager.get(member_id,goods_id,1);
				if(memberOrderItem != null){
                    memberOrderItem.setCommented(1);
                    memberOrderItem.setComment_time(DateUtil.getDateline());
                    memberOrderItemManager.update(memberOrderItem);
                }
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IOrderManager getOrderManager() {
		return orderManager;
	}
	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}
	public IMemberCommentManager getMemberCommentManager() {
		return memberCommentManager;
	}
	public void setMemberCommentManager(IMemberCommentManager memberCommentManager) {
		this.memberCommentManager = memberCommentManager;
	}
	public IMemberOrderItemManager getMemberOrderItemManager() {
		return memberOrderItemManager;
	}
	public void setMemberOrderItemManager(
			IMemberOrderItemManager memberOrderItemManager) {
		this.memberOrderItemManager = memberOrderItemManager;
	}
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
}
