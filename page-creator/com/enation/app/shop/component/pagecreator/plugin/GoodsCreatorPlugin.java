package com.enation.app.shop.component.pagecreator.plugin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.component.plugin.store.IStoreDisEvent;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.shop.component.pagecreator.service.impl.GeneralPageCreator;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterAddEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsAfterEditEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsStartChange;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.processor.HttpCopyWrapper;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;


/**
 * 商品静态页生成插件
 * @author kingapex
 *2015-3-25
 */
@Component
public class GoodsCreatorPlugin extends AutoRegisterPlugin implements
		IGoodsAfterAddEvent, IGoodsAfterEditEvent,IGoodsStartChange,IStoreDisEvent{
   
    private IDaoSupport<Object> daoSupport;
    
    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.component.plugin.store.IStoreDisEvent#onAfterStoreDis(int)
     */
    @Override
    public void onAfterStoreDis(int storeId) {
        try{
            String sql = "SELECT * FROM ES_GOODS WHERE STORE_ID = ?";
            List<Map> goodsList = this.daoSupport.queryForList(sql, storeId);
            
            // 遍历该商铺所有的商品 并刷新静态页
            for(Map goods : goodsList) {
                
                this.createGoodsPage(goods);
                
            }
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
        
    }
    
	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsAfterEditEvent#onAfterGoodsEdit(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void onAfterGoodsEdit(Map goods, HttpServletRequest request) {
	    try {
	        this.createGoodsPage(goods);
	    } catch(RuntimeException e) {
	        e.printStackTrace();
	    }
	    
	}

	/* (non-Javadoc)
	 * @see com.enation.app.shop.core.plugin.goods.IGoodsAfterAddEvent#onAfterGoodsAdd(java.util.Map, javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public void onAfterGoodsAdd(Map goods, HttpServletRequest arg1)
			throws RuntimeException {
	    
	    try {
            this.createGoodsPage(goods);
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
		
	}
	
	public void createGoodsPage(Map goods){
		int goodsid = Integer.valueOf( goods.get("goods_id").toString() );
		HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
		HttpCopyWrapper newrequest = new HttpCopyWrapper(request); 
		
		String root_path = StringUtil.getRootPath();
		String folder = root_path +"/html/goods";
		String pagename=("/goods-"+goodsid+".html");
		newrequest.setServletPath(pagename);
		ThreadContextHolder.setHttpRequest(newrequest);
		
		String pagePath =root_path+"/html/goods"+pagename;
		System.out.println("... ..."+pagePath);
		GeneralPageCreator pageCreator = new GeneralPageCreator(pagePath);
		pageCreator.parse(pagename);
		
	}

	@Override
	public void startChange(Map goods) {
		this.createGoodsPage(goods);
		
	}
	
	public void createGoodsPageForArr(String[] goodsIdArr){
	    HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
	    
	    for (String goodsIdStr: goodsIdArr) {
	        int goodsid = Integer.valueOf(goodsIdStr);
	        HttpCopyWrapper newrequest = new HttpCopyWrapper(request); 
	        
	        String root_path = StringUtil.getRootPath();
	        String folder = root_path +"/html/goods";
	        String pagename=("/goods-"+goodsid+".html");
	        newrequest.setServletPath(pagename);
	        ThreadContextHolder.setHttpRequest(newrequest);
	        
	        String pagePath =root_path+"/html/goods"+pagename;
	        GeneralPageCreator pageCreator = new GeneralPageCreator(pagePath);
	        pageCreator.parse(pagename);
	    }
        
    }

    
    public IDaoSupport<Object> getDaoSupport() {
        return daoSupport;
    }

    
    public void setDaoSupport(IDaoSupport<Object> daoSupport) {
        this.daoSupport = daoSupport;
    }

    

}
