package com.enation.app.shop.component.pagecreator.service.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import com.enation.eop.SystemSetting;
import com.enation.eop.processor.facade.FacadePageParser;
import com.enation.framework.util.StringUtil;

/**
 * 商品静态页面生成器
 * @author kingapex
 *2015-3-25
 */
public class GoodsPageCreator extends FacadePageParser {
	private int goodsid;
	
	public  GoodsPageCreator(int _goodsid){
		this.goodsid=_goodsid;
	}
	
	@Override
	protected Writer getWriter() throws IOException{
		 String root_path = StringUtil.getRootPath();
		return new FileWriter(root_path+"/html/goods-"+this.goodsid+".html");
	}
	
 

	@Override
	protected void outError(Exception e) {
		e.printStackTrace();
	}
	 
}
