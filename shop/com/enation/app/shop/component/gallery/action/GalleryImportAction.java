package com.enation.app.shop.component.gallery.action;

import java.util.List;
import java.util.Map;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.action.WWAction;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.FileUtil;

/**
 * 相册数据升级
 * @author lzf
 * 2012-10-25下午2:51:56
 * ver 1.0
 */
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Results({ 
    @Result(name="import", type="freemarker", location="/com/enation/app/shop/component/gallery/action/import.html")
})
@Action("galleryImport")
public class GalleryImportAction extends WWAction {

	private IDaoSupport daoSupport;
	private IDBRouter baseDBRouter;
	private IGoodsGalleryManager goodsGalleryManager;

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IDBRouter getBaseDBRouter() {
		return baseDBRouter;
	}

	public void setBaseDBRouter(IDBRouter baseDBRouter) {
		this.baseDBRouter = baseDBRouter;
	}

	public IGoodsGalleryManager getGoodsGalleryManager() {
		return goodsGalleryManager;
	}

	public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
		this.goodsGalleryManager = goodsGalleryManager;
	}

	public String execute() {
		return "import";
	}
	
	public String imported(){
		try{
			if(EopSetting.DBTYPE.equals("1")) {//MySQL {
				String sql = "update " + this.baseDBRouter.getTableName("goods")+" set original = image_default,big=replace(image_default,concat('.',substring_index(image_default, '.', -1)),concat('_big.',substring_index(image_default, '.', -1))),small=replace(image_default,concat('.',substring_index(image_default, '.', -1)),concat('_small.',substring_index(image_default, '.', -1))),thumbnail=replace(image_default,concat('.',substring_index(image_default, '.', -1)),concat('_thumbnail.',substring_index(image_default, '.', -1)))"; 
				daoSupport.execute(sql);
			} else if(EopSetting.DBTYPE.equals("2")) {
				
			} else{//SQLServer
				daoSupport.execute("update " + this.baseDBRouter.getTableName("goods")+" set original = image_default");
				daoSupport.execute("update " + this.baseDBRouter.getTableName("goods")+" set big=replace(original, '.', '_big.'),small=replace(original, '.', '_small.'),thumbnail=replace(original, '.', '_thumbnail.')");
				//TODO 注意！上面一行的写法不能解决多个'.'的情况
			}
			List<Map> image_files = (List<Map>)this.daoSupport.queryForList("select goods_id, image_default,image_file from " + this.baseDBRouter.getTableName("goods"));
			for(Map map:image_files){
				if (map.get("image_default") == null || map.get("image_file") == null)
					continue;
				 
				String image_file = map.get("image_file").toString();

				// 数据里有错误，有的没有image_file
				String image_default = map.get("image_default").toString();
				String[] files = image_file.split(",");
				for (String file : files) {
					String ext = FileUtil.getFileExt(file);
					GoodsGallery gallery = new GoodsGallery();
					gallery.setGoods_id(Integer.valueOf(map.get("goods_id").toString()));
					gallery.setOriginal(file);
					gallery.setBig(file.replaceAll("." + ext, "_big." + ext));
					gallery.setThumbnail(file.replaceAll("." + ext, "_thumbnail." + ext));
					gallery.setTiny(file.replaceAll("." + ext, "_thumbnail." + ext));
					gallery.setSmall(file.replaceAll("." + ext, "_small." + ext));
					if (image_default.equals(file))
						gallery.setIsdefault(1);
					this.goodsGalleryManager.add(gallery);
				}
			}
			this.json = "{\"result\":\"1\"}";
		} catch (Exception e) {
			this.json = "{\"result\":\"0\"}";
			e.printStackTrace();
		}
		return this.JSON_MESSAGE;
	}

}
