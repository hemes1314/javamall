package com.enation.app.shop.mobile.action.bonus;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.IMemberGiftcardManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

@ParentPackage("shop_default")
@Namespace("/api/mobile")
@Scope("prototype")
@Action("bonus")
public class BonusApiAction extends WWAction{

	private IBonusManager bonusManager;
	private ICartManager cartManager;
	private int bonusid;
	private String sn;

	
	/**
	 * 获取会员可用红包列表
	 * @param 无
	 * @return 红包列表，List<Map>型
	 * map内容
	 * type_name:红包名称
	 * type_money:红包金额
	 * send_type：红包类型 (0会员发放，1:按商品发放,2:按订单发放,3:线下发放的红包)
	 */
	 
	public String getMemberBonus(){
		try {
			Member member = UserConext.getCurrentMember();
			if(member ==null){
				this.showErrorJson("未登录，不能使用此api");
				return this.JSON_MESSAGE;
			}
			
			Double goodsprice = cartManager.countGoodsTotal(this.getRequest().getSession().getId());
			List bonusList  = bonusManager.getMemberBonusList(member.getMember_id(), goodsprice,1);
			this.json = JsonMessageUtil.getMobileListJson(bonusList);
		} catch (Exception e) {
			this.logger.error("调用获取会员红包api出错",e);
			this.showErrorJson(e.getMessage());
		}
	
		
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 使用一个红包
	 * @param bonusid:红包id
	 * @return json字串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 为提示信息
	 */
	public String useOne(){
		try {
			
			//清除使用的红包
			if(bonusid==0){
				BonusSession.clean();
				this.showSuccessJson("清除红包成功");
				return this.JSON_MESSAGE;
			}
			
			MemberBonus bonus  =bonusManager.getBonus(bonusid);
			
		
			
			Double goodsprice = cartManager.countGoodsTotal(this.getRequest().getSession().getId());
			if(goodsprice<= bonus.getMin_goods_amount()){
				
				this.showErrorJson("订单的商品金额不足["+bonus.getMin_goods_amount()+"],不能使用此红包");
				return this.JSON_MESSAGE;
				
			}
			
			BonusSession.useOne(bonus);
			this.showSuccessJson("红包使用成功");
		} catch (Exception e) {
			this.showErrorJson("使用红包发生错误["+e.getMessage()+"]");
			this.logger.error("使用红包发生错误",e);
		}
		return this.JSON_MESSAGE;
	}
	
	
	public String useSn(){
		try {
			
			 
			if(StringUtil.isEmpty(sn)){
				this.showErrorJson("红包编号不能为空");
				return this.JSON_MESSAGE;
			}
			
			MemberBonus bonus  =bonusManager.getBonus(sn);
			if(bonus==null){
				this.showErrorJson("您输入的红包编号不正确");
				return this.JSON_MESSAGE;
			}
			
			
			if(bonus.getUsed_time()!=null){
				
				this.showErrorJson("此红包已被使用过");
				return this.JSON_MESSAGE;
			}
	
	
			Double goodsprice = cartManager.countGoodsTotal(this.getRequest().getSession().getId());
			if(goodsprice<= bonus.getMin_goods_amount()){
				
				this.showErrorJson("订单的商品金额不足["+bonus.getMin_goods_amount()+"],不能使用此红包");
				return this.JSON_MESSAGE;
				
			}
			
			long now = DateUtil.getDateline();
			if(bonus.getUse_start_date() > now){
				long l=Long.valueOf(bonus.getUse_start_date())*1000;
				this.showErrorJson("此红包还未到使用期，开始使用时间为["+DateUtil.toString(new Date(l), "yyyy年MM月dd日")+"]");
				return this.JSON_MESSAGE;
			}
			
			if(bonus.getUse_end_date() < now){
				long l=Long.valueOf(bonus.getUse_end_date())*1000;
				this.showErrorJson("此红包已过期，使用截至时间为["+DateUtil.toString(new Date(l), "yyyy年MM月dd日")+"]");
				return this.JSON_MESSAGE;
			}
			
			BonusSession.use(bonus);
			this.showSuccessJson("红包使用成功");
		} catch (Exception e) {
			this.showErrorJson("使用红包发生错误["+e.getMessage()+"]");
			this.logger.error("使用红包发生错误",e);
		}
		return this.JSON_MESSAGE;
	}
	
	/**
	 * 
	 * 用sn取消一个红包的使用
	 * @return
	 */
	public String cancelSn(){
		
		try {
			if(StringUtil.isEmpty(sn)){
				this.showErrorJson("编号不能为空");
				return this.JSON_MESSAGE;
			}
			BonusSession.cancel(sn);
			this.showSuccessJson("取消成功");
		} catch (Exception e) {
			this.showErrorJson("取消红包发生错误["+e.getMessage()+"]");
			this.logger.error("取消红包发生错误",e);
		}
		return this.JSON_MESSAGE;
	}
	
	
	/**
	 * 获取使用的红包的金额
	 * @param 获取正在使用的红包金额
	 * @return json字串
	 * result  为1表示调用成功0表示失败 ，int型
	 * message 如果错误，为提示信息
	 * money: 如果调用成功，为使用红包的总金额
	 */
	public String getUseBonusMoney(){
		
		try {
			double moneyCount = BonusSession.getUseMoney();
			this.json = JsonMessageUtil.getNumberJson("money", moneyCount);
		
		} catch (Exception e) {
			this.logger.error("获取红包金额出错", e);
			this.showErrorJson("获取红包金额出错["+e.getMessage()+"]");
		}
		
		
		return this.JSON_MESSAGE;

	}


    public IBonusManager getBonusManager() {
		return bonusManager;
	}

	public void setBonusManager(IBonusManager bonusManager) {
		this.bonusManager = bonusManager;
	}

	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}

	public int getBonusid() {
		return bonusid;
	}

	public void setBonusid(int bonusid) {
		this.bonusid = bonusid;
	}

	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn;
	}
}
