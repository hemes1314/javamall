package com.enation.app.shop.core.service.impl.cache;

import java.util.List;

import com.enation.app.shop.core.model.ArticleCat;
import com.enation.app.shop.core.service.IArticleCatManager;
import com.enation.eop.resource.model.EopSite;
import com.enation.eop.sdk.context.EopContext;
import com.enation.framework.cache.AbstractCacheProxy;

public class ArticleCatCacheProxy extends AbstractCacheProxy<List<ArticleCat>> implements
		IArticleCatManager {
	
	public static final String cacheName ="article_cat";
	private IArticleCatManager articleCatManager ;
	public ArticleCatCacheProxy(IArticleCatManager articleCatManager) {
		super(cacheName);
		this.articleCatManager = articleCatManager;
	}
	
	private String getKey(){
		return cacheName;
	}
	private void cleanCache(){
		this.cache.remove(getKey());
	}

	
	public int delete(int catId) {
		int r = this.articleCatManager.delete(catId);
		if(r==0){
			this.cleanCache();
		}
		return r;
	}

	
	public ArticleCat getById(int catId) {
		return this.articleCatManager.getById(catId);
	}

	
	public List listChildById(Integer catId) {
		List<ArticleCat> catList  = this.cache.get(this.getKey());
		if(catList== null ){
			catList  = this.articleCatManager.listChildById(catId);
			this.cache.put(this.getKey(), catList);
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load article cat form database");
			}
		}else{
			if(this.logger.isDebugEnabled()){
				this.logger.debug("load article cat form cache");
			}
		}
		
		return catList;
	}

	
	public List listHelp(int catId) {
		return this.articleCatManager.listHelp(catId);
	}

	
	public void saveAdd(ArticleCat cat) {
		this.articleCatManager.saveAdd(cat);
		this.cleanCache();
	}

	
	public void saveSort(int[] catIds, int[] catSorts) {
		this.articleCatManager.saveSort(catIds, catSorts);
		this.cleanCache();
	}

	
	public void update(ArticleCat cat) {
		this.articleCatManager.update(cat);
		this.cleanCache();
	}



}
