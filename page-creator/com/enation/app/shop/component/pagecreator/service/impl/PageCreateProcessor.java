/**
 * 
 */
package com.enation.app.shop.component.pagecreator.service.impl;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.base.core.model.TaskProgress;
import com.enation.app.base.core.service.ProgressContainer;
import com.enation.eop.processor.HttpCopyWrapper;
import com.enation.eop.processor.core.freemarker.FreeMarkerPaser;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.jms.IJmsProcessor;
import com.enation.framework.util.StringUtil;

/**
 * 静态页生jms处理器
 * @author kingapex
 *2015-3-27
 */
@Component
public class PageCreateProcessor implements IJmsProcessor {
	
	private IDaoSupport daoSupport;
	
	private static List<String> pages= new ArrayList();
 
	private String[] choose_pages; //要生成的页面
	public static final String PRGRESSID="page_create";
	 
	
	/* (non-Javadoc)
	 * @see com.enation.framework.jms.IJmsProcessor#process(java.lang.Object)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void process(Object data) {
		System.out.println("开始执行任务");
		FreeMarkerPaser.set(new FreeMarkerPaser());
		FreeMarkerPaser fmp = FreeMarkerPaser.getInstance();
		Map map = (Map)data;
		HttpServletRequest httpRequest =(HttpServletRequest) map.get("myrequest");
		choose_pages = (String[]) map.get("choose_pages");
		System.out.println("choose_pages :"+ choose_pages);

		System.out.println("choose_pages size:"+ choose_pages.length);
		ThreadContextHolder.setHttpRequest(httpRequest);
		try{
			
			
			String root_path = StringUtil.getRootPath();
			String folder = root_path +"/html";
			
			File file = new File(folder);		
			if (!file.exists() ){
				file.mkdirs();
			}
			
 
			
			if(this.checkExists("index")){pages.add("/index.html");}
			if(this.checkExists("register")){pages.add("/register.html");}
			if(this.checkExists("login")){pages.add("/login.html"); }
			
			
			int goodscount= this.getGoodsCount();
			int helpcount= this.getHelpCount();
			int othercount=pages.size();
			int allcount = goodscount+helpcount+othercount;
			
			//生成任务进度
			TaskProgress taskProgress = new TaskProgress(allcount);
			ProgressContainer.putProgress(PRGRESSID, taskProgress);
			
			
			this.createGeneralPages();
			
			if(this.checkExists("goods"))
			{
				//生成商品静态页 
				this.createGoodsPages();
			}
			
			if(this.checkExists("help")){
				createCmsPages();
			}
			
			ProgressContainer.getProgress(PRGRESSID).success();
			
		}catch(Exception e){
			
			ProgressContainer.getProgress(PRGRESSID).fail("生成出错");
			
			e.printStackTrace();
			
		}

	}
	
	
	
	
	
	/**
	 * 生成常用页面
	 */
	private void createGeneralPages(){
		
		
		String rootPath = StringUtil.getRootPath();
		HttpCopyWrapper request  =(HttpCopyWrapper)ThreadContextHolder.getHttpRequest();
		for (String pagename : pages) {
			
			request.setServletPath(pagename);
			String pagePath =rootPath+"/html"+pagename;
			
			GeneralPageCreator pageCreator = new GeneralPageCreator(pagePath);
			pageCreator.parse(pagename);
			ProgressContainer.getProgress(PRGRESSID).step("正在生成["+pagename+"]"); 
		}
		
	
	}
	
	
	/**
	 * 生成帮助中心的静态页
	 */
	private void createCmsPages(){
		
		String root_path = StringUtil.getRootPath();
		String folder = root_path +"/html/help";
		
		//如果help文件夹不存在，创建
		File file = new File(folder);		
		if (!file.exists() ){
			file.mkdirs();
		}
		
		int help_count = this.getHelpCount();
		int page_size=100; //100条查一次
		int page_count = 0;
		page_count = help_count / page_size;
		page_count = help_count % page_size > 0 ? page_count + 1 : page_count;
		
		
		HttpCopyWrapper request  =(HttpCopyWrapper)ThreadContextHolder.getHttpRequest();
		String sql="select cat_id,id,title from  es_helpcenter ";
		for(int i=1;i<=page_count;i++){
			List<Map>  list =this.daoSupport.queryForListPage(sql,i,page_size);

			for (Map map : list) {
				int catid = NumberUtils.toInt(map.get("cat_id").toString());
				int articleid = NumberUtils.toInt(map.get("id").toString());
				ThreadContextHolder.setHttpRequest(request);
				
				String pagename=("/help-"+catid+"-"+articleid+".html");
				request.setServletPath(pagename);
				String pagePath =root_path+"/html/help"+pagename;
				GeneralPageCreator pageCreator = new GeneralPageCreator(pagePath);
				pageCreator.parse(pagename);
				
				ProgressContainer.getProgress(PRGRESSID).step("正在生成帮助页["+map.get("title")+"]"); 
				
			}
		
		}
		
	}
	
	
	
	
	
	/**
	 * 生成商品静态页
	 */
	private void createGoodsPages(){
		
		String root_path = StringUtil.getRootPath();
		String folder = root_path +"/html/goods";
		
		//如果help文件夹不存在，创建
		File file = new File(folder);		
		if (!file.exists() ){
			file.mkdirs();
		}
		
		//商品总数
		int goods_count = this.getGoodsCount();
		int page_size=100; //100条查一次
		int page_count = 0;
		page_count = goods_count / page_size;
		page_count = goods_count % page_size > 0 ? page_count + 1 : page_count;
		 
		
		HttpCopyWrapper request  =(HttpCopyWrapper)ThreadContextHolder.getHttpRequest();
		for(int i=1;i<=page_count;i++){
			List<Map> goodsList  = this.daoSupport.queryForListPage("select goods_id,name from es_goods",i, page_size);
			for (Map goods : goodsList) {
				ProgressContainer.getProgress(PRGRESSID).step("正在生成商品页["+goods.get("name")+"]"); 
				int goodsid = Integer.valueOf( goods.get("goods_id").toString() );
				String pagename=("/goods-"+goodsid+".html");
				request.setServletPath(pagename);
				String pagePath =root_path+"/html/goods"+pagename;
				GeneralPageCreator pageCreator = new GeneralPageCreator(pagePath);
				pageCreator.parse(pagename);
				
				
			}
			
			

			
		}
		
	}
	
	/**
	 * 检测某个页面是否存在
	 * @param page
	 * @return
	 */
	private boolean checkExists(String page){
		if(this.choose_pages==null) return false;
		for(String choose:choose_pages){
			if(choose.equals(page)){
				return true;
			}
		}
		return false;
	}
	
	private int getGoodsCount(){
		if(this.checkExists("goods"))
		return this.daoSupport.queryForInt("select count(0) from es_goods") ;
		else
		return 0;
	}
	
	private int getHelpCount(){
		if(this.checkExists("help"))
		return this.daoSupport.queryForInt("select count(0) from  es_helpcenter ");
		else
		return 0;
	}
	

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
	 
	public static void main(String[] args) {
		BigDecimal b1 = new BigDecimal("1");
		BigDecimal b2 = new BigDecimal("458");
		BigDecimal b = b1.divide(b2,5,BigDecimal.ROUND_HALF_UP);
		System.out.println(b.doubleValue());
	}

	
}
