package com.enation.app.shop.component.goodscore.plugin.tag;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.Tag;
import com.enation.app.shop.core.plugin.goods.AbstractGoodsPlugin;
import com.enation.app.shop.core.plugin.goods.IGoodsTabShowEvent;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;

/**
 * 商品标签插件
 * @author kingapex
 * 2010-1-18下午02:56:43
 */
@Component
public class GoodsTagPlugin extends AbstractGoodsPlugin implements IGoodsTabShowEvent {
	private ITagManager tagManager;
	
	
	public void addTabs() {
 
	}

	
 

	
	
	/*为添加商品和修改商品页面填充必要的数据*/
	
	
	public String getAddHtml(HttpServletRequest request) {
		List<Tag> taglist  = this.tagManager.list();
		FreeMarkerPaser freeMarkerPaser = new FreeMarkerPaser(getClass());
		freeMarkerPaser.setPageName("tag");
		freeMarkerPaser.putData("tagList", taglist);
		return freeMarkerPaser.proessPageContent();
	}
	
	
	
	public String getEditHtml(Map goods, HttpServletRequest request) {
		List<Tag> taglist  = this.tagManager.list();
	 
		Integer goods_id =  Integer.valueOf(goods.get("goods_id").toString());
		List<Integer> tagIds=this.tagManager.list(goods_id);
	 
		
		FreeMarkerPaser freeMarkerPaser = new FreeMarkerPaser(getClass());
		freeMarkerPaser.setPageName("tag");
		freeMarkerPaser.putData("tagList", taglist);	
		freeMarkerPaser.putData("tagRelList", tagIds);
		return freeMarkerPaser.proessPageContent();
	}

	
	
	
	/*在保存添加和保存更新的时候，将tagid的数组和goodsid对应起来保存在库里*/
	
	
	
	public void onAfterGoodsAdd(Map goods, HttpServletRequest request)
			throws RuntimeException {
		if(goods.get("store_id")==null){
			this.save(goods, request);
		}

	}

	
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request){
		if(goods.get("store_id")==null){
			this.save(goods, request);
		}
	}

	private void save(Map goods, HttpServletRequest request){
		Integer goods_id =  Integer.valueOf(goods.get("goods_id").toString());
		
		String[] tagstr=  request.getParameterValues("tag_id");
		Integer[] tagids = null;
		if(tagstr!=null){
			tagids = new Integer[tagstr.length];
			for(int i=0;i<tagstr.length;i++){
				tagids[i]=	Integer.valueOf(tagstr[i]) ;
			}
		}
		this.tagManager.saveRels(goods_id, tagids);
	}
	
	
	
	
	
	
	

	
	public void onBeforeGoodsEdit(Map goods, HttpServletRequest request)
 {
		 

	}

	
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request)
			 {
		 
		 
	}

	
	
	public String getAuthor() {
		return "kingapex";
	}

	
	public String getId() {
		return "goodstag";
	}

	
	public String getName() {
		return "商品标签";
	}

	
	public String getType() {
		return null;
	}

	
	public String getVersion() {
		return "1.0";
	}

	
	public void perform(Object... params) {

	}


	public void setTagManager(ITagManager tagManager) {
		this.tagManager = tagManager;
	}






	@Override
	public String getTabName() {
		// TODO Auto-generated method stub
		return "标签";
	}






	@Override
	public int getOrder() {
		 
		return 13;
	}

}
