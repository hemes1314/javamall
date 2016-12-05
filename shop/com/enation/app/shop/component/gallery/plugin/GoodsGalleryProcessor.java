package com.enation.app.shop.component.gallery.plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.mobile.util.gfs.service.IGFSManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.jms.IJmsProcessor;
import com.enation.framework.util.StringUtil;

/**
 * 商品相册JMS处理器
 * @author kingapex
 *
 */
@Component
public class GoodsGalleryProcessor implements IJmsProcessor {

	private ISettingService settingService ;
	private IGoodsGalleryManager goodsGalleryManager;
	private IDaoSupport baseDaoSupport;
	private IGFSManager gfsManager;
	
	private String getSettingValue(String code){
		return  settingService.getSetting("photo", code);
	}
 
	private void createThumb(String filepath, String targetpath, int pic_width, int pic_height) {
		try {
			this.goodsGalleryManager.createThumb(filepath, targetpath, pic_width, pic_height);
		} catch (Exception e) {
			Logger logger = Logger.getLogger(getClass());
			logger.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void process(Object data) {
		
		/**
		 * 各种图片尺寸的大小
		 */
		int tiny_pic_width = 60;
		int tiny_pic_height = 60;
		int thumbnail_pic_width = 180;
		int thumbnail_pic_height = 180;
		int small_pic_width = 460;
		int small_pic_height = 460;
		int big_pic_width = 800;
		int big_pic_height = 800;
		
		String temp = getSettingValue("tiny_pic_width");
		tiny_pic_width = temp == null ? tiny_pic_width : StringUtil.toInt(temp,	true);

		temp = getSettingValue("tiny_pic_height");
		tiny_pic_height = temp == null ? tiny_pic_height : StringUtil.toInt(temp, true);

		temp = getSettingValue("thumbnail_pic_width");
		thumbnail_pic_width = temp == null ? thumbnail_pic_width : StringUtil.toInt(temp, true);
		
		temp = getSettingValue("thumbnail_pic_height");
		thumbnail_pic_height = temp == null ? thumbnail_pic_height : StringUtil.toInt(temp, true);		
		
		temp = getSettingValue("small_pic_width");
		small_pic_width = temp == null ? small_pic_width : StringUtil.toInt(temp, true);
	 
		temp = getSettingValue("small_pic_height");
		small_pic_height = temp == null ? small_pic_height : StringUtil.toInt(temp, true);

		temp = getSettingValue("big_pic_width");
		big_pic_width = temp == null ? big_pic_width : StringUtil.toInt(temp, true);

		temp = getSettingValue("big_pic_height");
		big_pic_height = temp == null ? big_pic_height : StringUtil.toInt(temp,	true);	
		
		
		Map<String, Object> param = (Map) data;
		List<GoodsGallery> list = (List<GoodsGallery>) param.get("galleryList"); // 相册列表
		Map goods = (Map) param.get("goods"); // 本商品
		int goodsid = StringUtil.toInt(goods.get("goods_id").toString(), true);
	 
		// 新增的相册图片路径列表
		List<String> insertedGalleries = new ArrayList<String>();
		// 数据库中的相册
		List<GoodsGallery> dbList = this.goodsGalleryManager.list(goodsid);
		GoodsGallery defaultGallery = null;
		// 生成所有的相册图片的各种规格图片
		for (GoodsGallery gallery : list) {
	 		//原始图片
			String filepath = gallery.getOriginal();
			gallery.setOriginal(transformPath(filepath)); // 转换为fs:开头
	 		
			// 寻找默认图片
			if (gallery.getIsdefault() == 1) {
				defaultGallery = gallery;
			}
	 		
			// 检测是否已经在数据库中，如果是则不再操作
			if (this.checkInDb(gallery.getOriginal(), dbList)) {
				continue;
			}
	 		
			// 生成大图
			String targetpath = gallery.getBig();
			createThumb(filepath, targetpath, big_pic_width, big_pic_height);
			targetpath = transformPath(targetpath);
			gallery.setBig(targetpath);
	 		
			// 生成小图
			targetpath = gallery.getSmall();
			createThumb(filepath, targetpath, small_pic_width, small_pic_height);
			targetpath = transformPath(targetpath);
			gallery.setSmall(targetpath);	 		
	 		
			// 生成缩略图
			targetpath = gallery.getThumbnail();
			createThumb(filepath, targetpath, thumbnail_pic_width, thumbnail_pic_height);
			targetpath = transformPath(targetpath);
			gallery.setThumbnail(targetpath);	 		
	 		
	 		//生成小图
			targetpath = gallery.getTiny();
			createThumb(filepath, targetpath, tiny_pic_width, tiny_pic_height);
			targetpath = transformPath(targetpath);
			gallery.setTiny(targetpath);
			
			// 原图转存GFS服务器
			gallery.setOriginal_gfs(gfsManager.handleImageToGFS(gallery.getOriginal()));
	 		
			// 插入相册表
			gallery.setGoods_id(goodsid);
			
			this.goodsGalleryManager.add(gallery);
			// 添加新增的相册图片路径到列表中
			insertedGalleries.add(gallery.getOriginal());
	 	}
		
		// 更新商品默认图片信息
		if (defaultGallery != null) {
			String original= transformPath(defaultGallery.getOriginal());
			String big =transformPath(defaultGallery.getBig());
			String small = transformPath(defaultGallery.getSmall());
			String thumbnail = transformPath(defaultGallery.getThumbnail());
			String original_gfs = defaultGallery.getOriginal_gfs();
			if (StringUtils.isNotBlank(original) && StringUtils.isBlank(original_gfs)) {
			    // 原图转存GFS服务器
			    original_gfs = gfsManager.handleImageToGFS(original);
			}
			this.baseDaoSupport.execute(
					"update goods set original=?,big=?,small=?,thumbnail=?,original_gfs=? where goods_id=?",
					original,
					big,
					small,
					thumbnail,
					original_gfs,
					goodsid);
			this.baseDaoSupport.execute("update goods_gallery set isdefault=0 where goods_id=?", goodsid);
			this.baseDaoSupport.execute("update goods_gallery set isdefault=1 where goods_id=? and original=?",	goodsid, transformPath(defaultGallery.getOriginal()));
	 	
			goods.put("original", original);
			goods.put("big", big);
			goods.put("small", small);
			goods.put("thumbnail", thumbnail);
			goods.put("original_gfs", original_gfs);
			goods.put("insertedGalleries", insertedGalleries);
		}
	}
	
	/**
	 * 检测某个图片是否已经存在
	 * 
	 * @param path
	 * @param dbList
	 * @return
	 */
	private boolean checkInDb(String path, List<GoodsGallery> dbList) {
		for (GoodsGallery gallery : dbList) {
			if (gallery.getOriginal().equals(path)) {
				return true;
			}
		}
		return false;
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

	public ISettingService getSettingService() {
		return settingService;
	}

	public void setSettingService(ISettingService settingService) {
		this.settingService = settingService;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}

	public IDaoSupport getBaseDaoSupport() {
		return baseDaoSupport;
	}

	public void setBaseDaoSupport(IDaoSupport baseDaoSupport) {
		this.baseDaoSupport = baseDaoSupport;
	}
    
    public void setGfsManager(IGFSManager gfsManager) {
        this.gfsManager = gfsManager;
    }
}
