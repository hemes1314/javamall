/**
 * 
 */
package com.enation.app.shop.component.goodsindex.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.springframework.stereotype.Component;

import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import com.enation.app.shop.component.goodsindex.service.IGoodsIndexManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.plugin.search.SearchSelector;
import com.enation.app.shop.core.service.IGoodsSearchManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;

/**
 * 基于lucene全文检索的搜索器
 * @author kingapex
 *2015-4-20
 */
@Component
public class GoodsLuceneSearch implements IGoodsSearchManager {
	
	private IGoodsIndexManager goodsIndexManager;
	
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IGoodsSearchManager#getSelector(com.enation.app.shop.core.model.Cat)
	 */
	@Override
	public Map<String,Object> getSelector() {
		return   this.goodsIndexManager.createSelector();
	}
 
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.service.IGoodsSearchManager#search(int, int)
	 */
	@Override
	public Page search(int pageNo,int pageSize) {
		  return this.goodsIndexManager.search(pageNo, pageSize);
	}

	public IGoodsIndexManager getGoodsIndexManager() {
		return goodsIndexManager;
	}

	public void setGoodsIndexManager(IGoodsIndexManager goodsIndexManager) {
		this.goodsIndexManager = goodsIndexManager;
	}
 

}
