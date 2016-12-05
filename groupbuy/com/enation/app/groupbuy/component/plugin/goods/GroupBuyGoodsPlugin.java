package com.enation.app.groupbuy.component.plugin.goods;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.enation.framework.cache.CacheFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.groupbuy.component.plugin.act.IGroupBuyActDeleteEvent;
import com.enation.app.groupbuy.component.plugin.act.IGroupBuyActEndEvent;
import com.enation.app.groupbuy.component.plugin.act.IGroupBuyActStartEvent;
import com.enation.app.groupbuy.core.model.GroupBuyActive;
import com.enation.app.groupbuy.core.service.IGroupBuyActiveManager;
import com.enation.app.shop.component.pagecreator.plugin.GoodsCreatorPlugin;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.support.CartItem;
import com.enation.app.shop.core.plugin.goods.IGoodsBeforeAddEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsDeleteEvent;
import com.enation.app.shop.core.plugin.goods.IGoodsVisitEvent;
import com.enation.app.shop.core.plugin.order.IAfterOrderCreateEvent;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.SystemSetting;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.context.webcontext.WebSessionContext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.plugin.AutoRegisterPlugin;
import com.enation.framework.util.StringUtil;
/**
 * 
 * @ClassName: GroupBuyGoodsPlugin 
 * @Description: 团购商品插件 
 * @author TALON 
 * @date 2015-7-31 上午10:44:09 
 *
 */
@Component
public class GroupBuyGoodsPlugin extends AutoRegisterPlugin implements IAfterOrderCreateEvent,IGoodsVisitEvent,IGroupBuyActStartEvent,IGroupBuyActEndEvent,IGroupBuyActDeleteEvent,IGoodsBeforeAddEvent,IGoodsDeleteEvent{
	private IDaoSupport daoSupport;
	private IGroupBuyActiveManager groupBuyActiveManager;
	private IGoodsManager goodsManager;

    private GoodsCreatorPlugin goodsCreatorPlugin;
	/**
	 * 判断是否浏览过商品 如果没有浏览过商品则浏览次数加1
	 */
	@Override
	public void onVisit(Map goods) {
		if(groupBuyActiveManager.get() != null){
			WebSessionContext sessionContext = ThreadContextHolder.getSessionContext();
			List<Map> visitedGoods = (List<Map>)sessionContext.getAttribute("visitedGroupBuyGoods");
			
			Integer goods_id=Integer.valueOf(goods.get("goods_id").toString());
			boolean visited = false;
			if(visitedGoods==null) visitedGoods = new ArrayList<Map>();
			for(Map map:visitedGoods){
				if(map.get("goods_id").toString().equals(StringUtil.toString(goods_id))){//说明当前session访问过此商品
					visitedGoods.remove(map);
					visited = true;
					break;
				}
			}
			String  thumbnail =(String) goods.get("thumbnail");
			if(StringUtil.isEmpty(thumbnail)){
				String default_img_url = SystemSetting.getDefault_img_url();
				thumbnail=default_img_url;
			}else{
				thumbnail=UploadUtil.replacePath(thumbnail);
			}
			Map newmap = new HashMap();
			newmap.put("goods_id", goods_id);
			newmap.put("thumbnail", thumbnail);
			newmap.put("name", goods.get("name"));
			newmap.put("price", goods.get("price"));
			visitedGoods.add(0, newmap);
			sessionContext.setAttribute("visitedGroupBuyGoods", visitedGoods);
			if(!visited){
				GroupBuyActive curAct = groupBuyActiveManager.get();
            if (curAct != null) {
                Integer act_id=curAct.getAct_id();
    			String sql="update es_groupbuy_goods set view_num=view_num+1 where goods_id=? and act_id=?";
    			this.daoSupport.execute(sql, goods_id,act_id);
            }
			}
		}
	}
	@Override
	public void onEndGroupBuyEnd(Integer act_id) {
        String sql="update es_goods  SET is_groupbuy=0 WHERE goods_id in(SELECT goods_id from es_groupbuy_goods WHERE act_id=?)";
        this.daoSupport.execute(sql, act_id);
      //hp清除缓存
        com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);

        try {
            sql="SELECT * from es_groupbuy_goods WHERE act_id=?";
            List<Map> goods_list=daoSupport.queryForList(sql, act_id);
            for (Map goods:goods_list) {
                goodsManager.startChange(goods);
                //hp清除缓存
                Long goodsid =(Long)goods.get("goods_id");
                iCache.remove(String.valueOf(goodsid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onGroupBuyStart(Integer act_id) {
        //团购商品开启团购
        String sql="update es_goods  SET is_groupbuy=1 WHERE goods_id in(SELECT goods_id from es_groupbuy_goods WHERE act_id=?)";
        this.daoSupport.execute(sql, act_id);
        //hp清除缓存
        com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
       
        try {
            sql="SELECT * from es_groupbuy_goods WHERE act_id=?";
            List<Map> goods_list=daoSupport.queryForList(sql, act_id);
            for (Map goods:goods_list) {
                 //hp清除缓存
                Long goodsid =(Long)goods.get("goods_id");
                iCache.remove(String.valueOf(goodsid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }
	@Override
	public void onDeleteGroupBuyAct(Integer act_id) {
	    //还原商品
        this.daoSupport.execute("update es_goods set is_groupbuy=0 where goods_id in (select goods_id from es_groupbuy_goods where act_id=?)",act_id);
        //hp清除缓存
        com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
       
        try {
          String  sql1="SELECT * from es_groupbuy_goods WHERE act_id=?";
            List<Map> goods_list=daoSupport.queryForList(sql1, act_id);
            for (Map goods:goods_list) {
                 //hp清除缓存
                Long goodsid =(Long)goods.get("goods_id");
                iCache.remove(String.valueOf(goodsid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //删除商品
        this.daoSupport.execute("delete from es_groupbuy_goods WHERE act_id=?",act_id);
	}
	@Override
	public void onBeforeGoodsAdd(Map goods, HttpServletRequest request) {
		goods.put("is_groupbuy", 0);  //是否为团购商品
	}
	@Override
	public void onGoodsDelete(Integer[] goodsid) {
		String id_str = StringUtil.arrayToString(goodsid, ",");
		String sql = "delete  from es_groupbuy_goods where goods_id in (" + id_str + ")";
		this.daoSupport.execute(sql);
		
	}
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void onAfterOrderCreate(Order order, List<CartItem> itemList,String sessionid) {
	    GroupBuyActive act = groupBuyActiveManager.get();
        if(act==null) return;
        int act_id = act.getAct_id();
	    
		String sql = "select oi.* from es_order_items oi inner join es_goods g on oi.goods_id=g.goods_id  where order_id = ? and g.is_groupbuy=1";
		List<Map > orderitemList = this.daoSupport.queryForList(sql,order.getOrder_id());
		sql="update es_groupbuy_goods set buy_num=buy_num+?,goods_num=goods_num-? where goods_id=? and act_id=?";
		for (Map orderItem : orderitemList) {
			this.daoSupport.execute(sql, orderItem.get("num"),orderItem.get("num"),orderItem.get("goods_id"),act_id);
			
			// 2015/11/2 humaodong
            Integer goodsId = (Integer)orderItem.get("goods_id");
            int leftNum = -1;
            try {
               leftNum = this.daoSupport.queryForInt("select goods_num from es_groupbuy_goods where goods_id=? and act_id=?", goodsId,act_id);
            } catch(Exception e) {
            }
            if(leftNum <= 0) {
                this.daoSupport.execute("update es_goods set is_groupbuy=0 where goods_id=?", goodsId);
                if(SystemSetting.getStatic_page_open() == 1) {
                    String[] args = { goodsId + "" };
                    this.goodsCreatorPlugin.createGoodsPageForArr(args);
                }
                //hp清除缓存
                com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
                iCache.remove(String.valueOf(goodsId));
     
            }
		
		}
		
	}
	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}
	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	public IGroupBuyActiveManager getGroupBuyActiveManager() {
		return groupBuyActiveManager;
	}
	public void setGroupBuyActiveManager(
			IGroupBuyActiveManager groupBuyActiveManager) {
		this.groupBuyActiveManager = groupBuyActiveManager;
	}
	

    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }

    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }
    
    public GoodsCreatorPlugin getGoodsCreatorPlugin() {
        return goodsCreatorPlugin;
    }
    
    public void setGoodsCreatorPlugin(GoodsCreatorPlugin goodsCreatorPlugin) {
        this.goodsCreatorPlugin = goodsCreatorPlugin;
    }
    
}
