package com.enation.app.shop.core.action.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.advbuy.core.model.AdvBuy;
import com.enation.app.advbuy.core.model.AdvBuyActive;
import com.enation.app.advbuy.core.service.IAdvBuyActiveManager;
import com.enation.app.b2b2cAdvbuy.core.model.StoreAdvBuy;
import com.enation.app.b2b2cAdvbuy.core.service.impl.IStoreAdvBuyManager;
import com.enation.app.b2b2cFlashbuy.core.model.StoreFlashBuy;
import com.enation.app.b2b2cFlashbuy.core.service.impl.IStoreFlashBuyManager;
import com.enation.app.b2b2cGroupbuy.core.model.StoreGroupBuy;
import com.enation.app.b2b2cGroupbuy.core.service.impl.IStoreGroupBuyManager;
import com.enation.app.b2b2cSecbuy.core.model.StoreSecBuy;
import com.enation.app.b2b2cSecbuy.core.service.impl.IStoreSecBuyManager;
import com.enation.app.b2b2ccostdown.core.model.CostDown;
import com.enation.app.b2b2ccostdown.core.model.CostDownActive;
import com.enation.app.b2b2ccostdown.core.model.StoreCostDown;
import com.enation.app.b2b2ccostdown.core.service.CostDownActiveManager;
import com.enation.app.b2b2ccostdown.core.service.StoreCostDownManager;
import com.enation.app.flashbuy.core.model.FlashBuyActive;
import com.enation.app.flashbuy.core.service.IFlashBuyActiveManager;
import com.enation.app.groupbuy.core.model.GroupBuy;
import com.enation.app.groupbuy.core.model.GroupBuyActive;
import com.enation.app.groupbuy.core.service.IGroupBuyActiveManager;
import com.enation.app.secbuy.core.model.SecBuyActive;
import com.enation.app.secbuy.core.service.ISecBuyActiveManager;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.impl.ActivityGoodsManager;
import com.enation.app.shop.mobile.service.IApiCommentManager;
import com.enation.app.utils.PriceUtils;
import com.enation.app.utils.PriceUtils.IActivity;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;
import com.sun.org.apache.bcel.internal.generic.IMUL;

/**
 * 商品api
 * @author kingapex
 *2013-8-20下午8:17:14
 */
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("goods")
@SuppressWarnings({ "rawtypes", "unchecked", "serial" })
public class GoodsApiAction extends WWAction {
	private IProductManager productManager;

	private IGoodsManager goodsManager;
	private IApiCommentManager apiCommentManager;
	private ActivityGoodsManager activityGoodsManager;
	@Autowired
	private CostDownActiveManager costDownActiveManager;
	@Autowired
	private StoreCostDownManager storeCostDownManager;
	private IGroupBuyActiveManager groupBuyActiveManager;
	private IStoreGroupBuyManager storeGroupBuyManager;
	private IFlashBuyActiveManager flashBuyActiveManager;
	private IStoreFlashBuyManager storeFlashBuyManager;
	private ISecBuyActiveManager secBuyActiveManager;
	private IStoreSecBuyManager storeSecBuyManager;
	private IAdvBuyActiveManager advBuyActiveManager;
	private IStoreAdvBuyManager storeAdvBuyManager;

	
	protected final Logger logger = Logger.getLogger(GoodsApiAction.class);
	
	private int goodsid;
	public int getGoodsid() {
		return goodsid;
	}

	public void setGoodsid(int goodsid) {
		this.goodsid = goodsid;
	}

	private Integer catid;
	private String keyword;
	private Integer brandid;
	private List<Goods> goodslist;
	private Map goodsMap;

	/**
	 * 搜索商品
	 * 输入参数：
	 * @param catid ：分类id,如果不输入，则搜索全部的分类下的商品
	 * @param brandid:品牌id，如果不佃入，是搜索全部的品牌下的商品
	 * @param keyword：搜索关键字，会搜索商品名称和商品编号
	 * @return 商品搜索结果
	 * {@link Goods}
	 */
	public String search(){
		goodsMap=new HashMap();

		goodsMap.put("catid", catid);
		goodsMap.put("brandid", brandid);
		goodsMap.put("keyword", keyword);
		goodsMap.put("stype", 0);

		goodslist =goodsManager.searchGoods(goodsMap);
		this.json = JsonMessageUtil.getListJson(goodslist);
		return JSON_MESSAGE;
	}

	public String productList(){
		/**
		 *  {'product_id':${product.product_id},'goods_id':${product.goods_id},'sn':'${product.sn}','store':${product.store!0},
		'price':${product.price},
	   'specs':${product.specsvIdJson}
	  }
		 */
		try {
			List<Product> productList  = this.productManager.list(goodsid);

			StringBuffer str  = new StringBuffer();
			for (Product product : productList) {
				//edit by Ken 修改成从缓存获取商品信息, copy from com.enation.app.shop.mobile.action.goods.GoodsApiAction.detail()
				Map goods = goodsManager.getByCache(product.getGoods_id());

				Integer buyCount = (Integer)goods.get("buy_count");
				int commentCount = apiCommentManager.getCommentsCount(product.getGoods_id());

				if(str.length()!=0){str.append(",");}
				str.append("{\"product_id\":"+product.getProduct_id()+",");
				str.append("\"goods_id\":"+product.getGoods_id()+",");
				str.append("\"sn\":\""+product.getSn()+"\",");
				str.append("\"store\":"+product.getStore()+",");
				str.append("\"enable_store\":"+product.getEnable_store()+",");
				str.append("\"price\":"+product.getPrice()+",");
				str.append("\"buy_count\":"+buyCount+",");
				str.append("\"comment_count\":"+commentCount+",");
				
				str.append("\"specs\":"+product.getSpecsvIdJson()+"}");

			}
			this.json=  "{\"result\":1,\"data\":["+str+"]}";


		} catch (Exception e) {
			this.logger.error("获取产品列表出错", e);
			this.showErrorJson("获取产品列表出错");
		}



		return this.JSON_MESSAGE;
	}

	/**
	 * 获取活动价格
	 * @param goodsid 商品id
	 * @return json
	 */
	public String getActivityPrice(){
		try{
			System.out.println("goodsManager:"+goodsManager);
	        Map goodsMap = goodsManager.get(goodsid);

			System.out.println("goodsMap:" + goodsMap);
			String typeName = "价格：";
			double price = NumberUtils.toDouble(goodsMap.get("price").toString());

			StoreGroupBuy gb = null;
			StoreCostDown cd = null;
			StoreAdvBuy ab = null;
			if (NumberUtils.toInt(goodsMap.get("is_cost_down").toString()) == 1) {
				CostDownActive cda = costDownActiveManager.get();
				if (cda != null) cd = storeCostDownManager.getBuyGoodsId(goodsid, cda.getAct_id());
			}
			if (NumberUtils.toInt(goodsMap.get("is_groupbuy").toString()) == 1) {
				GroupBuyActive groupbuyAct = groupBuyActiveManager.get();
				if (groupbuyAct != null) gb = storeGroupBuyManager.getBuyGoodsId(goodsid, groupbuyAct.getAct_id());
			}
			if (NumberUtils.toInt(goodsMap.get("is_advbuy").toString()) == 1) {
				AdvBuyActive advbuyAct = advBuyActiveManager.get();
				if (advbuyAct != null) ab = storeAdvBuyManager.getBuyGoodsId(goodsid, advbuyAct.getAct_id());
			}
			IActivity a = PriceUtils.getMinimumPriceActivity(gb, cd, ab);

			JSONObject jsonObject = new JSONObject();
			System.out.println("price:" + price);
			if (null != a) {
				price = a.getPrice();
				if (a.equals(gb))
					typeName = "闪购价格：";
				if (a.equals(ab))
					typeName = "预售价格：";
				if (a.equals(cd)) {
					typeName = "直降价格：";
					jsonObject.put("result", 1);
					jsonObject.put("type", "zj");

					JSONObject object = new JSONObject();

					object.put("typeName", typeName);
					object.put("price", price);
					object.put("gbName", cd.getGb_name());

					String imgUrl = cd.getImg_url();
					if (imgUrl != null) {
						imgUrl = UploadUtil.replacePath(imgUrl);
					}
	/*                String prefix = imgUrl.substring(0,imgUrl.lastIndexOf("."));
					String suffix = imgUrl.substring(imgUrl.lastIndexOf("."));
	                String thumbnailImg = prefix+"_thumbnail"+suffix;
	                String bigImg = prefix+"_big"+suffix;
	                String smallImg = prefix+"_small"+suffix;*/

					object.put("thumbnailImg", imgUrl);
					object.put("bigImg", imgUrl);
					object.put("smallImg", imgUrl);
					jsonObject.put("data", object);

					this.json = jsonObject.toJSONString();
					return this.JSON_MESSAGE;
				}
			}

			this.json=  "{\"result\":1,\"data\":{" + " \"typeName\":\"" + typeName +"\",\"price\":" + price + "}}";
		}catch(Exception e){
			System.out.println("e:"+e.getMessage());
			e.printStackTrace();
			logger.error("getActivityPrice:"+e.getStackTrace());
		}

	    return this.JSON_MESSAGE;
	}



	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}

	public Integer getCatid() {
		return catid;
	}

	public void setCatid(Integer catid) {
		this.catid = catid;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public Integer getBrandid() {
		return brandid;
	}

	public void setBrandid(Integer brandid) {
		this.brandid = brandid;
	}

	public List<Goods> getGoodslist() {
		return goodslist;
	}

	public void setGoodslist(List<Goods> goodslist) {
		this.goodslist = goodslist;
	}

	public Map getGoodsMap() {
		return goodsMap;
	}

	public void setGoodsMap(Map goodsMap) {
		this.goodsMap = goodsMap;
	}

	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}


    public ActivityGoodsManager getActivityGoodsManager() {
        return activityGoodsManager;
    }


    public void setActivityGoodsManager(ActivityGoodsManager activityGoodsManager) {
        this.activityGoodsManager = activityGoodsManager;
    }


    public IGroupBuyActiveManager getGroupBuyActiveManager() {
        return groupBuyActiveManager;
    }


    public void setGroupBuyActiveManager(IGroupBuyActiveManager groupBuyActiveManager) {
        this.groupBuyActiveManager = groupBuyActiveManager;
    }


    public IStoreGroupBuyManager getStoreGroupBuyManager() {
        return storeGroupBuyManager;
    }


    public void setStoreGroupBuyManager(IStoreGroupBuyManager storeGroupBuyManager) {
        this.storeGroupBuyManager = storeGroupBuyManager;
    }


    public IFlashBuyActiveManager getFlashBuyActiveManager() {
        return flashBuyActiveManager;
    }


    public void setFlashBuyActiveManager(IFlashBuyActiveManager flashBuyActiveManager) {
        this.flashBuyActiveManager = flashBuyActiveManager;
    }


    public IStoreFlashBuyManager getStoreFlashBuyManager() {
        return storeFlashBuyManager;
    }


    public void setStoreFlashBuyManager(IStoreFlashBuyManager storeFlashBuyManager) {
        this.storeFlashBuyManager = storeFlashBuyManager;
    }


    public ISecBuyActiveManager getSecBuyActiveManager() {
        return secBuyActiveManager;
    }


    public void setSecBuyActiveManager(ISecBuyActiveManager secBuyActiveManager) {
        this.secBuyActiveManager = secBuyActiveManager;
    }


    public IStoreSecBuyManager getStoreSecBuyManager() {
        return storeSecBuyManager;
    }


    public void setStoreSecBuyManager(IStoreSecBuyManager storeSecBuyManager) {
        this.storeSecBuyManager = storeSecBuyManager;
    }


    public IAdvBuyActiveManager getAdvBuyActiveManager() {
        return advBuyActiveManager;
    }


    public void setAdvBuyActiveManager(IAdvBuyActiveManager advBuyActiveManager) {
        this.advBuyActiveManager = advBuyActiveManager;
    }


    public IStoreAdvBuyManager getStoreAdvBuyManager() {
        return storeAdvBuyManager;
    }


    public void setStoreAdvBuyManager(IStoreAdvBuyManager storeAdvBuyManager) {
        this.storeAdvBuyManager = storeAdvBuyManager;
    }


	public IApiCommentManager getApiCommentManager() {
		return apiCommentManager;
	}

	public void setApiCommentManager(IApiCommentManager apiCommentManager) {
		this.apiCommentManager = apiCommentManager;
	}

}
