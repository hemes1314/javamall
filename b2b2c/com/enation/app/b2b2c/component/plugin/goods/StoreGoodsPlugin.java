package com.enation.app.b2b2c.component.plugin.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterEditEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsBeforeAddEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsBeforeEditEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsSearchFilter;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;
/**
 * 店铺商品Plugin
 * @author LiFenLong
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
@Component
public class StoreGoodsPlugin extends AutoRegisterPlugin implements IGoodsAfterEditEvent,IGoodsBeforeAddEvent,IGoodsBeforeEditEvent,IGoodsSearchFilter {
	private IGoodsGalleryManager goodsGalleryManager;
	private IStoreMemberManager storeMemberManager;
	private IProductManager productManager;
	private IGoodsManager goodsManager;
	private IDaoSupport daoSupport;
	
	/**
	 * 店铺商品修改后修改商品相册内容
	 */
	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
		String[] imgFs = request.getParameterValues("del_pic");
		if (imgFs != null && imgFs.length > 0) {
			// 数据库中的相册
			int goodsid = StringUtil.toInt(goods.get("goods_id").toString(), true);
			List<GoodsGallery> dbList = this.goodsGalleryManager.list(goodsid);
			Map<String, GoodsGallery> galleryMap = new HashMap<String, GoodsGallery>(dbList.size());
			for (GoodsGallery gallery : dbList) {
				galleryMap.put(gallery.getOriginal(), gallery);
			}
			List<GoodsGallery> deletedGalleries = new ArrayList<GoodsGallery>(imgFs.length);
			String static_server_domain= SystemSetting.getStatic_server_domain();
			String photoName = null;
			for (String img : imgFs) {
				photoName = img.replaceAll(static_server_domain, EopSetting.FILE_STORE_PREFIX);
				if (galleryMap.containsKey(photoName)) {
					goodsGalleryManager.delete(img);
					deletedGalleries.add(galleryMap.get(photoName));
				}
			}
			// 已删除的相册图片路径列表
			goods.put("deletedGalleries", deletedGalleries);
		}
		
		//更新购物车中商品价格
		daoSupport.execute("UPDATE es_cart SET price=? WHERE goods_id=?", goods.get("price"), goods.get("goods_id"));
	}
	/**
	 * 修改商品相册内容
	 * @param gallery
	 */
	private void edit(GoodsGallery gallery){
		daoSupport.update("es_goods_gallery", gallery, "img_id="+gallery.getImg_id());
	}

	/**
	 * 如果未上架改为已上架增加店铺商品数量
	 * 如果已上架改为不上架则减少商品数量
	 */
	@Override
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request) {
		Map map=goodsManager.get(Integer.valueOf(goods.get("goods_id").toString()));
		if(goods.get("market_enable").equals("1")){
			if(map.get("market_enable").toString().equals("0")){
				StoreMember member= storeMemberManager.getStoreMember();
				String sql="update es_store set goods_num=goods_num+1 where store_id=?";
				this.daoSupport.execute(sql, member.getStore_id());
			}
		}else if(goods.get("market_enable").equals("0")){
			if(map.get("market_enable").toString().equals("1")){
				StoreMember member= storeMemberManager.getStoreMember();
				String sql="update es_store set goods_num=goods_num-1 where store_id=?";
				this.daoSupport.execute(sql, member.getStore_id());
			}
		}
		//设置商品列表图片
		String[] status=request.getParameterValues("status");
		String[] imgFs=request.getParameterValues("goods_fs");
		if(status!=null&&imgFs!=null){
			if(status[0].equals("3")||status[0].equals("1")){
				goods.put("thumbnail", imgFs[0]);
			}
		}
	}
	@Override
	/**
	 * 添加商品修改店铺商品数量
	 */
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		//如果商品上架则更改店铺商品数量
		if(goods.get("market_enable").equals("1")){
			StoreMember member=storeMemberManager.getStoreMember();
			String sql="update es_store set goods_num=goods_num+1 where store_id=?";
			this.daoSupport.execute(sql, member.getStore_id());
		}
		//添加商品列表图片
		String[] status=request.getParameterValues("status");
		String[] imgFs=request.getParameterValues("goods_fs");
		if(status!=null&&imgFs!=null){
			if(status[0].equals("1")){
				goods.put("thumbnail", imgFs[0]);
			}
		}
		//添加商品购买次数以及评论次数
		goods.put("buy_num", 0) ;	//购买数量
		goods.put("comment_num", 0);	//评论次数
		
	}

	@Override
	public String getSelector() {
		
		return " ,s.store_name as store_name2";
	}
	@Override
	public String getFrom() {
		return " inner join es_store s on g.store_id=s.store_id ";
	}
	@Override
	public void filter(StringBuffer sql) {
		
	}
	
	/**
	 * 页面中传递过来的图片地址为:http://<staticserver>/<image path>如:
	 * http://static.enationsoft.com/attachment/goods/1.jpg
	 * 存在库中的为fs:/attachment/goods/1.jpg 生成fs式路径
	 * 
	 * @param path
	 * @return
	 */
	private String transformPath(String path) {
		String static_server_domain= SystemSetting.getStatic_server_domain();

		String regx =static_server_domain;
		path = path.replace(regx, EopSetting.FILE_STORE_PREFIX);
		return path;
	}
	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}
	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public IProductManager getProductManager() {
		return productManager;
	}
	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}
	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	
}
