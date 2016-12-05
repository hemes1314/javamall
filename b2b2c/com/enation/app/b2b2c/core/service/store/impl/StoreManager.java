package com.enation.app.b2b2c.core.service.store.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.enation.framework.cache.CacheFactory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.component.plugin.store.StorePluginBundle;
import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.store.IStoreManager;
import com.enation.app.b2b2c.core.service.store.IStoreSildeManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.base.core.service.IRegionsManager;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.openapi.service.IWareOpenApiManager;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class StoreManager  extends BaseSupport implements IStoreManager{
	private IStoreMemberManager storeMemberManager;
	private IStoreSildeManager storeSildeManager;
	private IRegionsManager regionsManager;
	private StorePluginBundle storePluginBundle;
	private IMemberManager memberManager;
	private IGoodsManager goodsManager;
	private GoodsPluginBundle goodsPluginBundle;
	
	// OpenApi商品信息管理接口
	private IWareOpenApiManager wareOpenApiManager;
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#apply(com.enation.app.b2b2c.core.model.Store)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void apply(Store store) {
		//获取当前用户信息
		Member member=storeMemberManager.getStoreMember();
		if (member != null) {
			store.setMember_id(member.getMember_id());
			store.setMember_name(member.getUname());
		}
		this.getStoreRegions(store);
		this.daoSupport.insert("es_store", store);
		store.setStore_id(this.daoSupport.getLastId("es_store"));
		storePluginBundle.onAfterApply(store);
	}
	/**
	 * 获取店铺地址
	 * @param store
	 */
	private void getStoreRegions(Store store){
		store.setStore_province(regionsManager.get(store.getStore_provinceid()).getLocal_name());
		store.setStore_city(regionsManager.get(store.getStore_cityid()).getLocal_name());
		store.setStore_region(regionsManager.get(store.getStore_regionid()).getLocal_name());
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#audit_pass(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void audit_pass(Long member_id,Integer storeId,Integer pass,Integer name_auth,Integer store_auth,Double commission,Double wine_commission,Double chinese_spirits_commission,Double foreign_spirits_commission,Double beer_commission,Double other_spirits_commission,Double around_commission) {
		if(pass==1){
			store_auth=store_auth==null?0:store_auth;
			name_auth=name_auth==null?0:name_auth;
			this.daoSupport.execute("update es_store set create_time=?,name_auth=?,store_auth=?,commission=?,wine_commission =? ,chinese_spirits_commission =? ,foreign_spirits_commission =? ,beer_commission =? ,other_spirits_commission =?,around_commission =?  where store_id=?",DateUtil.getDateline(),name_auth,store_auth,commission,wine_commission,chinese_spirits_commission,foreign_spirits_commission,beer_commission,other_spirits_commission,around_commission,storeId);
			this.editStoredis(1, storeId);
			storePluginBundle.onAfterPass(this.getStore(storeId));
		}else{
			//审核未通过
			this.daoSupport.execute("update es_store set disabled=? where store_id=?",-1,storeId);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#store_list(java.util.Map, int, int)
	 */
	@Override
	public Page store_list(Map other,Integer disabled,int pageNo,int pageSize) {
		StringBuffer sql = new StringBuffer("");
		disabled = disabled == null ? 1 : disabled;
		String store_name = other.get("name") == null ? "" : other.get("name")
				.toString();
		String searchType = other.get("searchType") == null ? "" : other.get(
				"searchType").toString();
		
		String recently = "select count(*) from es_order eo where eo.store_id = s.store_id and status = 7 and create_time between "+((new Date().getTime()/1000)-7776000)+" and " +new Date().getTime()/1000;
		String goodsCount = "select count(*) from es_goods g where g.store_id=s.store_id and g.market_enable=1 and g.disabled=0";
		
		// 店铺状态
		if (disabled.equals(-2)) {
			sql.append("select ("+recently+") recently , s.* from es_store s   where  disabled!="//添加 查询到的列 recently 最近成交量
					+ disabled);
		} else {
			sql.append("select ("+recently+") recently ,("+ goodsCount +") as goods_count, s.* from es_store s   where  disabled="//添加 查询到的列 recently 最近成交量
					+ disabled);
		}
		if (!StringUtil.isEmpty(store_name)) {
			sql.append("  and s.store_name like ?");
		}
		if (!StringUtil.isEmpty(searchType) && !searchType.equals("default")) {
			sql.append(" order by " + searchType + " desc");
		} else {
			sql.append(" order by store_id" + " desc");
		}
		//System.out.println(sql.toString());
		if (!StringUtil.isEmpty(store_name)) {
		    return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize, "%"+ store_name +"%");
		} else {
		    return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#disStore(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void disStore(Integer storeId) {
		//关闭店铺
		this.daoSupport.execute("update es_store set  end_time=? where store_id=?",DateUtil.getDateline(), storeId);
		this.editStoredis(2, storeId);
		//修改会员店铺状态
		this.daoSupport.execute("update es_member set is_store=? where member_id=?",3,this.getStore(storeId).getMember_id());
		//更高店铺商品状态  lxl  update 将店铺里的商品下架
		this.daoSupport.execute("update es_goods set disabled=? , market_enable =?  where store_id=?", 1,0,storeId);
		
		// 查询指定店铺的所有商品
		List<Goods> goodsList = this.baseDaoSupport.queryForList("select * from es_goods where store_id=?", Goods.class, storeId);
		if (goodsList != null) {
			List<Goods> list = new ArrayList<Goods>(goodsList.size());
			Goods g = null;
			for (Goods goods : goodsList) {
				g = new Goods();
				g.setGoods_id(goods.getGoods_id());
				g.setMarket_enable(0);
				g.setDisabled(1);
				g.setEnable_store(goods.getEnable_store());
				list.add(g);
			}
			try {
				wareOpenApiManager.batchUpdateStatus(list);
	        } catch(Exception e) {
	            e.printStackTrace();
	            throw new RuntimeException(e);
	        }
		}
		
		//hp清除缓存
        try {
            //hp清除缓存
            com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            
            String  sql1="SELECT * from es_goods WHERE store_id=?";
            List<Map> goods_list=daoSupport.queryForList(sql1, storeId);
            for (Map goods:goods_list) {
                Long goodsid =(Long)goods.get("goods_id");
                iCache.remove(String.valueOf(goodsid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
		
		this.goodsPluginBundle.onAfterStoreDis(storeId);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#useStore(java.lang.Integer)
	 */
	@Override
	public void useStore(Integer storeId) {
		this.editStoredis(1, storeId);
		this.daoSupport.execute("update es_member set is_store="+1+" where member_id="+this.getStore(storeId).getMember_id());
		//更高店铺商品状态
		this.daoSupport.execute("update es_goods set disabled=? where store_id=?", 0,storeId);
		   //hp清除缓存
        try {
            //hp清除缓存
            com.enation.framework.cache.ICache  iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            
            String  sql1="SELECT * from es_goods WHERE store_id=?";
            List<Map> goods_list=daoSupport.queryForList(sql1, storeId);
            for (Map goods:goods_list) {
                Long goodsid =(Long)goods.get("goods_id");
                iCache.remove(String.valueOf(goodsid));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	/**
	 * 更改店铺状态
	 * @param disabled
	 * @param store_id
	 */
	private void editStoredis(Integer disabled,Integer store_id){
		this.daoSupport.execute("update es_store set disabled=? where store_id=?",disabled,store_id);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#getStore(java.lang.Integer)
	 */
	@Override
	public Store getStore(Integer storeId) {
		String sql="select * from es_store where store_id="+storeId;
		List<Store> list = this.baseDaoSupport.queryForList(sql,Store.class);
		Store store = (Store) list.get(0);
		if(store.getId_img()!=null&&!StringUtil.isEmpty(store.getId_img())){			
			store.setId_img( UploadUtil.replacePath(store.getId_img()));
		}
		if(store.getLicense_img()!=null&&!StringUtil.isEmpty(store.getLicense_img())){			
			store.setLicense_img( UploadUtil.replacePath(store.getLicense_img()));
		}
		store.setGoods_num(goodsManager.getCountByStore(store.getStore_id()));
		return store;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#editStore(com.enation.app.b2b2c.core.model.store.Store)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void editStore(Store store){
		StoreMember member=storeMemberManager.getStoreMember();
		this.daoSupport.update("es_store", store, "store_id="+member.getStore_id());
		if(store.getDisabled()==1){
			this.daoSupport.execute("update  es_member set is_store=1 where member_id=?",member.getMember_id() );
		}else{
			this.daoSupport.execute("update  es_member set is_store=2 where member_id=?",member.getMember_id() );
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#editStoreInfo(com.enation.app.b2b2c.core.model.store.Store)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void editStoreInfo(Store store){
		 this.daoSupport.update("es_store", store, " store_id="+store.getStore_id());
	}
	 public void editStore(Map store) {
		 this.daoSupport.update("es_store", store, " store_id="+store.get("store_id"));
	 }
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#checkStore()
	 */
	@Override
	public boolean checkStore() {
		Member member=storeMemberManager.getStoreMember();
		String sql="select count(store_id) from es_store where member_id=?";
		int isHas= this.daoSupport.queryForInt(sql, member.getMember_id());
		if(isHas>0)
			return true;
		else
			return false;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#save(com.enation.app.b2b2c.core.model.Store)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void save(Store store) {
		store.setMember_id(this.storeMemberManager.getMember(store.getMember_name()).getMember_id());
		store.setCreate_time(DateUtil.getDateline());
		this.daoSupport.insert("es_store", store);
		store.setStore_id(this.daoSupport.getLastId("es_store"));
		
		storePluginBundle.onAfterPass(store);
	}
	
	/**
	 * 注册商店，同事注册会员
	 * Navy Xue
	 * 2015-07-03
	 */
	public void registStore(Store store, Member member) {
		// 保存会员
		int addMemberReturn = this.memberManager.add(member);
		
		if (addMemberReturn == 1) {
		    
		}
		store.setMember_name(member.getUname());
		//暂时先将店铺等级定为默认等级，以后版本升级更改此处
		store.setStore_level(1);
		// 保存商店
		save(store);
		// 申请开店
		String sql="update es_member set is_store=1,store_id=? where member_id=?";
		daoSupport.execute(sql, store.getStore_id(),store.getMember_id());
	}
	
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#checkIdNumber(java.lang.String)
	 */
	@Override
	public Integer checkIdNumber(String idNumber) {
		String sql = "select member_id from store where id_number=?";
		List result = this.baseDaoSupport.queryForList(sql, idNumber);
		return  result.size() > 0 ? 1 : 0;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#editStoreOnekey(java.lang.String, java.lang.String)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void editStoreOnekey(String key, String value) {
		StoreMember member=storeMemberManager.getStoreMember();
		Map map=new HashMap();
		map.put(key,value);
		this.daoSupport.update("es_store", map, "store_id="+member.getStore_id());
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#collect(java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void addcollectNum(Integer storeid) {
		String sql = "update es_store set store_collect = store_collect+1 where store_id=?";
		this.baseDaoSupport.execute(sql, storeid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#getStore(java.lang.String)
	 */
	@Override
	public boolean checkStoreName(String storeName) {
		String sql="select  count(store_id) from es_store where store_name=?";
		Integer count= this.daoSupport.queryForInt(sql, storeName);
		return count!=0?true:false;
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#reduceCollectNum(java.lang.Integer)
	 */
	@Override
	public void reduceCollectNum(Integer storeid) {
		String sql = "update es_store set store_collect = store_collect-1 where store_id=? and store_collect>0";
		this.baseDaoSupport.execute(sql, storeid);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#saveStoreLicense(java.lang.Integer, java.lang.String, java.lang.String, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void saveStoreLicense(Integer storeid, String id_img,
			String license_img, Integer store_auth, Integer name_auth) {
		if(store_auth==2){
			this.daoSupport.execute("update es_store set store_auth=?,license_img=? where store_id=?",store_auth,license_img,storeid);
		}
		if(name_auth==2){
			this.daoSupport.execute("update es_store set name_auth=?,id_img=? where store_id=?",name_auth,id_img,storeid);
		}     
		
		//add by linyang 第一次图片提交失败，且通过验证
		if(store_auth==1){
            this.daoSupport.execute("update es_store set license_img=? where store_id=?",license_img,storeid);
        }
        if(name_auth==1){
            this.daoSupport.execute("update es_store set id_img=? where store_id=?",id_img,storeid);
        }
		
		
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#auth_list(java.util.Map, java.lang.String, int, int)
	 */
	@Override
	public Page auth_list(Map other, Integer disabled, int pageNo, int pageSize) {
	    StringBuffer sql=new StringBuffer("select s.* from es_store s   where  disabled="+disabled);
        sql.append(" and (store_auth=2 or name_auth=2 or store_auth=0 or name_auth=0)");
        return this.daoSupport.queryForPage(sql.toString(), pageNo, pageSize);
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#auth_pass(java.lang.Integer, java.lang.Integer, java.lang.Integer)
	 */
	@Override
	public void auth_pass(Integer store_id, Integer name_auth,
			Integer store_auth) {
		if(store_auth!=null){
				this.daoSupport.execute("update es_store set store_auth=? where store_id=?",store_auth,store_id);
		}
		if(name_auth!=null){
			this.daoSupport.execute("update es_store set name_auth=? where store_id=?",name_auth,store_id);
		}
	}
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#getStoreByMember(java.lang.Integer)
	 */
	@Override
	public Store getStoreByMember(Long memberId) {
		return (Store)this.daoSupport.queryForObject("select * from es_store where member_id=?", Store.class, memberId);
	}
	
	@Override
    public List<Store> getStoreByMemberName(String memberName) {
        return this.daoSupport.queryForList("select * from es_store where member_name=?", Store.class, memberName);
    }
	/*
	 * (non-Javadoc)
	 * @see com.enation.app.b2b2c.core.service.store.IStoreManager#reApply(com.enation.app.b2b2c.core.model.store.Store)
	 */
	@Override
	public void reApply(Store store) {
		//获取当前用户信息
		Member member=storeMemberManager.getStoreMember();
		if (member != null) {
			store.setMember_id(member.getMember_id());
			store.setMember_name(member.getUname());
		}
		this.daoSupport.update("es_store", store, "store_id="+store.getStore_id());
		storePluginBundle.onAfterApply(store);
	}
	
	public List getSearchWords(String keyword){
	    return this.daoSupport.queryForList("select * from (select store_id,store_name from es_store where store_name like ? order by store_id asc) where ROWNUM <= 10", "%" + keyword + "%");
	}
	
	@Override
	public List list() {
        String sql = "select * from store";
        return this.baseDaoSupport.queryForList(sql);
    }
	
	@Override
    public void updateStoreField(int goodsId, String fieldValue, String fieldName) {
        StringBuilder build = new StringBuilder("update es_store set ");
        build.append(fieldName).append("=? where store_id=?");
        this.baseDaoSupport.execute(build.toString(), fieldValue, goodsId);
    }

    @Override
    public void updateStoreField(Map<Integer, String> fieldValueMap, String fieldName) {
        if (fieldValueMap.isEmpty()) return;
        StringBuilder build = new StringBuilder("update es_store set ");
        build.append(fieldName).append("=? where store_id=?");
        List<Object[]> batchArgs = new ArrayList<Object[]>(fieldValueMap.size());
        for (Map.Entry<Integer, String> entry : fieldValueMap.entrySet()) {
            batchArgs.add(new Object[] { entry.getValue(), entry.getKey() });
        }
        this.baseDaoSupport.batchExecute(build.toString(), batchArgs);
    }
	
	public IMemberManager getMemberManager() {
		return memberManager;
	}
	public void setMemberManager(IMemberManager memberManager) {
		this.memberManager = memberManager;
	}
	public StorePluginBundle getStorePluginBundle() {
		return storePluginBundle;
	}
	public void setStorePluginBundle(StorePluginBundle storePluginBundle) {
		this.storePluginBundle = storePluginBundle;
	}

	public IStoreMemberManager getStoreMemberManager() {
		return storeMemberManager;
	}
	public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
		this.storeMemberManager = storeMemberManager;
	}
	public IStoreSildeManager getStoreSildeManager() {
		return storeSildeManager;
	}

	public void setStoreSildeManager(IStoreSildeManager storeSildeManager) {
		this.storeSildeManager = storeSildeManager;
	}
	public IRegionsManager getRegionsManager() {
		return regionsManager;
	}
	public void setRegionsManager(IRegionsManager regionsManager) {
		this.regionsManager = regionsManager;
	}
    
    public IGoodsManager getGoodsManager() {
        return goodsManager;
    }
    
    public void setGoodsManager(IGoodsManager goodsManager) {
        this.goodsManager = goodsManager;
    }
    
    public GoodsPluginBundle getGoodsPluginBundle() {
        return goodsPluginBundle;
    }
    
    public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
        this.goodsPluginBundle = goodsPluginBundle;
    }
    @Override
    public List<Store> getStoreByStorename(String storeName) {
       
        return this.daoSupport.queryForList("select * from es_store where STORE_NAME=?", Store.class, storeName);
    }
    @Override
    public Page list(int pageNo, int pageSize) {
        String sql = "select * from es_store";
        return this.daoSupport.queryForPage(sql, pageNo, pageSize);
    }
	public void setWareOpenApiManager(IWareOpenApiManager wareOpenApiManager) {
		this.wareOpenApiManager = wareOpenApiManager;
	}
}
