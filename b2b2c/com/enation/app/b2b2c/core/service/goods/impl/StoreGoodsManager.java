package com.enation.app.b2b2c.core.service.goods.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.goods.StoreGoods;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.openapi.service.IWareOpenApiManager;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.database.Page;
import com.enation.framework.util.StringUtil;
@Component
public class StoreGoodsManager extends BaseSupport implements IStoreGoodsManager{
	private IStoreMemberManager storeMemberManager;
	private GoodsPluginBundle goodsPluginBundle;
	private IGoodsCatManager goodsCatManager;
	private IProductManager productManager;
	private IWareOpenApiManager wareOpenApiManager;
	
	/*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#storeGoodsList(java.lang.Integer, java.lang.Integer, java.util.Map)
     */
    @Override
    public Page storeGoodsList(Integer pageNo,Integer pageSize,Map map) {
        Integer store_id=Integer.valueOf(map.get("store_id").toString());
        Integer disable=Integer.valueOf(map.get("disable")+"");
        String store_cat=String.valueOf(map.get("store_cat"));
        String goodsName=String.valueOf(map.get("goodsName"));
        String market_enable=String.valueOf(map.get("market_enable"));
        
        StringBuffer sql=new StringBuffer("SELECT g.*,c.store_cat_name from es_goods g LEFT JOIN es_store_cat c ON g.store_cat_id=c.store_cat_id where g.store_id="+store_id +" and  g.disabled="+disable);
        
        if(!StringUtil.isEmpty(store_cat)&&!StringUtil.equals(store_cat, "null")&&!StringUtil.equals(store_cat, "0")){
            sql.append(" and g.store_cat_id="+store_cat);
        }
        if(!StringUtil.isEmpty(goodsName)&&!StringUtil.equals(goodsName, "null")){
            sql.append(" and ((g.name like '%"+goodsName+"%') or (g.sn like '%"+goodsName+"%') )");
        }
        if(!StringUtil.isEmpty(market_enable)&&!StringUtil.equals(market_enable, "null")){
            if(!market_enable.equals("-1")){
                sql.append(" and g.market_enable="+market_enable);
            }
        }
        
        sql.append(" and disabled = 0 order by g.create_time desc");
        return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
    }
    /*
     * (non-Javadoc)
     * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#storeGoodsList(java.lang.Integer, java.lang.Integer, java.util.Map)
     */
    @Override
    public Page storeGoodsListDels(Integer pageNo,Integer pageSize,Map map) {
        Integer store_id=Integer.valueOf(map.get("store_id").toString());
        Integer disable=Integer.valueOf(map.get("disable")+"");
        String store_cat=String.valueOf(map.get("store_cat"));
        String goodsName=String.valueOf(map.get("goodsName"));
        String market_enable=String.valueOf(map.get("market_enable"));
        
        StringBuffer sql=new StringBuffer("SELECT g.*,c.store_cat_name from es_goods g LEFT JOIN es_store_cat c ON g.store_cat_id=c.store_cat_id where g.store_id="+store_id +" and  g.disabled="+disable);
        
        if(!StringUtil.isEmpty(store_cat)&&!StringUtil.equals(store_cat, "null")&&!StringUtil.equals(store_cat, "0")){
            sql.append(" and g.store_cat_id="+store_cat);
        }
        if(!StringUtil.isEmpty(goodsName)&&!StringUtil.equals(goodsName, "null")){
            sql.append(" and ((g.name like '%"+goodsName+"%') or (g.sn like '%"+goodsName+"%') )");
        }
        if(!StringUtil.isEmpty(market_enable)&&!StringUtil.equals(market_enable, "null")){
            if(!market_enable.equals("-1")){
                sql.append(" and g.market_enable="+market_enable);
            }
        }
        
        sql.append(" order by g.create_time desc");
        return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#storeGoodsList(int, java.util.Map)
	 */
	@Override
	public List<Map> storeGoodsList(int storeid, Map map) {
		
		StringBuffer sql=new StringBuffer("SELECT g.* from es_goods g where g.store_id="+storeid +" and g.disabled=0");
		String store_catid=String.valueOf(map.get("store_catid"));
		String keyword=String.valueOf(map.get("keyword"));
		if(!StringUtil.isEmpty(store_catid) && !"0".equals(store_catid)){ //按店铺分类搜索
			sql.append(" and g.store_cat_id="+store_catid);
		}
		
		if(!StringUtil.isEmpty(keyword) ){
			sql.append(" and ((g.name like '%"+keyword+"%') or (g.sn like '%"+keyword+"%') )");
		}
		Integer market_enable = (Integer) map.get("market_enable");
		if(market_enable!=null){
            sql.append(" and g.market_enable="+market_enable);
        }
		return this.daoSupport.queryForList(sql.toString());
	}
	
	
	protected Map po2Map(Object po) {
		Map poMap = new HashMap();
		Map map = new HashMap();
		try {
			map = BeanUtils.describe(po);
		} catch (Exception ex) {
		}
		Object[] keyArray = map.keySet().toArray();
		for (int i = 0; i < keyArray.length; i++) {
			String str = keyArray[i].toString();
			if (str != null && !str.equals("class")) {
				if (map.get(str) != null) {
					poMap.put(str, map.get(str));
				}
			}
		}
		return poMap;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#b2b2cGoodsList(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page b2b2cGoodsList(Integer pageNo, Integer pageSize, Map map) {
		String keyword=(String) (map.get("namekeyword")==null?"":map.get("namekeyword"));
		String cat_id=(String) (map.get("cat_id")==null?"":map.get("cat_id"));
		String search_type=(String) (map.get("search_type")==null?"":map.get("search_type")); //0:默认、1:销量、2:价格
		StringBuffer sql=new StringBuffer("select g.*,s.store_name as store_name,s.qq as qq from es_goods g inner join es_store s on s.store_id=g.store_id INNER JOIN es_brand b ON b.brand_id=g.brand_id  where s.disabled=1 and g.disabled=0 and g.market_enable=1");
			
		if(!StringUtil.isEmpty(keyword)){
			sql.append("  and ((g.name like '%"+keyword+"%') or ( g.sn like '%"+keyword+"%') or(b.name like '%"+keyword+"%'))");
		}
		if (!StringUtil.isEmpty(cat_id) && cat_id!="0") {
			Cat cat = this.goodsCatManager.getById(NumberUtils.toInt(cat_id));
			sql.append(" and  g.cat_id in(select c.cat_id from es_goods_cat c where c.cat_path like '" + cat.getCat_path()+ "%')");
		}
		if(!StringUtil.isEmpty(search_type)){
			if(search_type.equals("1")){
				sql.append(" order by buy_num desc");
			}else if(search_type.equals("2")){
				sql.append(" order by price desc");
			}else{
				sql.append(" order by goods_id desc");
			}
		}
		//System.out.println(sql.toString());
		return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#store_searchGoodsList(java.lang.Integer, java.lang.Integer, java.util.Map)
	 */
	@Override
	public Page store_searchGoodsList(Integer page, Integer pageSize, Map params) {
		Integer storeid = (Integer) params.get("storeid");
		String keyword = (String) params.get("keyword");
		String start_price = (String) params.get("start_price");
		String end_price = (String) params.get("end_price");
		Integer key = (Integer) params.get("key");
		String order = (String) params.get("order");
		Integer cat_id = (Integer) params.get("stc_id");
		
		StringBuffer sql=new StringBuffer("select g.* from es_goods g left join es_store_cat c on c.store_cat_id=g.store_cat_id where g.disabled=0 and g.market_enable=1 and g.store_id="+storeid);
		
		if(!StringUtil.isEmpty(keyword)){
			sql.append("  and g.name like '%"+keyword+"%' ");
		}
		
		if(!StringUtil.isEmpty(start_price)){
			sql.append(" and g.price>="+ Double.valueOf(start_price));
		}
		if(!StringUtil.isEmpty(end_price)){
 			sql.append(" and g.price<="+ Double.valueOf(end_price));
		}
		
		if (cat_id!=null && cat_id!=0) {
			sql.append(" and  (g.store_cat_id="+cat_id+" or c.store_cat_pid="+cat_id+")");
		}
		
		if(key!=null){
			if(key==1){			//1:新品
				sql.append(" order by goods_id "+order);
			}else if(key==2){	//2:价格
				sql.append(" order by price "+order);
			}else if(key==3){	//3:销量
				sql.append(" order by buy_num "+order);
			}else if(key==4){	//4:收藏
				sql.append(" order by goods_id "+order);
			}else if(key==5){	//5:人气
				sql.append(" order by goods_id "+order);
			}
		}
		 
		Page webpage = this.baseDaoSupport.queryForPage(sql.toString(), page, pageSize);
		return webpage;
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#saveGoodsStore(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveGoodsStore(Integer storeid,Integer goods_id, Integer storeNum) {
        Product product=productManager.getByGoodsId(goods_id);
        Integer productid=product.getProduct_id();
        if (storeid == 0) { // 新库存
            this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)", goods_id, productid, 1, storeNum,storeNum);
        }else{
            // 更新库存
            this.daoSupport.execute("update es_product_store set enable_store=enable_store-store+?,store=? where goodsid=?", storeNum,storeNum, goods_id);
        }
        this.daoSupport.execute("update es_goods set enable_store=enable_store-store+?,store=? where goods_id=?", storeNum,storeNum, goods_id);
        this.daoSupport.execute("update es_product set enable_store=enable_store-store+?,store=? where product_id=?", storeNum,storeNum, productid);
        
        // 调用商品库存修改OpenApi接口（baoxiufeng）
        try {
            product = productManager.getByGoodsId(goods_id);
            wareOpenApiManager.updateStock(product);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        //hp清除缓存
        ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goods_id));
	}
	
    @SuppressWarnings("unchecked")
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveGoodsSpecStore(Integer[] store_id,Integer goods_id, Integer[] storeNum,Integer[] product_id){
        Map<Integer, Integer> productStoreMap = new HashMap<Integer, Integer>(store_id.length);
        for(int i= 0;i<store_id.length ;i ++){
            if(store_id[i] == 0) { //新库存
                this.daoSupport.execute("insert into es_product_store(goodsid,productid,depotid,store,enable_store)values(?,?,?,?,?)", goods_id, product_id[i], 1, storeNum[i],storeNum[i]);
            }else{ //更新库存  
                this.daoSupport.execute("update es_product_store set enable_store=enable_store-store+?,store=? where storeid=?", storeNum[i],storeNum[i], store_id[i]);
            }
            //更新某个货品的总库存 
            this.daoSupport.execute("update es_product set enable_store=enable_store-store+?,store=? where product_id=?", storeNum[i],storeNum[i], product_id[i]);
            productStoreMap.put(product_id[i], store_id[i]);
        }
        //更新商品总库存
        this.daoSupport.execute("update "+this.getTableName("goods")+" set store=(select sum(store) from "+this.getTableName("product_store")+" where goodsid=?),enable_store=(select sum(enable_store) from "+this.getTableName("product_store")+" where goodsid=?) where goods_id=? ", goods_id,goods_id,goods_id);
    
        // 调用商品SKU库存修改OpenApi接口（baoxiufeng）
        List<Map> mapList = productManager.list(product_id);
        List<Product> products = new ArrayList<Product>(mapList.size());
        Product product = null;
        for (Map map : mapList) {
            product = new Product();
            product.setProduct_id((Integer) map.get("product_id"));
            product.setEnable_store((Integer) map.get("enable_store"));
            product.setPrice((Double) map.get("price"));
            product.setStoreId(productStoreMap.get(product.getProduct_id()));
            products.add(product);
        }
        try {
        	// 调用商品可用库存修改OpenApi接口
            wareOpenApiManager.batchUpdateStock(products);
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#transactionList(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public List transactionList(Integer pageNo, Integer pageSize,
			Integer goods_id) {
		String sql="select * from  es_transaction_record where goods_id=? order by record_id";
		return  daoSupport.queryForListPage(sql, pageNo, pageSize, goods_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#transactionCount(java.lang.Integer)
	 */
	@Override
	public int transactionCount(Integer goods_id) {
		String sql="select count(0) from  es_transaction_record where goods_id=? ";
		return	this.daoSupport.queryForInt(sql, goods_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#getGoods(java.lang.Integer)
	 */
	@Override
	public StoreGoods getGoods(Integer goods_id) {
		String sql  = "select * from es_goods where goods_id=?";
		StoreGoods goods = (StoreGoods) this.baseDaoSupport.queryForObject(sql, StoreGoods.class, goods_id);
		return goods;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.goods.IStoreGoodsManager#getStoreGoodsNum(int)
	 */
	@Override
	public int getStoreGoodsNum(int struts) {
		StoreMember member  = storeMemberManager.getStoreMember();
		StringBuffer sql=new StringBuffer("SELECT count(goods_id) from es_goods where store_id=? and  disabled=0 and market_enable=?");
		return this.daoSupport.queryForInt(sql.toString(), member.getStore_id(),struts);
	}
	public IProductManager getProductManager() {
		return productManager;
	}

	public void setProductManager(IProductManager productManager) {
		this.productManager = productManager;
	}

	@Override
	public Map getGoodsStore(Integer goods_id) {
		 List<Map> list= this.daoSupport.queryForList("select * from es_product_store where goodsid=?", goods_id);
		 if(list.size()>0){
			 return list.get(0);
		 }else{
			 return null;
		 }
	}
	
	@Override
	public List getGoodsSpecStore(Integer goods_id){
		List<Map> list= this.daoSupport.queryForList("select * from es_product_store where goodsid=?", goods_id);
		if(list.size()>0){
			return list;
		}else{
			return new ArrayList();
		}
	}
	
	@Override
	public void addStoreGoodsComment(Integer goods_id) {

		String sql="update es_goods set comment_num=comment_num+1 where goods_id="+goods_id;
		this.daoSupport.execute(sql);
	      //hp清除缓存
        com.enation.framework.cache.ICache iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goods_id));

	}
	public GoodsPluginBundle getGoodsPluginBundle() {
		return goodsPluginBundle;
	}

	public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
		this.goodsPluginBundle = goodsPluginBundle;
	}

	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}


	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}

	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
    
    public void setWareOpenApiManager(IWareOpenApiManager wareOpenApiManager) {
        this.wareOpenApiManager = wareOpenApiManager;
    }
}
