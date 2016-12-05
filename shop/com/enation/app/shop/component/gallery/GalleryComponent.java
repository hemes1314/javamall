package com.enation.app.shop.component.gallery;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.enation.app.base.core.service.dbsolution.DBSolutionFactory;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.eop.sdk.context.EopSetting;
import com.enation.framework.component.IComponent;
import com.enation.framework.database.IDBRouter;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.FileUtil;

/**
 * 新的相册
 * @author lzf
 * 2012-10-19上午6:55:50
 * ver 1.0
 */
@Component
public class GalleryComponent implements IComponent {
	
	private IDaoSupport daoSupport;
	private IDBRouter baseDBRouter;
	private IGoodsGalleryManager goodsGalleryManager;
	
	@Override
	public void install() {
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/gallery/gallery_install.xml","es_");
	}

	@Override
	public void unInstall() {
		
		DBSolutionFactory.dbImport("file:com/enation/app/shop/component/gallery/gallery_install.xml","es_");

	}

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


}
