package com.enation.app.shop;

import org.apache.log4j.Logger;

import com.enation.app.shop.core.service.ICartManager;
import com.enation.app.shop.core.service.impl.cache.ArticleCatCacheProxy;
import com.enation.app.shop.core.service.impl.cache.GoodsCatCacheProxy;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.App;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.ISqlFileExecutor;

/**
 * 网店应用
 * 
 * @author kingapex 2010-1-23下午06:21:07
 */
public class ShopApp extends App {
	protected final Logger logger = Logger.getLogger(getClass());
	private IDBRouter baseDBRouter;
	private ISqlFileExecutor sqlFileExecutor;
	private ICartManager cartManager;
	
    public ShopApp() {
		// 商品表
		super();
		tables.add("goods");
		tables.add("product");// lzf add

		// 商品类别表
		tables.add("goods_cat");

		// 创建品牌表
		tables.add("brand");

		// 创建类型相关表
		tables.add("goods_type");
		tables.add("type_brand"); // 类型品牌关联表

		// 赠品,lzf add
		tables.add("freeoffer");
		tables.add("freeoffer_category");

		// 商品标签
		tables.add("tags");
		tables.add("tag_rel");

		// 会员
		tables.add("member");
		tables.add("member_lv");
		tables.add("goods_lv_price");

		// 代理商
		tables.add("agent");
		tables.add("agent_transfer");

		// 配送方式标签
		tables.add("dly_type");
		tables.add("dly_area");
		tables.add("dly_type_area");

		// 物流公司
		tables.add("logi_company");

		// 商品评论
		tables.add("comments");

		// 订单相关
		tables.add("cart");
		tables.add("order");
		tables.add("order_items");
		tables.add("order_log");

		tables.add("delivery");
		tables.add("delivery_item");
		tables.add("payment_cfg");
		tables.add("payment_detail");
		tables.add("payment_logs");
		tables.add("refund_logs");
		tables.add("member_address");
		tables.add("message");
		tables.add("order_gift");
		// 营销推广相关
		tables.add("gnotify");
		tables.add("point_history");
		tables.add("coupons");
		tables.add("promotion");
		tables.add("member_coupon");
		tables.add("pmt_member_lv");
		tables.add("pmt_goods");
		tables.add("favorite");
		tables.add("advance_logs");
		tables.add("promotion_activity");
		tables.add("goods_complex");
		// tables.add("goods_adjunct"); 已转移至组件
		tables.add("goods_articles");
		tables.add("goods_field");
		tables.add("group_buy_count");
		tables.add("limitbuy");
		tables.add("limitbuy_goods");
		tables.add("article");
		tables.add("article_cat");
		tables.add("package_product");
		tables.add("dly_center");
		tables.add("print_tmpl");
		tables.add("order_pmt");
		tables.add("group_buy");

		tables.add("member_comment");
		tables.add("warn_num");
		tables.add("freeze_point");
		tables.add("member_lv_discount");
		// tables.add("order_pay");
		tables.add("order_meta");
		tables.add("coupons");
		tables.add("member_coupon");
		tables.add("member_order_item");
		tables.add("store_log");
		tables.add("depot_user");
		tables.add("product_store");
		tables.add("depot");
		tables.add("goods_depot");
		tables.add("allocation_item");
		tables.add("returns_order");
		// tables.add("invoice"); 已转移至组件
	}

	public String getId() {
		return "shop";
	}

	public String getName() {
		return "shop应用";
	}

	public String getNameSpace() {
		return "/shop";
	}

	/**
	 * 系统初始化安装时安装base的sql脚本
	 */
	public void install() {
		this.doInstall("file:com/enation/app/shop/shop.xml");
		this.doInstall("file:com/enation/app/shop/gome.xml");
	}
	
	/**
	 * 清除缓存
	 */
	protected void cleanCache() {

		// 清除商品分类缓存
		CacheFactory.getCache(GoodsCatCacheProxy.CACHE_KEY).remove(GoodsCatCacheProxy.CACHE_KEY + "_" + userid + "_" + siteid + "_0");

		// 清除文章分类缓存
		CacheFactory.getCache(ArticleCatCacheProxy.cacheName).remove(ArticleCatCacheProxy.cacheName + "_" + userid + "_" + siteid);
	}


	/**
	 * session失效事件
	 */
	public void sessionDestroyed(String sessionid, EopSite site) {

		if (this.logger.isDebugEnabled()) {
			this.logger.debug("clean cart...");
		}

		// 清空购物车
		if (site != null) {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("site get from session ");
			}
			cartManager.clean(sessionid);
		} else {
			if (this.logger.isDebugEnabled()) {
				this.logger.debug("site get from session is null");
			}
		}
	}

	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}


	public ICartManager getCartManager() {
		return cartManager;
	}

	public void setCartManager(ICartManager cartManager) {
		this.cartManager = cartManager;
	}
}
