package com.enation.app.shop.component.spec.plugin.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.spec.service.ISpecManager;
import com.enation.app.shop.core.model.GoodsLvPrice;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.SpecValue;
import com.enation.app.shop.core.model.Specification;
import com.enation.app.shop.core.plugin.goods.AbstractGoodsPlugin;
import com.enation.app.shop.core.plugin.goods.IGoodsDeleteEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsTabShowEvent;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.SnDuplicateException;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.plugin.IAjaxExecuteEnable;
import com.enation.framework.util.StringUtil;

/**
 * 商品规格插件
 * @author enation
 *
 */
@Component
public class GoodsSpecPlugin extends AbstractGoodsPlugin implements IGoodsDeleteEvent,IGoodsTabShowEvent,IAjaxExecuteEnable{
 
	private IProductManager productManager;
	private IMemberLvManager memberLvManager;
	private IOrderManager orderManager;
	private ISpecManager specManager;
	
	public void addTabs() {
		// this.addTags(4, "规格");
	}
	
	/**
	 * 在添加商品之前处理商品要添加的数据
	 * @param goods
	 * @param request
	 */
	private void processGoods(Map goods, HttpServletRequest request) {
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();
		String haveSpec = httpRequest.getParameter("haveSpec");
		goods.put("have_spec", haveSpec);
		//未开启规格
		if("0".equals(haveSpec)){
			goods.put("cost", httpRequest.getParameter("cost"));
			goods.put("price", httpRequest.getParameter("price"));
			goods.put("weight", httpRequest.getParameter("weight"));
		} else if ("1".equals(haveSpec)) {
			
			if(EopSetting.PRODUCT.equals("b2b2c")){
				Integer storeid = NumberUtils.toInt(goods.get("store_id").toString());
				String gid = (String) goods.get("goods_id");
				Integer goods_id = gid == null ? null : NumberUtils.toInt(gid);
				String[] sns = httpRequest.getParameterValues("sns");
				if(sns!=null){
					for (String sn : sns) {
						int count = this.productManager.getSnIsExist(sn,goods_id,storeid);
						if(count==1){
							throw new SnDuplicateException(sn);
						}
					}
				}
			}
			
			if (!"yes".equals(httpRequest.getParameter("isedit"))) {
				// 默认值
				goods.put("cost", 0);
				goods.put("price", 0);
				goods.put("weight", 0);
			}
			//记录规格相册关系
			String specs_img = httpRequest.getParameter("spec_imgs");
			specs_img = specs_img == null ? "{}" : specs_img;
			goods.put("specs", specs_img);
			
			String[] prices = httpRequest.getParameterValues("prices");
			String[] costs = httpRequest.getParameterValues("costs");
			String[] weights = httpRequest.getParameterValues("weights");
			
			if (prices != null && prices.length > 0) {
				goods.put("price", prices[0]);
			}
			if (costs != null && costs.length > 0) {
				goods.put("cost", costs[0]);
			}
			if (weights != null && weights.length > 0) {
				goods.put("weight", weights[0]);
			}
		}
	}

	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		this.processGoods(goods, request);
	}
	
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request)  {
		this.processSpec(goods, request);
	}

	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request)  {
		this.processGoods(goods, request);
	}
	
	/**
	 * 获取已有的货号数量
	 * @param sns
	 * @return
	 */
	private int getSnsSize(String[] sns) {
		int i = 0;
		for (String sn : sns) {
			if (!StringUtil.isEmpty(sn)) {
				i++;
			}
		}
		return i;
	}
	
	/**
	 * 2011-01-12新增：修复保存商品时，其货品的id会全部重新生成的问题<br>
	 * 处理有规格商品的逻辑
	 * 每一行（即每一个货品product）,有如下hidden：
	 * 		specids(规格id数组，以,号隔开)如：1,2
	 *      specvids(规格值数组，以,号隔开)如21,20
	 *      productids货品id数组，如果为新增，则为空
	 * @param goods
	 * @param request
	 */
	private void processSpec(Map goods, HttpServletRequest request) {
		if (goods.get("goods_id") == null)
			throw new RuntimeException("商品id不能为空");
		Integer goodsId = Integer.valueOf(goods.get("goods_id").toString());
		HttpServletRequest httpRequest = ThreadContextHolder.getHttpRequest();

		String haveSpec = httpRequest.getParameter("haveSpec");
		
		if ("1".equals(haveSpec)) {
			/**
			 * =======================
			 * 带有规格的商品的逻辑
			 * =======================
			 */
			
			String[] specidsAr = httpRequest.getParameterValues("specids"); //规格id数组
			String[] specvidsAr = httpRequest.getParameterValues("specvids");//规格值id数组
			
			String[] productids = httpRequest.getParameterValues("productids"); //货品id数组
			String[] sns = httpRequest.getParameterValues("sns");
			String[] prices = httpRequest.getParameterValues("prices");
			String[] costs = httpRequest.getParameterValues("costs");
		
			String[] weights = httpRequest.getParameterValues("weights");
			
			List<Product> productList = new ArrayList<Product>();
			
			int i = 0;
			int snIndex = this.getSnsSize(sns);
			for (String sn : sns) {
				Integer productId = StringUtil.isEmpty(productids[i]) ? null : Integer.valueOf(productids[i]);
				if (sn == null || sn.equals("")) {
					sn = goods.get("sn") + "-" + (snIndex + 1);
					snIndex++;
				}
			
				/*
				 * 组合商品、货品、规格值、规格对应关系
				 */
				List<SpecValue> valueList = new ArrayList<SpecValue>();
				int j = 0;
				String[] specids = specidsAr[i].split(","); // 此货品的规格
				String[] specvids = specvidsAr[i].split(","); // 此货品的规格值
				
				//此货品的规格值list
				for (String specid : specids) {
					SpecValue specvalue = new SpecValue();
					specvalue.setSpec_value_id(Integer.valueOf(specvids[j].trim()));
					specvalue.setSpec_id(Integer.valueOf(specid.trim()));
					valueList.add(specvalue);
					j++;
				}
				
				// 生成货品对象
				Product product = new Product();
				product.setGoods_id(goodsId);
				product.setSpecList(valueList);// 设置此货品的规格list
				product.setName((String) goods.get("name"));
				product.setSn(sn);
				product.setProduct_id(productId); // 2010-1-12新增写入货品id，如果是新货品，则会是null
				//product.setStore(0);               // 修改添加是product表里库存为空，添加时默认为0，   如果有问题，请高手优先处理此处  whj  2015-05-21
				
				
				
				String[] specvalues = httpRequest.getParameterValues("specvalue_" + i);
				product.setSpecs(StringUtil.arrayToString(specvalues, "、"));
				// 价格
				if (null == prices[i] || "".equals(prices[i]))
					product.setPrice(0D);
				else
					product.setPrice(Double.valueOf(prices[i]));

//				if (!"yes".equals(httpRequest.getParameter("isedit"))) { // 添加时默认为0，修改时不处理
//					product.setStore(0);
//				}
				
				// 成本价
				if (null == costs[i] || "".equals(costs[i]))
					product.setCost(0D);
				else
					product.setCost(Double.valueOf(costs[i]));
				
				// 重量
				if (null == weights[i] || "".equals(weights[i]))
					product.setWeight(0D);
				else
					product.setWeight(Double.valueOf(weights[i]));
				
				// 20101123新增会员价格相应逻辑
				// 规格为：name为加_index
				String[] lvPriceStr = httpRequest.getParameterValues("lvPrice_"
						+ i);
				String[] lvidStr = httpRequest.getParameterValues("lvid_" + i);
				
				// 生成会员价list
				if (lvidStr != null && lvidStr.length > 0) { // lzf add line 20110114
					List<GoodsLvPrice> goodsLvPrices = this.createGoodsLvPrices(lvPriceStr, lvidStr, goodsId);
					product.setGoodsLvPrices(goodsLvPrices);
				} // lzf add line 20110114
				
				productList.add(product);
				i++;
			} 
			this.productManager.add(productList);
		} else {
			Product product = this.productManager.getByGoodsId(goodsId);
			if (product == null) {
				product = new Product();
			}
			
			product.setGoods_id(goodsId);
			product.setCost(Double.valueOf("" + goods.get("cost")));
			product.setPrice(Double.valueOf("" + goods.get("price")));
			product.setSn((String) goods.get("sn"));
			product.setWeight(Double.valueOf("" + goods.get("weight")));
			product.setName((String) goods.get("name"));
			//product.setStore(0);                                              //如果添加是不默认是0，那么添加时候eclicp报错，如果有问题，请高手们优先chu'li
			
			// 20101123新增会员价格相应逻辑
			String[] lvPriceStr = httpRequest.getParameterValues("lvPrice");
			String[] lvidStr = httpRequest.getParameterValues("lvid");
			
			// 生成会员价list
			if (lvidStr != null && lvidStr.length > 0) { // lzf add line 20110114
				List<GoodsLvPrice> goodsLvPrices = this.createGoodsLvPrices(lvPriceStr, lvidStr, goodsId);
				product.setGoodsLvPrices(goodsLvPrices);
			} // lzf add line 20110114
			
			List<Product> productList = new ArrayList<Product>();
			productList.add(product);
			this.productManager.add(productList);
		}
	}
 
	/**
	 * 根据会员级别id和会员价信息生成会员价list<br>
	 * 会员价格如果无空则不插入数据，即不生成会员价，而是按此会员级别的默认折扣计算会员价格,以减少冗余数据。
	 * 
	 * @param lvPriceStr 会员价数组，数组的值类型为字串，考虑到一般由request中获取。
	 * @param lvidStr 会员级别id数组，数组的值类型为字串，考虑到一般由request中获取。
	 * @param goodsid 当前商品id，没有填充productid,此id在productmanager中添加数据时动态获取到并填充
	 * @return 生成的List<GoodsLvPrice>
	 */
	private List<GoodsLvPrice> createGoodsLvPrices(String[] lvPriceStr,	String[] lvidStr, int goodsid) {
		List<GoodsLvPrice> goodsLvPrices = new ArrayList<GoodsLvPrice>();
		for (int i = 0; i < lvidStr.length; i++) {
			int lvid = StringUtil.toInt(lvidStr[i]);
			Double lvPrice = StringUtil.toDouble(lvPriceStr[i]);
			
			if (lvPrice.doubleValue() != 0) {// 输入了会员价格
				GoodsLvPrice goodsLvPrice = new GoodsLvPrice();
				goodsLvPrice.setGoodsid(goodsid);
				goodsLvPrice.setPrice(lvPrice);
				goodsLvPrice.setLvid(lvid);
				goodsLvPrices.add(goodsLvPrice);
			}
		}
		return goodsLvPrices;
	}
	
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request) {
		this.processSpec(goods, request);
	}	
	
	/**
	 * 响应修改时填充数据事件
	 * <br/>形成规格list
	 */
	public String getEditHtml(Map goods, HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();
		Integer goods_id = Integer.valueOf(goods.get("goods_id").toString());
		List<String> specNameList = productManager.listSpecName(goods_id);
		List<Product> productList = productManager.list(goods_id);

		List<Specification> specList = this.specManager.listSpecAndValue();
		freeMarkerPaser.putData("specList", specList);

		List lvList = this.memberLvManager.list();
		freeMarkerPaser.putData("lvList", lvList);

		freeMarkerPaser.putData("productList", productList);
		freeMarkerPaser.putData("specNameList", specNameList);
		freeMarkerPaser.setPageName("spec2");
		// freeMarkerPaser.setPageFolder("/");
		return freeMarkerPaser.proessPageContent();
	}	
	
	/**
	 * 响应商品添加页填充数据事件
	 * <br/>
	 */
	public String getAddHtml(HttpServletRequest request) {
		FreeMarkerPaser freeMarkerPaser = FreeMarkerPaser.getInstance();

		List<Specification> specList = this.specManager.listSpecAndValue();
		freeMarkerPaser.putData("specList", specList);
		// freeMarkerPaser.setPageFolder("/plugin/spec");
		freeMarkerPaser.setPageName("spec2");
		return freeMarkerPaser.proessPageContent();
	}
	
	public void onGoodsDelete(Integer[] goodsid) {
		 this.productManager.delete(goodsid);
	}

	public String getAuthor() {
		return "kingapex";
	}

	public String getId() {
		return "goodsspec";
	}

	public String getName() {
		return "通用商品规格插件";
	}

	public String getType() {
		return "";
	}

	public String getVersion() {
		return "1.0";
	}

	public void perform(Object... params) {
		
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	public IMemberLvManager getMemberLvManager() {
		return memberLvManager;
	}

	public void setMemberLvManager(IMemberLvManager memberLvManager) {
		this.memberLvManager = memberLvManager;
	}

	@Override
	public String getTabName() {
		return "规格";
	}

	@Override
	public int getOrder() {
		return 4;
	}

	@Override
	public String execute() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String action = request.getParameter("action");
		if ("check-pro-in-order".equals(action)) {
			int productid = StringUtil.toInt(request.getParameter("productid"),	true);
			boolean isinorder = this.orderManager.checkProInOrder(productid);
			if (isinorder) {
				return "{result:1}";
			} else {
				return "{result:0}";
			}
		} else if ("check-goods-in-order".equals(action)) {
			int goodsid = StringUtil.toInt(request.getParameter("goodsid"),	true);
			boolean isinorder = this.orderManager.checkGoodsInOrder(goodsid);
			if (isinorder) {
				return "{result:1}";
			} else {
				return "{result:0}";
			}
		}
		return "";
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public ISpecManager getSpecManager() {
		return specManager;
	}

	public void setSpecManager(ISpecManager specManager) {
		this.specManager = specManager;
	}

}
