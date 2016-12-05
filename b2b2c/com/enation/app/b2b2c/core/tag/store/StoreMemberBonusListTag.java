package com.enation.app.b2b2c.core.tag.store;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.IStoreBonusManager;
import com.enation.app.b2b2c.core.service.cart.IStoreCartManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.bonus.model.MemberBonus;
import com.enation.app.shop.component.bonus.service.BonusSession;
import com.enation.app.shop.component.bonus.service.IBonusManager;
import com.enation.app.shop.core.service.ICartManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.taglib.BaseFreeMarkerTag;

import freemarker.template.TemplateModelException;

/**
 * 结算页查询商城发放优惠券列表
 * @author xulipeng
 *
 */

@Component
public class StoreMemberBonusListTag extends BaseFreeMarkerTag {

	private IStoreCartManager storeCartManager;
	private IStoreBonusManager storeBonusManager;
	private IStoreMemberManager storeMemberManager;
	private IBonusManager bonusManager;
	private	ICartManager cartManager;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected Object exec(Map params) throws TemplateModelException {
		try {
			
			Integer type= (Integer) params.get("type");
			StoreMember member = this.storeMemberManager.getStoreMember();
			if(member ==null){
				return ("未登陆，不能使用此api");
			}
			
			Double goodsprice = cartManager.countGoodsTotal(this.getRequest().getSession().getId());
			if(type==1){
				goodsprice=99999999.0;
			}
			
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			Map result = new HashMap();
			String page = request.getParameter("page");
			page = (page == null || page.equals("")) ? "1" : page;
			int pageSize = 10;
			
			Page webpage  = bonusManager.getMemberBonusList(member.getMember_id(), goodsprice,type,Integer.valueOf(page),pageSize);
			
			Long totalCount = webpage.getTotalCount();
			List<Map> bonusList = (List) webpage.getResult();
			bonusList = bonusList == null ? new ArrayList() : bonusList;
			
			MemberBonus useBonus = BonusSession.getOne();
			if(useBonus!=null){
				for (Map bonus : bonusList) {
					Integer bonusid= (Integer)bonus.get("bonus_id");
					if(bonusid== useBonus.getBonus_id()){//已经使用
						bonus.put("used",1);
					}else{
						bonus.put("used",0);
					}
				}
			}

			result.put("totalCount", totalCount);
			result.put("pageSize", pageSize);
			result.put("page", page);
			result.put("bonusList", bonusList);
			return  result;
			
		} catch (Exception e) {
			throw new 	TemplateModelException(e);	 
		}
	}

	public IStoreCartManager getStoreCartManager() {
		return storeCartManager;
	}

	public void setStoreCartManager(IStoreCartManager storeCartManager) {
		this.storeCartManager = storeCartManager;
	}

	public IStoreBonusManager getStoreBonusManager() {
		return storeBonusManager;
	}

	public void setStoreBonusManager(IStoreBonusManager storeBonusManager) {
		this.storeBonusManager = storeBonusManager;
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

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
	

}
