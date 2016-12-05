/**
 * 
 */
package com.enation.app.shop.component.goodsindex.action.api;

import java.util.List;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.shop.component.goodsindex.model.GoodsWords;
import com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.JsonMessageUtil;

/**
 * 商品分词 api action
 * @author kingapex
 *2015-4-19
 */
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("goods-words")
public class GoodsWordsApiAction extends WWAction {
	
	private String keyword;
	private List<GoodsWords> wordsList;
	
	private IGoodsIndexManager goodsIndexManager;
	
	public String execute(){
		
		try{
			this.wordsList = this.goodsIndexManager.getGoodsWords(keyword);
			
			this.json= JsonMessageUtil.getListJson(wordsList);
		}catch(Exception e){
			e.printStackTrace();
			this.showErrorJson("error");
		}
		return this.JSON_MESSAGE;
	}

	
	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public IGoodsIndexManager getGoodsIndexManager() {
		return goodsIndexManager;
	}

	public void setGoodsIndexManager(IGoodsIndexManager goodsIndexManager) {
		this.goodsIndexManager = goodsIndexManager;
	}


	public List<GoodsWords> getWordsList() {
		return wordsList;
	}


	public void setWordsList(List<GoodsWords> wordsList) {
		this.wordsList = wordsList;
	}

 
	
	
	 
}
