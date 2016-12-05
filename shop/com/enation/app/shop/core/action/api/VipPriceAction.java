package com.enation.app.shop.core.action.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.MemberLv;
import com.enation.app.shop.core.model.GoodsLvPrice;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPriceManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.CurrencyUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.enation.framework.util.StringUtil;

/**
 * 会员价格api
 * @author lina
 * 2014-2-17
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("vipprice")
public class VipPriceAction extends WWAction {
	private IMemberLvManager memberLvManager;
	private IMemberPriceManager memberPriceManager;
	private IProductManager productManager;
	private Integer productid;

	/**
	 * 有规格商品的会员vip价格
	 * @param productid 
	 * @return json格式的商品会员价格及重量
	 * vipprice:会员价格
	 * weight:重量
	 */
	public String showVipPrice() {
		Product product = this.productManager.get(productid);
		double price = product.getPrice(); // 此货品的价格
		double vipprice = price;
		List<MemberLv> memberLvList = memberLvManager.list();
		// 读取此货品的会员价格
		List<GoodsLvPrice> glpList = this.memberPriceManager.listPriceByPid(productid);
		// 设置了会员价格，读取出低的价格
		if (glpList != null && glpList.size() > 0) {
			// 设置了会员价格
			double discount = 1;
			for (MemberLv lv : memberLvList) {
				double lvprice1 = 0; // 会员价格
				if (lv.getDiscount() != null) {
					discount = lv.getDiscount() / 100.00;
					lvprice1 = CurrencyUtil.mul(price, discount);
				}
				double lvPrice = this.getMemberPrice(lv.getLv_id(), glpList); // 定义的会员价格
				if (lvPrice == 0) {
					lv.setLvPrice(lvprice1);
					lvPrice = lvprice1;
				} else {
					lv.setLvPrice(lvPrice);
				}
				if (vipprice > lvPrice) {
					vipprice = lvPrice;
				}
			}
		} else {
			double discount = 1;
			for (MemberLv lv : memberLvList) {
				if (lv.getDiscount() != null) {
					discount = lv.getDiscount() / 100.00;
					double lvprice = CurrencyUtil.mul(price, discount);
					lv.setLvPrice(lvprice);
					if (vipprice > lvprice) {
						vipprice = lvprice;
					}
				}
			}
		}

		Map vip = new HashMap(2);
		vip.put("vipprice", vipprice);
		vip.put("weight", product.getWeight());
		this.json = JsonMessageUtil.getObjectJson(vip);
		return this.JSON_MESSAGE;
	}

	/**
	 * 根据级别获取 该级别某商品的价格
	 * 
	 * @param lv_id
	 * @param memPriceList
	 * @return
	 */
	private double getMemberPrice(int lv_id, List<GoodsLvPrice> memPriceList) {
		for (GoodsLvPrice lvPrice : memPriceList) {
			if (lv_id == lvPrice.getLvid()) {
				return lvPrice.getPrice();
			}
		}
		return 0;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	public IMemberPriceManager getMemberPriceManager() {
		return memberPriceManager;
	}

	public void setMemberPriceManager(IMemberPriceManager memberPriceManager) {
		this.memberPriceManager = memberPriceManager;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public Integer getProductid() {
		return productid;
	}

	public void setProductid(Integer productid) {
		this.productid = productid;
	}

}
