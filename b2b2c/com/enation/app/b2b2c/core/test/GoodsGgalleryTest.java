package com.enation.app.b2b2c.core.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.framework.cache.CacheFactory;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.log4j.Logger;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.ISettingService;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.eop.sdk.utils.UploadUtil;

import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.FileUtil;
import com.enation.framework.util.StringUtil;
/**
 * 店铺商品图片API
 * @author LiFenLong
 *
 */
@Component
public class GoodsGgalleryTest {
	private ISettingService settingService ;
	private IGoodsGalleryManager goodsGalleryManager;
	private IGoodsManager goodsManager;
	private IDaoSupport daoSupport;
	String static_server_path= SystemSetting.getStatic_server_path();
	String static_server_domain= SystemSetting.getStatic_server_domain();
//	public void mock(){
//		settingService = this.getBean("settingService");
//		goodsGalleryManager = this.getBean("goodsGalleryManager");
//		goodsManager = this.getBean("goodsManager");
//		daoSupport = this.getBean("daoSupport");
//	}
//	public void oss(){
//		String static_server_path= SystemSetting.getStatic_server_path();
//		
//		List<Map> goods_list=goodsManager.list();
//		for (Map goods:goods_list) {
//			isExist(static_server_path+"/attachment/store/"+goods.get("store_id")+"/goods");
//		}
//	}
	public void isExist(String path){
		
		File file = new File(path);
		  //判断文件夹是否存在,如果不存在则创建文件夹
		 if(!file.exists()){
			 file.mkdir();
		 }
	}
	@Test
	public void  test(){
//		执行流程
//		1.获取商品以及商品图片列表
//		2.将商品图片存放到另外一个目录下
//		3.修改商品表
			try {
				List<Map> goods_list=goodsManager.list();
				for (Map goods:goods_list) {
					FileUtil.createFolder(static_server_path+"/attachment/store/"+goods.get("store_id")+"/goods");
					List<GoodsGallery> list = goodsGalleryManager.list(NumberUtils.toInt(goods.get("goods_id").toString()));
					String[] picnames=new String[list.size()];
					int num=0;
					for (GoodsGallery goodsGallery:list) {
						picnames[num]=UploadUtil.replacePath(goodsGallery.getOriginal().replace("http://static.b2b2cv2.javamall.com.cn",static_server_path));
						num=num+1;
					}
					this.proessPhoto(picnames, goods,UploadUtil.replacePath(goods.get("original").toString().replace("http://static.b2b2cv2.javamall.com.cn",static_server_path)) );
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
	}
	/**
	 * 生成缩略图
	 * @param picnames
	 * @param goods
	 * @param image_default
	 */
	public void proessPhoto(String[] picnames, Map goods, String image_default) {
		Integer store_id = NumberUtils.toInt(goods.get("store_id").toString());
		if (picnames == null) {
			return;
		}		

		// 生成相册列表，待jsm处理器使用
		List<GoodsGallery> galleryList = new ArrayList<GoodsGallery>();

		for (int i = 0; i < picnames.length; i++) {
			GoodsGallery gallery = new GoodsGallery();

			String filepath = picnames[i];
			if(filepath==null)
				continue;
			// 生成小缩略图
			String tiny = getThumbPath(filepath, "_tiny",store_id);
			// 生成缩略图
			String thumbnail = getThumbPath(filepath, "_thumbnail",store_id);
			// 生成小图
			String small = getThumbPath(filepath, "_small",store_id);
			// 生成大图
			String big = getThumbPath(filepath, "_big",store_id);

			gallery.setOriginal(filepath); // 相册原始路径
			gallery.setBig(big);
			gallery.setSmall(small);
			gallery.setThumbnail(thumbnail);
			gallery.setTiny(tiny);
			galleryList.add(gallery);
			//设置为默认图片
			if(!StringUtil.isEmpty(image_default) && image_default.equals(filepath)){
				gallery.setIsdefault(1);
			}
		}
		Map<String, Object> param = new HashMap<String, Object>(2);
		param.put("galleryList", galleryList);
		param.put("goods", goods);
		
		try {
			this.process(param);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}

	}
	private String getSettingValue(String code){
		return  settingService.getSetting("photo", code);
	}
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
		//获取店铺Id
		Integer store_id = NumberUtils.toInt(goods.get("store_id").toString());
		// 数据库中的相册
		List<GoodsGallery> dbList = this.goodsGalleryManager.list(goodsid);
	 	
		GoodsGallery defaultGallery = null;
		// 生成所有的相册图片的各种规格图片
		for (GoodsGallery gallery : list) {
	 		//原始图片
			String filepath = gallery.getOriginal();
			//System.out.println(transformPath(filepath.replace(static_server_path, static_server_domain)));
			gallery.setOriginal(transformPath(filepath.replace(static_server_path, static_server_domain))); // 转换为fs:开头
	 		
			// 寻找默认图片
			if (gallery.getIsdefault() == 1) {
				defaultGallery = gallery;
			}
	 		
			// 生成大图
			String targetpath = gallery.getBig();
			createThumb(filepath, targetpath, big_pic_width, big_pic_height);
			targetpath = transformPath(targetpath.replace(static_server_path, static_server_domain));
			gallery.setBig(targetpath);
	 		
			// 生成小图
			targetpath = gallery.getSmall();
			createThumb(filepath, targetpath, small_pic_width, small_pic_height);
			targetpath = transformPath(targetpath.replace(static_server_path, static_server_domain));
			gallery.setSmall(targetpath);	 		
	 		
			// 生成缩略图
			targetpath = gallery.getThumbnail();
			createThumb(filepath, targetpath, thumbnail_pic_width, thumbnail_pic_height);
			targetpath = transformPath(targetpath.replace(static_server_path, static_server_domain));
			gallery.setThumbnail(targetpath);	 		
	 		
	 		//生成小图
			targetpath = gallery.getTiny();
			createThumb(filepath, targetpath, tiny_pic_width, tiny_pic_height);
			targetpath = transformPath(targetpath.replace(static_server_path, static_server_domain));
			gallery.setTiny(targetpath);
	 		
			//生成原图
			createOrigin(gallery.getOriginal(),store_id);
			targetpath=transformPath(targetpath.replace(static_server_path, static_server_domain).replace("/goods", "/store/"+store_id+"/goods")); 
			gallery.setOriginal(targetpath);
			// 插入相册表
			gallery.setGoods_id(goodsid);
			this.goodsGalleryManager.add(gallery);	 		
	 	}
 
		// 更新商品默认图片信息
		if (defaultGallery != null) {
			this.daoSupport.execute(
					"update es_goods set original=?,big=?,small=?,thumbnail=? where goods_id=?",
					transformPath(defaultGallery.getOriginal()),
					transformPath(defaultGallery.getBig()),
					transformPath(defaultGallery.getSmall()),
					transformPath(defaultGallery.getThumbnail()),
					goodsid);
			this.daoSupport.execute("update es_goods_gallery set isdefault=0 where goods_id=?", goodsid);
			this.daoSupport.execute("update es_goods_gallery set isdefault=1 where goods_id=? and original=?",	goodsid, transformPath(defaultGallery.getOriginal()));
			 //hp清除缓存
            com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            iCache.remove(String.valueOf(goodsid));

		}
	}
	private void createOrigin(String filepath,Integer store_id){
		
		String static_server_domain= SystemSetting.getStatic_server_domain();
		String static_server_path= SystemSetting.getStatic_server_path();
		String thumbName=UploadUtil.replacePath(filepath).replace("/goods", "/store/"+store_id+"/goods");
		
		
		String serverPath = static_server_path.replaceAll("\\\\", "/");
		filepath = filepath.replaceAll(static_server_domain, serverPath);
		filepath = filepath.replaceAll("\\\\","/");
		thumbName = thumbName.replaceAll(static_server_domain, serverPath);
		thumbName = thumbName.replaceAll("\\\\","/");
		
		File tempFile = new File(thumbName);
		
		if (tempFile.exists()) {
			// //System.out.println("已存在");
		} else {
			filepath=UploadUtil.replacePath(filepath).replaceAll(static_server_domain, serverPath);
			System.out.println(filepath);
			FileUtil.copyFile(filepath, thumbName);
			//IThumbnailCreator thumbnailCreator = ThumbnailCreatorFactory.getCreator(filepath, thumbName);
			// //System.out.println("生成完成");
		}
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

		String regx =static_server_domain;
		path = path.replace(regx, EopSetting.FILE_STORE_PREFIX);
		return path;
	}
	/**
	 * 店铺的商品图片地址
	 * @param path
	 * @return
	 */
	private String getThumbPath(String filePath, String shortName,Integer store_id) {
		return UploadUtil.getThumbPath(filePath, shortName).replace("/goods", "/store/"+store_id+"/goods");
	}
	/**
	 * 创建文件
	 * @param filepath
	 * @param targetpath
	 * @param pic_width
	 * @param pic_height
	 */
	private void createThumb(String filepath, String targetpath, int pic_width, int pic_height) {
		try {
			this.goodsGalleryManager.createThumb(filepath, targetpath, pic_width, pic_height);
		} catch (Exception e) {
			e.printStackTrace();
		}
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
	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}
	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
}
