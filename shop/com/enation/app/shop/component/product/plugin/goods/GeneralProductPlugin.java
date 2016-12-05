package com.enation.app.shop.component.product.plugin.goods;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.GoodsLvPrice;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterAddEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterEditEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsDeleteEvent;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;

/**
 * 普通货品插件
 * @author kingapex
 * @date 2011-11-6 下午4:34:51 
 * @version V1.0
 */
@Component
public class GeneralProductPlugin extends AutoRegisterPlugin implements	IGoodsAfterAddEvent, IGoodsAfterEditEvent, IGoodsDeleteEvent {
	private IProductManager productManager;

	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		 
		if (goods.get("goods_id") == null)
			throw new RuntimeException("商品id不能为空");
		Integer goodsId = Integer.valueOf(goods.get("goods_id").toString());
		Integer brandid = null;
		if (goods.get("brand_id") != null) {
			brandid = Integer.valueOf(goods.get("brand_id").toString());
		}
		String sn = (String) goods.get("sn");
			
		Product product = new Product();
		product.setGoods_id(goodsId);
		product.setCost(Double.valueOf("" + goods.get("cost")));
		product.setPrice(Double.valueOf("" + goods.get("price")));
		product.setSn(sn);
		product.setStore(Integer.valueOf("" + goods.get("store")));
		product.setWeight(Double.valueOf("" + goods.get("weight")));
		product.setName((String) goods.get("name"));
		
		List<Product> productList = new ArrayList<Product>();
			
		// 20120305新增会员价格相应逻辑
		String[] lvPriceStr = request.getParameterValues("lvPrice");
		String[] lvidStr = request.getParameterValues("lvid");
			
		// 生成会员价list
		if (lvidStr != null && lvidStr.length > 0) { // lzf add line 20110114
			List<GoodsLvPrice> goodsLvPrices = this.createGoodsLvPrices(lvPriceStr, lvidStr, goodsId);
			product.setGoodsLvPrices(goodsLvPrices);
		}
			
		productList.add(product);
		this.productManager.add(productList);
	}

	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		if (goods.get("goods_id") == null)
			throw new RuntimeException("商品id不能为空");
		Integer goodsId = Integer.valueOf(goods.get("goods_id").toString());
		Product product = this.productManager.getByGoodsId(goodsId);
		product.setGoods_id(goodsId);
		product.setCost(Double.valueOf("" + goods.get("cost")));
		product.setPrice(Double.valueOf("" + goods.get("price")));
		product.setSn((String) goods.get("sn"));
		//product.setStore(Integer.valueOf(""+goods.get("store")));
		//product.setWeight(Double.valueOf( ""+goods.get("weight")));
		product.setName((String)goods.get("name"));
		 
		List<Product> productList = new ArrayList<Product>();

		//20120305新增会员价格相应逻辑
		String[] lvPriceStr = request.getParameterValues("lvPrice");
		String[] lvidStr = request.getParameterValues("lvid");
		
		// 生成会员价list
		if (lvidStr != null && lvidStr.length > 0) { // lzf add line 20110114
			List<GoodsLvPrice> goodsLvPrices = this.createGoodsLvPrices(lvPriceStr, lvidStr, goodsId);
			product.setGoodsLvPrices(goodsLvPrices);
		}		
		
		productList.add(product);
		this.productManager.add(productList);
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

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	@Override
	public void onGoodsDelete(Integer[] goodsid) {
		 this.productManager.delete(goodsid);
	}

}
