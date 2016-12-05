package com.enation.app.shop.core.service.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.shop.component.gallery.model.GoodsGallery;
import com.enation.app.shop.component.gallery.service.IGoodsGalleryManager;
import com.enation.app.shop.core.model.Attribute;
import com.enation.app.shop.core.model.Cat;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsStores;
import com.enation.app.shop.core.model.Goodslist;
import com.enation.app.shop.core.model.Product;
import com.enation.app.shop.core.model.StoreLog;
import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.app.shop.core.model.support.GoodsTypeDTO;
import com.enation.app.shop.core.openapi.service.IWareOpenApiManager;
import com.enation.app.shop.core.plugin.goods.GoodsDataFilterBundle;
import com.enation.app.shop.core.plugin.goods.GoodsPluginBundle;
import com.enation.app.shop.core.service.IDepotMonitorManager;
import com.enation.app.shop.core.service.IGoodsCatManager;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IGoodsTypeManager;
import com.enation.app.shop.core.service.IMemberLvManager;
import com.enation.app.shop.core.service.IMemberPriceManager;
import com.enation.app.shop.core.service.IProductManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.IStoreLogManager;
import com.enation.app.shop.core.service.ITagManager;
import com.enation.app.shop.core.service.SnDuplicateException;
import com.enation.app.shop.mobile.util.gfs.service.IGFSManager;
import com.enation.eop.sdk.database.BaseSupport;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.cache.CacheFactory;
import com.enation.framework.cache.ICache;
import com.enation.framework.cache.redis.SystemConstants;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IntegerMapper;
import com.enation.framework.database.Page;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;
import com.opensymphony.xwork2.ActionContext;

import edu.emory.mathcs.backport.java.util.Arrays;

/**
 * Goods业务管理
 * 
 * @author kingapex 2010-1-13下午12:07:07
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class GoodsManager extends BaseSupport implements IGoodsManager {
	private ITagManager tagManager;
	private GoodsPluginBundle goodsPluginBundle;
	private ISellBackManager sellBackManager;
	private IGoodsCatManager goodsCatManager;
	private IMemberPriceManager memberPriceManager;
	private IMemberLvManager memberLvManager;
	private IDepotMonitorManager depotMonitorManager;
	private GoodsDataFilterBundle goodsDataFilterBundle;
	private IStoreLogManager storeLogManager;
	private IProductManager productManager;
	// private IDaoSupport<Goods> daoSupport;
	private ICache iCache;
	
	// OpenApi商品信息管理接口
	private IWareOpenApiManager wareOpenApiManager;
	private IGoodsGalleryManager goodsGalleryManager;
	private IGoodsTypeManager goodsTypeManager;
	private IGFSManager gfsManager;
	
	/**
	 * 添加商品，同时激发各种事件
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Goods goods) {
		try {
			Map goodsMap = po2Map(goods);
	        //add by lin 避免由于连续操作导致重复添加同一个商品
            String sql = "select * from es_goods where sn = ?";
            List g = this.baseDaoSupport.queryForList(sql, goodsMap.get("sn"));
            if(g.size() > 0) return;
			// 触发商品添加前事件
			goodsPluginBundle.onBeforeAdd(goodsMap);
			goodsMap.put("disabled", 0);
			goodsMap.put("create_time", DateUtil.getDateline());
			goodsMap.put("view_count", 0);
			goodsMap.put("buy_count", 0);
			goodsMap.put("last_modify", DateUtil.getDateline());
			goodsMap.put("store", 0);
		/*	
			if ("0".equals((String) goodsMap.get("have_spec"))) {              //添加商品时，默认商品库存是0，但是这是在没有规格的情况下。  如有问题，请高手优先修改这里，  whj-2015-05-21
				goodsMap.put("store", 0);
			}
		*/	
			// 设置商品价格和重量
            goods.setCost(Double.valueOf((String) goodsMap.get("cost")));
            goods.setPrice(Double.valueOf((String) goodsMap.get("price")));
            goods.setWeight(Double.valueOf((String) goodsMap.get("weight")));
			
			this.baseDaoSupport.insert("goods", goodsMap);
			Integer goods_id = this.baseDaoSupport.getLastId("goods");
			goods.setGoods_id(goods_id);
			goodsMap.put("goods_id", goods_id);
			goodsPluginBundle.onAfterAdd(goodsMap);
			
			// 商品原图转存GFS
			goods.setOriginal((String) goodsMap.get("original"));
			if (goodsMap.get("original_gfs") == null) {
			    goods.setOriginal_gfs(gfsManager.handleImageToGFS(goods.getOriginal()));
			} else {
			    goods.setOriginal_gfs((String) goodsMap.get("original_gfs"));
			}
			goods.setOriginal_gfs((String) goodsMap.get("original_gfs"));
            
			// 设置商品参数信息
            goods.setParams((String) goodsMap.get("params"));
			// 设置商品额外销售属性字段
            setGoodsAttributes(goods);
            // 查询商品的货品信息
            goods.setProduct(productManager.getByGoodsId(goods.getGoods_id()));
            // 查询商品的相册信息
            List<String> insertedGalleries = (List<String>) goodsMap.get("insertedGalleries");
            List<GoodsGallery> galleries = goodsGalleryManager.list(goods.getGoods_id());
            List<GoodsGallery> changedGalleries = new ArrayList<GoodsGallery>();
            if (insertedGalleries != null) {
            	for (GoodsGallery gallery : galleries) {
                	if (insertedGalleries.contains(gallery.getOriginal())) {
                		changedGalleries.add(gallery);
                	}
                }
            }
            goods.setGoodsGalleries(changedGalleries);
            // 调用商品添加OpenApi接口
            wareOpenApiManager.add(goods);
            
            //hp清除缓存
            iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            iCache.remove(String.valueOf(goods_id));
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
			e.printStackTrace();
		} catch(Exception e) {
            throw new RuntimeException(e);
        }
	}

	/**
	 * 设置商品额外销售属性字段.
	 * 
	 * @param goods 商品信息
	 */
    private void setGoodsAttributes(Goods goods) {
        GoodsTypeDTO goodsType = goodsTypeManager.get(goods.getType_id());
		Integer parseBrandId = 0;
        if (1 == goodsType.getHave_prop()) {
        	List<Cat> cats = goodsCatManager.listAllChildrenByType(goodsType.getType_id());
        	Map<String, Cat> attrTypeMap = new HashMap<String, Cat>();
        	Map<Integer, Map<String, Cat>> attrValueTypeMap = new HashMap<Integer, Map<String,Cat>>();
        	String catPath = null;
        	Map<String, Cat> catMap = null;
        	for (Cat cat : cats) {
        		catPath = cat.getCat_path();
        		catPath = catPath.replaceAll("\\d", "");
        		if (catPath.length() == 3) {
        			attrTypeMap.put(cat.getName(), cat);
        		} else {
        			catMap = attrValueTypeMap.get(cat.getParent_id());
        			if (catMap == null) {
        				attrValueTypeMap.put(cat.getParent_id(), catMap = new HashMap<String, Cat>());
        			}
        			catMap.put(cat.getName(), cat);
        		}
        	}
            HttpServletRequest request = (HttpServletRequest) ActionContext.getContext().get(ServletActionContext.HTTP_REQUEST);
            String[] propvalues = request.getParameterValues("propvalues");
            List<Map<String, Object>> attrMapList = new ArrayList<Map<String, Object>>();
            List<Attribute> attrs = goodsType.getPropList();
            List<String> attrKvs = new ArrayList<>(attrs.size());
            String k = null;
            String v = null;
            int i = 0;
            Cat c = null;
            for (Attribute attr : attrs) {
                c = attrTypeMap.get(attr.getName());
                if (c == null) continue;
                k = String.valueOf(c.getCat_id());
                switch (attr.getType()) {
                case 3:
                case 4:
                case 5:
                	catMap = attrValueTypeMap.get(c.getCat_id());
                    if (catMap == null) continue;
                    String[] s = attr.getOptionAr();
                    String p = propvalues[i];
                    Integer num = 0;
                    if (StringUtils.isNotBlank(p)) {
						num = NumberUtils.toInt(p);
					}
					if (s != null && s.length > num) {
						try {
							if("品牌".equals(attr.getName())){
                                parseBrandId = goodsType.getType_id()*10000+num;
                            }
						} catch (Exception e) {
							e.printStackTrace();
						}
						c = catMap.get(s[num]);
						if (c == null) continue;
						attrKvs.add(k + ":" + String.valueOf(c.getCat_id()));
                    }
                    break;
                case 6:
                    attrKvs.add(k + ":" + propvalues[i].replace("#", ",").substring(1).replaceAll("[:|]", " "));
                    break;
                default:
                	attrKvs.add(k + ":" + propvalues[i].replaceAll("[:|]", " "));
                    break;
                }
                i++;
            }
			goods.setBrand_id(parseBrandId);
            goods.setAttributes(StringUtils.join(attrKvs, "|"));
            // 更新商品表的所属品牌ID
            this.updateField("brand_id", parseBrandId, goods.getGoods_id());
        }
    }
	
	/**
	 * 修改商品同时激发各种事件
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(Goods goods) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("开始保存商品数据...");
			}
			Map goodsMap = this.po2Map(goods);
			
			this.goodsPluginBundle.onBeforeEdit(goodsMap);
			
			// 设置商品价格和重量
            goods.setCost(Double.valueOf((String) goodsMap.get("cost")));
            goods.setPrice(Double.valueOf((String) goodsMap.get("price")));
            goods.setWeight(Double.valueOf((String) goodsMap.get("weight")));
			
			this.baseDaoSupport.update("goods", goodsMap,
					"goods_id=" + goods.getGoods_id());
			String sql = "select * from es_goods where goods_id=?";
			
			goodsMap = this.daoSupport.queryForMap(sql, goods.getGoods_id());
			
			this.goodsPluginBundle.onAfterEdit(goodsMap);
			if (logger.isDebugEnabled()) {
				logger.debug("保存商品数据完成.");
			}
			
			// 商品原图转存GFS
            goods.setOriginal((String) goodsMap.get("original"));
            if (goodsMap.get("original_gfs") == null) {
                goods.setOriginal_gfs(gfsManager.handleImageToGFS(goods.getOriginal()));
            } else {
                goods.setOriginal_gfs((String) goodsMap.get("original_gfs"));
            }
            goods.setOriginal_gfs((String) goodsMap.get("original_gfs"));
            
            // 设置商品参数信息
            goods.setParams((String) goodsMap.get("params"));
            // 设置商品额外销售属性字段
            setGoodsAttributes(goods);
            // 查询商品的货品信息
            goods.setProduct(productManager.getByGoodsId(goods.getGoods_id()));
            // 查询商品的相册信息
            List<GoodsGallery> deletedGalleries = (List<GoodsGallery>) goodsMap.get("deletedGalleries");
            List<String> insertedGalleries = (List<String>) goodsMap.get("insertedGalleries");
            List<GoodsGallery> galleries = goodsGalleryManager.list(goods.getGoods_id());
            List<GoodsGallery> changedGalleries = new ArrayList<GoodsGallery>();
            if (insertedGalleries != null) {
            	for (GoodsGallery gallery : galleries) {
                	if (insertedGalleries.contains(gallery.getOriginal())) {
                		changedGalleries.add(gallery);
                	}
                }
            }
            if (deletedGalleries != null) {
            	for (GoodsGallery gallery : deletedGalleries) {
            		gallery.setDisabled(1);
            		changedGalleries.add(gallery);
            	}
            }
            goods.setGoodsGalleries(changedGalleries);
            // 调用商品编辑OpenApi接口
            wareOpenApiManager.edit(goods);
            
            //hp
            iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
            iCache.remove(String.valueOf(goods.getGoods_id()));
		} catch (RuntimeException e) {
			if (e instanceof SnDuplicateException) {
				throw e;
			}
			e.printStackTrace();
		} catch(Exception e) {
		    e.printStackTrace();
		    throw new RuntimeException(e);
        }
	}

	/**
	 * 得到修改商品时的数据
	 * 
	 * @param goods_id
	 * @return
	 */
	
	public GoodsEditDTO getGoodsEditData(Integer goods_id) {
		GoodsEditDTO editDTO = new GoodsEditDTO();
		try{
			
			String sql = "select * from goods where goods_id=?";
			Map goods = this.baseDaoSupport.queryForMap(sql, goods_id);

			String intro = (String) goods.get("intro");
			if (intro != null) {
				intro = UploadUtil.replacePath(intro);
				goods.put("intro", intro);
			}

			Map<Integer, String> htmlMap = goodsPluginBundle.onFillEditInputData(goods);

			editDTO.setGoods(goods);
			editDTO.setHtmlMap(htmlMap);
		}catch(Exception e){
			e.printStackTrace();
		}
		

		return editDTO;
	}

	/**
	 * 读取一个商品的详细<br/>
	 * 处理由库中读取的默认图片和所有图片路径:<br>
	 * 如果是以本地文件形式存储，则将前缀替换为静态资源服务器地址。
	 */

	
	public Map get(Integer goods_id) {
		String sql = "select g.*,b.name as brand_name from "
				+ this.getTableName("goods") + " g left join "
				+ this.getTableName("brand") + " b on g.brand_id=b.brand_id ";
		sql += "  where goods_id=?";
 
		//System.out.println(sql);
		
		Map goods = this.daoSupport.queryForMap(sql, goods_id);
		//2016-10-26 添加空指针判断
		if(goods != null){
			/**
			 * ====================== 对商品图片的处理 ======================
			 */
	 
			String small = (String) goods.get("small");
			if (small != null) {
				small = UploadUtil.replacePath(small);
				goods.put("small", small);
			}
			String big = (String) goods.get("big");
			if (big != null) {
				big = UploadUtil.replacePath(big);
				goods.put("big", big);
			}
		}
		return goods;
	}
	 public Map getByCache(Integer goods_id) {
	        Map goods = null;
	        //hp清除缓存
	        iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
	        goods =(Map)iCache.get(String.valueOf(goods_id));
	        if(goods==null||"".equals(goods)){
	          goods =this.get(goods_id);
	          iCache.put(String.valueOf(goods_id), goods);    
	        }
	 
	        return goods;
	    }
	public void getNavdata(Map goods) {
		// lzf 2011-08-29 add,lzy modified 2011-10-04
		int catid = (Integer) goods.get("cat_id");
		List list = goodsCatManager.getNavpath(catid);
		goods.put("navdata", list);
		// lzf add end
	}	

	private String getListSql(int disabled) {
		String selectSql = this.goodsPluginBundle.onGetSelector();
		String fromSql = this.goodsPluginBundle.onGetFrom();
		String sql = "";
		sql = "select g.*,b.name as brand_name ,t.name as type_name,c.name as cat_name "
				+ selectSql
				+ " from "
				+ this.getTableName("goods")
				+ " g left join "
				+ this.getTableName("goods_cat")
				+ " c on g.cat_id=c.cat_id left join "
				+ this.getTableName("brand")
				+ " b on g.brand_id = b.brand_id and b.disabled=0 left join "
				+ this.getTableName("goods_type")
				+ " t on g.type_id =t.type_id "
				+ fromSql
				+ " where g.disabled=" + disabled; // g.goods_type = 'normal' and 
	
		return sql;
	}

	/**
	 * 取得捆绑商品列表
	 * 
	 * @param disabled
	 * @return
	 */
	private String getBindListSql(int disabled) {
		String sql = "select g.*,b.name as brand_name ,t.name as type_name,c.name as cat_name from "
				+ this.getTableName("goods")
				+ " g left join "
				+ this.getTableName("goods_cat")
				+ " c on g.cat_id=c.cat_id left join "
				+ this.getTableName("brand")
				+ " b on g.brand_id = b.brand_id left join "
				+ this.getTableName("goods_type")
				+ " t on g.type_id =t.type_id"
				+ " where g.goods_type = 'bind' and g.disabled=" + disabled;
		return sql;
	}	

	/**
	 * 后台搜索商品
	 * 
	 * @param params
	 *            通过map的方式传递搜索参数
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page searchBindGoods(String name, String sn, String order, int page,
			int pageSize) {

		String sql = getBindListSql(0);

		if (order == null) {
			order = "goods_id desc";
		}

		if (name != null && !name.equals("")) {
			sql += "  and g.name like '%" + name + "%'";
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn = '" + sn + "'";
		}

		sql += " order by g." + order;
		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);

		List<Map> list = (List<Map>) (webpage.getResult());

		for (Map map : list) {
			List productList = sellBackManager.list(Integer.valueOf(map
					.get("goods_id").toString()));
			productList = productList == null ? new ArrayList() : productList;
			map.put("productList", productList);
		}

		return webpage;
	}

	/**
	 * 读取商品回收站列表
	 * 
	 * @param name
	 * @param sn
	 * @param order
	 * @param page
	 * @param pageSize
	 * @return
	 */
	public Page pageTrash(String name, String sn, String sort ,String order, int page,
			int pageSize) {

		String sql = getListSql(1);
		if (sort == null) {
		    sort = " g.goods_id ";
		}else{
		    if(sort.equals("brand_name")){
	            sort = " b.name";
	        }else if (sort.equals("cat_name")){
	            sort = " c.name";
	        } else{
	            sort = " g."+sort;
	        }
		}
		
		if(order == null){
		    order =" desc";
		}
		if (name != null && !name.equals("")) {
			sql += " and g.name like '%" + name + "%'";
		}

		if (sn != null && !sn.equals("")) {
			sql += " and g.sn = '" + sn + "'";
		}

		sql += " order by " + sort + "  " + order;

		Page webpage = this.daoSupport.queryForPage(sql, page, pageSize);

		return webpage;
	}

	
	
	/***
	 * 库存余量提醒分页列表
	 * 
	 * @param warnTotal
	 *            总报警数
	 * @param page
	 * @param pageSize
	 */
	public List<GoodsStores> storeWarnGoods(int warnTotal, int page, int pageSize) {
		// String sql =
		// " where g.market_enable = 1 and g.goods_type = 'normal' and g.disabled= 0 order by g.goods_id desc ";
		String select_sql = "select gc.name as gc_name,b.name as b_name,g.cat_id,g.goods_id,g.name,g.sn,g.price,g.last_modify,g.market_enable,s.sumstore ";
		String left_sql = " left join " + this.getTableName("goods") + " g  on s.goodsid = g.goods_id  left join " + this.getTableName("goods_cat") + " gc on gc.cat_id = g.cat_id left join " + this.getTableName("brand") + " b on b.brand_id = g.brand_id ";
		List<GoodsStores> list = new ArrayList<GoodsStores>();

		String sql_2 = select_sql
				+ " from  (select ss.* from (select goodsid,productid,sum(store) sumstore from " + this.getTableName("product_store") + "  group by goodsid,productid   ) ss "+
				"  left join " + this.getTableName("warn_num") + " wn on wn.goods_id = ss.goodsid  where ss.sumstore <=  (case when (wn.warn_num is not null or wn.warn_num <> 0) then wn.warn_num else ?  end )  ) s  "
				+ left_sql;
		List<GoodsStores> list_2 = this.daoSupport.queryForList(sql_2, new RowMapper() {
					@Override
					public Object mapRow(ResultSet rs, int arg1)
							throws SQLException {
						GoodsStores gs = new GoodsStores();
						gs.setGoods_id(rs.getInt("goods_id"));
						gs.setName(rs.getString("name"));
						gs.setSn(rs.getString("sn"));
						gs.setRealstore(rs.getInt("sumstore"));
						gs.setPrice(rs.getDouble("price"));
						gs.setLast_modify(rs.getLong("last_modify"));
						gs.setBrandname(rs.getString("b_name"));
						gs.setCatname(rs.getString("gc_name"));
						gs.setMarket_enable(rs.getInt("market_enable"));
						gs.setCat_id(rs.getInt("cat_id"));
						return gs;
					}
				}, warnTotal);
		list.addAll(list_2);// 普通商品		

		return list;
	}

	/**
	 * 批量将商品放入回收站
	 * 
	 * @param ids
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void delete(Integer[] ids) {
		if (ids == null) return;
		for (Integer id : ids) {
			this.tagManager.saveRels(id, null);
		}
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "update  goods set disabled=1 ,market_enable =0 where goods_id in ("
				+ id_str + ")";
		this.baseDaoSupport.execute(sql);
        // 同步调用OpenApi
		try {
		    wareOpenApiManager.batchDelete(Arrays.asList(ids));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        // hp清除缓存
        this.clearCache(ids);
	}

	/**
	 * 还原
	 * 
	 * @param ids
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void revert(Integer[] ids) {
		if (ids == null) return;
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "update  goods set disabled=0  where goods_id in ("
				+ id_str + ")";
		this.baseDaoSupport.execute(sql);
		
		// 同步调用OpenApi
		try {
		    wareOpenApiManager.batchRevert(Arrays.asList(ids));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
		// hp清除缓存
        this.clearCache(ids);
	}

	/**
	 * 清除
	 * 
	 * @param ids
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void clean(Integer[] ids) {
		if (ids == null)
			return;
		for (Integer id : ids) {
			this.tagManager.saveRels(id, null);
		}
		this.goodsPluginBundle.onGoodsDelete(ids);
		String id_str = StringUtil.arrayToString(ids, ",");
		String sql = "delete  from goods  where goods_id in (" + id_str + ")";
		this.baseDaoSupport.execute(sql);
		
		// 同步调用OpenApi
        try {
            wareOpenApiManager.batchClean(Arrays.asList(ids));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
		
		// hp清除缓存
        this.clearCache(ids);
	}

	public List list(Integer[] ids) {
		if (ids == null || ids.length == 0)
			return new ArrayList();
		String idstr = StringUtil.arrayToString(ids, ",");
		String sql = "select * from goods where goods_id in(" + idstr + ")";
		return this.baseDaoSupport.queryForList(sql);
	}

	public GoodsPluginBundle getGoodsPluginBundle() {
		return goodsPluginBundle;
	}

	public void setGoodsPluginBundle(GoodsPluginBundle goodsPluginBundle) {
		this.goodsPluginBundle = goodsPluginBundle;
	}

	/**
	 * 将po对象中有属性和值转换成map
	 * 
	 * @param po
	 * @return
	 */
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
    /**
     * 清除缓存
     * hp
     * @param ids
     * @return
     */
    public void clearCache(Integer[] ids) {
        if (ids == null)
            return;
        iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        for (Integer id : ids) {
            iCache.remove(String.valueOf(id));
            }
    }
  

	public Goods getGoods(Integer goods_id) {
		Goods goods = (Goods) this.baseDaoSupport.queryForObject(
				"select * from goods where goods_id=?", Goods.class, goods_id);
		return goods;
	}
	
	public IGoodsCatManager getGoodsCatManager() {
		return goodsCatManager;
	}

	public void setGoodsCatManager(IGoodsCatManager goodsCatManager) {
		this.goodsCatManager = goodsCatManager;
	}

	@Transactional(propagation = Propagation.REQUIRED)
	@Deprecated
	public void batchEdit() {
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		String[] ids = request.getParameterValues("goodsidArray");
		String[] names = request.getParameterValues("name");
		String[] prices = request.getParameterValues("price");
		String[] cats = request.getParameterValues("catidArray");
		String[] market_enable = request.getParameterValues("market_enables");
		String[] store = request.getParameterValues("store");
		String[] sord = request.getParameterValues("sord");

		String sql = "";

		for (int i = 0; i < ids.length; i++) {
			sql = "";
			if (names != null && names.length > 0) {
				if (!StringUtil.isEmpty(names[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " name='" + names[i] + "'";
				}
			}

			if (prices != null && prices.length > 0) {
				if (!StringUtil.isEmpty(prices[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " price=" + prices[i];
				}
			}
			if (cats != null && cats.length > 0) {
				if (!StringUtil.isEmpty(cats[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " cat_id=" + cats[i];
				}
			}
			if (store != null && store.length > 0) {
				if (!StringUtil.isEmpty(store[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " store=" + store[i];
				}
			}
			if (market_enable != null && market_enable.length > 0) {
				if (!StringUtil.isEmpty(market_enable[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " market_enable=" + market_enable[i];
				}
			}
			if (sord != null && sord.length > 0) {
				if (!StringUtil.isEmpty(sord[i])) {
					if (!sql.equals(""))
						sql += ",";
					sql += " sord=" + sord[i];
				}
			}
			sql = "update  goods set " + sql + " where goods_id=?";
			this.baseDaoSupport.execute(sql, ids[i]);

		}
		 //hp清除缓存
        if(ids!=null&&ids.length>0){
        Integer iids[] = new Integer[ids.length];
        for (int i = 0; i < ids.length; i++) {
			iids[i] = NumberUtils.toInt(ids[i]);
		}
			this.clearCache(iids);
		}
	}

	public Map census() {
		// 计算上架商品总数
		String sql = "select count(0) from goods where disabled = 0";
		int allcount = this.baseDaoSupport.queryForInt(sql);
				
		// 计算上架商品总数
		sql = "select count(0) from goods where market_enable=1 and  disabled = 0";
		int salecount = this.baseDaoSupport.queryForInt(sql);

		// 计算下架商品总数
		sql = "select count(0) from goods where market_enable=0 and  disabled = 0";
		int unsalecount = this.baseDaoSupport.queryForInt(sql);

		// 计算回收站总数
		sql = "select count(0) from goods where   disabled = 1";
		int disabledcount = this.baseDaoSupport.queryForInt(sql);

		// 读取商品评论数
		//sql = "select count(0) from comments where   for_comment_id is null  and commenttype='goods' and object_type='discuss'";
		sql = "select count(0) from member_comment where type=1";
		int discusscount = this.baseDaoSupport.queryForInt(sql);

		// 读取商品评论数
		//sql = "select count(0) from comments where for_comment_id is null  and  commenttype='goods' and object_type='ask'";
		sql = "select count(0) from member_comment where type=2";
		int askcount = this.baseDaoSupport.queryForInt(sql);

		Map<String, Integer> map = new HashMap<String, Integer>(2);
		map.put("salecount", salecount);
		map.put("unsalecount", unsalecount);
		map.put("disabledcount", disabledcount);
		map.put("allcount", allcount);
		map.put("discuss", discusscount);
		map.put("ask", askcount);
		return map;
	}
 
	/**
	 * 获取某个分类的推荐商品
	 */
	public List getRecommentList(int goods_id, int cat_id, int brand_id, int num) {
		 //原美睛网代码，去掉
		return null;
	}

	public List list() {
		String sql = "select * from goods where disabled = 0";
		List goodsList = this.baseDaoSupport.queryForList(sql);
		this.goodsDataFilterBundle.filterGoodsData(goodsList);
		return goodsList;
	}

	@Override
	public void updateField(String filedname, Object value, Integer goodsid) {
		this.baseDaoSupport.execute("update goods set " + filedname + "=? where goods_id=?", value, goodsid);
		   //hp清除缓存
        iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goodsid));

	}

	@Override
    public Goods getGoodBySn(String goodSn) {
        // update by lxl 
        String sql = " select * from goods where sn=? order by store_id" ;
        List<Goods>  goodsList= this.baseDaoSupport.queryForList(sql,Goods.class, goodSn);
        Goods goods = null ;
        if (!goodsList.isEmpty()){
            goods = (Goods) goodsList.get(0);
        }
//      Goods goods = (Goods) this.baseDaoSupport.queryForObject("select * from goods where sn=?", Goods.class, goodSn);
        return  goods;
    }
	
	@Override
    public Goods getGoodBySn(String goodsSn, Integer goodsId) {
        StringBuilder sql = new StringBuilder();
        sql.append("select * from goods where sn=? and goods_id <> ? order by store_id");
        List<Goods>  goodsList= baseDaoSupport.queryForList(sql.toString(), Goods.class, goodsSn, goodsId);
        Goods goods = null ;
        if (!goodsList.isEmpty()){
            goods = (Goods) goodsList.get(0);
        }
        return  goods;
    }

	public IDepotMonitorManager getDepotMonitorManager() {
		return depotMonitorManager;
	}

	public void setDepotMonitorManager(IDepotMonitorManager depotMonitorManager) {
		this.depotMonitorManager = depotMonitorManager;
	}
	
	@Override
	public List listByCat(Integer catid) {
		String sql = getListSql(0);
		if (catid.intValue() != 0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id in(";
			sql += "select c.cat_id from " + this.getTableName("goods_cat")
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%')  ";
		}
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public List listByTag(Integer[] tagid) {
		String sql = getListSql(0);
		if (tagid != null && tagid.length > 0) {
			String tagidstr = StringUtil.arrayToString(tagid, ",");
			sql += " and g.goods_id in(select rel_id from "
					+ this.getTableName("tag_rel") + " where tag_id in("
					+ tagidstr + "))";
		}
		return this.daoSupport.queryForList(sql);
	}

	@Override
	public void incViewCount(Integer goods_id) {
		this.baseDaoSupport.execute("update goods set view_count = view_count + 1 where goods_id = ?", goods_id);
		   //hp清除缓存
        iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goods_id));

	}
	
	
	public List listGoods(String catid,String tagid,String goodsnum){
		int num = 10;
		if(!StringUtil.isEmpty(goodsnum)){
			num = Integer.valueOf(goodsnum);
		}
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.* from es_tag_rel r LEFT JOIN es_goods g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		
		if(!StringUtil.isEmpty(catid) ){
			Cat cat  = this.goodsCatManager.getById(Integer.valueOf(catid));
			if(cat!=null){
				String cat_path  = cat.getCat_path();
				if (cat_path != null) {
					sql.append( " and  g.cat_id in(" ) ;
					sql.append("select c.cat_id from " + this.getTableName("goods_cat") + " ");
					sql.append(" c where c.cat_path like '" + cat_path + "%')");
				}
			}
		}
		
		if(!StringUtil.isEmpty(tagid)){
			sql.append(" AND r.tag_id="+NumberUtils.toInt(tagid, 0)+"");
		}
		
		sql.append(" order by r.ordernum asc");//这里应该按正序排
		//System.out.println(sql.toString());
		List list = new ArrayList();
		if(goodsnum==null){//如果goodsnum为null则该查询不受分页限制 需要查出所有的结果
		    list = this.daoSupport.queryForList(sql.toString());
		}else{
		    list = this.daoSupport.queryForListPage(sql.toString(), 1,num);
	        this.goodsDataFilterBundle.filterGoodsData(list);
		}
		
		return list;
	}

	public GoodsDataFilterBundle getGoodsDataFilterBundle() {
		return goodsDataFilterBundle;
	}

	public void setGoodsDataFilterBundle(GoodsDataFilterBundle goodsDataFilterBundle) {
		this.goodsDataFilterBundle = goodsDataFilterBundle;
	}

	@Override
	public List goodsBuyer(int goods_id, int pageSize) {
		String sql = "select distinct m.* from es_order o left join es_member m " +
				"on o.member_id=m.member_id where order_id in (select order_id from es_order_items " +
				"where goods_id=?)";
		Page page = this.daoSupport.queryForPage(sql, 1, pageSize, goods_id);
		
		return (List)page.getResult();
	}

	
	@Override
	public Page searchGoods(Map goodsMap, int page, int pageSize, String other,String sort,String order) {
		String sql = creatTempSql(goodsMap, other);
		//System.out.println(sql);
		StringBuffer _sql = new StringBuffer(sql);
		this.goodsPluginBundle.onSearchFilter(_sql);
		_sql.append(" order by "+sort+" "+order);
		Page webpage = this.daoSupport.queryForPage(_sql.toString(), page,pageSize);
		return webpage;
	}

	@Override
	public List searchGoods(Map goodsMap) {
		String sql = creatTempSql(goodsMap, null);
		return this.daoSupport.queryForList(sql,Goods.class);
	}
	
	@Override
	public Page searchGoodsForApp(Map goodsMap,String sort,String order,int page ,int pageSize) {
		String sql = getListSql(0);
		Integer catid = (Integer)goodsMap.get("cat_id");
		String name = (String) goodsMap.get("goods_name");
		String brand = (String) goodsMap.get("brand");
		Integer maxprice =  NumberUtils.toInt((String) goodsMap.get("maxprice"));
		Integer minprice = NumberUtils.toInt((String) goodsMap.get("minprice"));
		String tag_id =(String) goodsMap.get("tag_id");
		if (name != null && !name.equals("")) {
			name = name.trim();
			String[] keys = name.split("\\s");
			for (String key : keys) {
				sql += (" and g.name like '%");
				sql += (key);
				sql += ("%'");
			}
		}
		if (brand !=null && !StringUtil.isEmpty(brand)){
			sql += "  and g.params like '%"+brand+"%' ";
			
		}
		
		if(!StringUtil.isEmpty(tag_id)){
			sql += " and g.goods_id in (select rel_id from es_tag_rel where tag_id=" + tag_id +")";
		}
		
		if (catid != null && catid!=0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id="+ catid;

		}
		if (minprice!=null && minprice!=0){
			sql += " and g.price > "+minprice;
			
		}
		if(maxprice!=null && maxprice!=0){
			sql += " and g.price < "+maxprice;
		}
		if (sort.equals("grade")){
		   sort = sort.replace("grade","comment_num");
		}
			sql += " and g.disabled=0 and g.market_enable=1  ORDER BY "+sort+" "+order;
		System.out.println(sql);
		return this.daoSupport.queryForPage(sql,page,pageSize,Goodslist.class);
	}
	@Override
    public List searchGoods(Map goodsMap,String sort,String order) {
        String sql = getListSql(0);
        Integer catid = (Integer)goodsMap.get("catid");
        String name = (String) goodsMap.get("goods_name");
        Integer storeId = (Integer) goodsMap.get("store_id");
        String tag_id =(String) goodsMap.get("tag_id");
        
        if (name != null && !name.equals("")) {
            name = name.trim();
            String[] keys = name.split("\\s");
            for (String key : keys) {
                sql += (" and g.name like '%");
                sql += (key);
                sql += ("%'");
            }
        }
        
        if (storeId != null && storeId != 0) {
            sql += (" and g.store_id = " + storeId);
            
        }

        if (catid != null && catid!=0) {
            Cat cat = this.goodsCatManager.getById(catid);
            sql += " and  g.cat_id in(";
            sql += "select c.cat_id from " + this.getTableName("goods_cat")
                    + " c where c.cat_path like '" + cat.getCat_path()
                    + "%')  ";
        }

        if (!"".equals(sort)) {
            sql += " ORDER BY "+sort+" "+order;
        }
        
        if(!StringUtil.isEmpty(tag_id)){
            sql += "g.goods_id in (select rel_id from es_tag_rel where tag_id=" + tag_id +")";
        }
        
        return this.daoSupport.queryForList(sql,Goods.class);
    }

	
	private String creatTempSql(Map goodsMap,String other){
		other = other==null?"":other;
		String sql = getListSql(0);
		Integer brandid = (Integer) goodsMap.get("brandid");
		Integer catid = (Integer)goodsMap.get("catid");
		String name = (String) goodsMap.get("name");
		String sn = (String) goodsMap.get("sn");
		Integer[]tagid = (Integer[]) goodsMap.get("tagid");
		Integer stype = (Integer) goodsMap.get("stype");
		String keyword = (String) goodsMap.get("keyword");
		Integer market_enable = (Integer) goodsMap.get("market_enable");
		String tag_id =(String) goodsMap.get("tag_id");
		
		if (brandid != null && brandid != 0) {
			sql += " and g.brand_id = " + brandid + " ";
		}
		
		if("1".equals(other)){
			//商品属性为不支持打折的商品
			sql += " and g.no_discount=1";
		}
		if("2".equals(other)){
			//特殊打折商品，即单独设置了会员价的商品
			sql += " and (select count(0) from " + this.getTableName("goods_lv_price") + " glp where glp.goodsid=g.goods_id) >0";
		}
		
		if(stype!=null && keyword!=null){			
			if(stype==0){
				sql+=" and ( g.name like '%"+keyword+"%'";
				sql+=" or g.sn like '%"+keyword+"%')";
			}
		}
		
		if (name != null && !name.equals("")) {
			name = name.trim();
			String[] keys = name.split("\\s");
			for (String key : keys) {
				sql += (" and g.name like '%");
				sql += (key);
				sql += ("%'");
			}
		}

		if (sn != null && !sn.equals("")) {
			sql += "   and g.sn like '%" + sn + "%'";
		}


		if (catid != null && catid!=0) {
			Cat cat = this.goodsCatManager.getById(catid);
			sql += " and  g.cat_id in(";
			sql += "select c.cat_id from " + this.getTableName("goods_cat")
					+ " c where c.cat_path like '" + cat.getCat_path()
					+ "%')  ";
		}

		if (tagid != null && tagid.length > 0) {
			String tagidstr = StringUtil.arrayToString(tagid, ",");
			sql += " and g.goods_id in(select rel_id from "
					+ this.getTableName("tag_rel") + " where tag_id in("
					+ tagidstr + "))";
		}
		
		if(market_enable!=null){
			sql+=" and market_enable="+market_enable;
		}
		
		if(!StringUtil.isEmpty(tag_id)){
            sql += "g.goods_id in (select rel_id from es_tag_rel where tag_id=" + tag_id +")";
        }
		//System.out.println(sql);
		return sql;
	}
	
	@Override
	public List listByCat(String tagid, String catid,String goodsnum){
		
		StringBuffer sql = new StringBuffer();
		sql.append("select g.* from es_tag_rel r LEFT JOIN es_goods g ON g.goods_id=r.rel_id where g.disabled=0 and g.market_enable=1");
		
		if(!StringUtil.isEmpty(catid) ){
			Cat cat  = this.goodsCatManager.getById(Integer.valueOf(catid));
			if(cat!=null){
				String cat_path  = cat.getCat_path();
				if (cat_path != null) {
					sql.append( " and  g.cat_id in(" ) ;
					sql.append("select c.cat_id from " + this.getTableName("goods_cat") + " ");
					sql.append(" c where c.cat_path like '" + cat_path + "%')");
				}
			}
		}
		
		if(!StringUtil.isEmpty(tagid)){
			sql.append(" AND r.tag_id="+tagid+"");
		}
		
		sql.append(" order by r.ordernum desc");
		List list = null;
		if(goodsnum == null || "".equals(goodsnum)){
			list = this.daoSupport.queryForList(sql.toString());
		} else {
			list = this.daoSupport.queryForListPage(sql.toString(), 1, NumberUtils.toInt(goodsnum));
		}
		this.goodsDataFilterBundle.filterGoodsData(list);
		return list;
	}
	
	public List<Map<String, Object>> searchGoodsForActivity(Integer storeId, Integer activityId) {
	    String cond = "g.market_enable=1 ";
	    //过滤库存为0的数据
        cond += " and g.enable_store <> 0 ";
	    if (storeId != null) {
	        if (cond.length() > 0) {
                cond += " and ";
            }
	        cond += "g.store_id=" + storeId;
	    }
	    
	    if (activityId != null) {
	        if (cond.length() > 0) {
	            cond += " and ";
	        }
	        cond += "ag.activity_id=" + activityId;
	    }
	    
	    
	    if (cond.isEmpty()) {  
	        cond = "1=1";
	     
	    }
	    String sql = "select g.* from es_goods g left join es_activity_goods ag on ag.goods_id=g.goods_id where "+ cond +" order by g.goods_id desc";
	    List<Map<String, Object>> list = this.baseDaoSupport.queryForList(sql);
        
        return list;	    
	}
	
	public List<Map<String, Object>> searchGoodsForGift(Integer storeId, Integer activityId) {
        String cond = "";
        
        if (storeId != null) {
            cond += "g.store_id=" + storeId;
        }
        
        if (activityId != null) {
            if (cond.length() > 0) {
                cond += " and ";
            }
            cond += "ag.activity_id=" + activityId;
        }
        
        if (cond.isEmpty()) {
            cond = "1=1";
        }
        String sql = "select g.* from es_goods g left join es_activity_gift ag on ag.goods_id=g.goods_id where "+ cond +" order by g.goods_id desc";
        List<Map<String, Object>> list = this.baseDaoSupport.queryForList(sql);
        
        return list;
    }
	
	/*
     * (non-Javadoc)
     * @see com.enation.app.shop.core.service.IGoodsManager#startChange(java.util.Map)
     */
    public void startChange(Map goods){
        goodsPluginBundle.onStartchange(goods);
    }
    
	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}

	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}
	
	public IMemberPriceManager getMemberPriceManager() {
        return memberPriceManager;
    }

    public ITagManager getTagManager() {
        return tagManager;
    }

    public void setTagManager(ITagManager tagManager) {
        this.tagManager = tagManager;
    }

    public void setMemberPriceManager(IMemberPriceManager memberPriceManager) {
        this.memberPriceManager = memberPriceManager;
    }

    public IMemberLvManager getMemberLvManager() {
        return memberLvManager;
    }

    public void setMemberLvManager(IMemberLvManager memberLvManager) {
        this.memberLvManager = memberLvManager;
    }

	@Override
	public void insertHistory(Integer member_id, String session_id) {
		
		this.baseDaoSupport.execute(" intsert es_history set member_id =? ,session_id=? ,goods_id=? ", session_id,member_id);
		
	}


	@Override
	public void addGoods(Goods goods) {
		this.baseDaoSupport.insert("goods", goods);
	}
	
	@Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int erpAddGoods(Goods goods) {
        this.baseDaoSupport.insert("goods", goods);
        int goodsId = this.baseDaoSupport.getLastId("es_goods");
        
        Product product = new Product();
        product.setGoods_id(goodsId);
        product.setName(goods.getName());
        product.setSn(goods.getSn());
        product.setStore(goods.getStore());
        product.setEnable_store(goods.getEnable_store());
        product.setPrice(goods.getPrice());
        product.setCost(goods.getCost());
        product.setWeight(0D);
        List<Product> productList = new ArrayList<Product>();
        productList.add(product);
        productManager.add(productList);
        int productId = this.baseDaoSupport.getLastId("es_product");
        
        Map<String, Object> productStoreMap = new HashMap<String, Object>();
        productStoreMap.put("goodsid", goodsId);
        productStoreMap.put("productid", productId);
        productStoreMap.put("depotid", 1);
        productStoreMap.put("store", goods.getStore());
        productStoreMap.put("enable_store", goods.getEnable_store());
        this.baseDaoSupport.insert("es_product_store", productStoreMap);
        
        return goodsId;
    }


	@Override
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStore(Integer goodsid,Integer num ,Integer storeId) {
	    // new update by lxl
	    Goods goods = this.getGoods(goodsid);
	    Integer productid = this.getProductId(goodsid);
	    //更新产品库存
	    Integer useStore =  goods.getStore() - goods.getEnable_store();
	    Integer store = num;
        Integer enableStore = num - useStore;
	    String sql = "update product_store set enable_store="+ enableStore +", store="+ store +" where goodsid=?";
	    this.baseDaoSupport.execute(sql, goodsid);
	    
        //更新商品总库存
	    sql = "update es_goods set enable_store="+ enableStore +", store="+ store +" where goodsid=?";
        this.daoSupport.execute(sql, goodsid);
        
        //更新某个货品的总库存
        sql = "update es_product set enable_store="+ enableStore +", store="+ store +" where product_id=? ";
        this.daoSupport.execute(sql, productid);
    
	    
        //add 库存日志
        StoreLog storeLog = new StoreLog();
        storeLog.setGoodsid(goodsid);
        storeLog.setGoodsname(goods.getName());
        storeLog.setDepot_type(0);
        storeLog.setOp_type(1);
        storeLog.setDepotid(1); // 仓库id  暂时设为1
        storeLog.setDateline(DateUtil.getDateline());
        storeLog.setNum(num);
        storeLog.setUserid(0L);
        storeLog.setUsername("erp");
        storeLogManager.add(storeLog);
        //hp清除缓存
        iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goodsid));

	//	this.baseDaoSupport.execute(" update es_goods set store=? where sn=?", store,sn);
	}
	private Integer getProductId(Integer goodsid) {
        String sql = "select product_id from product where goods_id = ?";
        List<Integer> productidList = this.baseDaoSupport.queryForList(sql, new IntegerMapper(), goodsid);
        Integer productid = productidList.get(0);
        return productid;
    }

	@Override
	public void updatePrice(String sn, Double price) {
		
		this.baseDaoSupport.execute(" update es_goods set price=? where sn=?", price,sn);
		 //hp
        String sql1 = "select * from es_goods where sn=?";
        Map goodsMap = this.daoSupport.queryForMap(sql1, sn);
       // Long goodsid =(Long)goodsMap.get("goods_id");
        //hp清除缓存
        iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goodsMap.get("goods_id")));

	}


	@Override
	public void addStore_id(String sn, Store store) {
		String sql = " update es_goods set store_id =?,store_name=?,commission=? where sn=?";
		this.baseDaoSupport.execute(sql, store.getStore_id(),store.getStore_name(),
				store.getCommission(),sn);
		  
        //hp
        String sql1 = "select * from es_goods where sn=?";
        Map goodsMap = this.daoSupport.queryForMap(sql1, sn);
        //hp清除缓存
        iCache = CacheFactory.getCache(SystemConstants.PRE_GOODS);
        iCache.remove(String.valueOf(goodsMap.get("goods_id")));
  
	}


    @Override
    public Page searchHistory(int pageNo, int pageSize, Integer member_id) {
        
        String sql = " select oi.goods_id, oi.product_id,oi.sn,oi.image,oi.name, oi.price from es_order_items oi "
                + "left join es_order o on  o.order_id =oi.order_id where o.member_id=?";
        
        return this.daoSupport.queryForPage(sql, pageNo, pageSize, member_id);
    }
    
    public void setPPosition(Goods goods, int pPosition, String value) {
        switch(pPosition) {
            case 1:
                goods.setP1(value);
                break;
            case 2:
                goods.setP2(value);
                break;
            case 3:
                goods.setP3(value);
                break;
            case 4:
                goods.setP4(value);
                break;
            case 5:
                goods.setP5(value);
                break;
            case 6:
                goods.setP6(value);
                break;
            case 7:
                goods.setP7(value);
                break;
            case 8:
                goods.setP8(value);
                break;
            case 9:
                goods.setP9(value);
                break;
            default:
                goods.setP10(value);
                break;
        }
    }


    @Override
    public int getId(String sn, Integer storeId) {
        String sql = "select (*)  from es_goods where sn = ? and store_id =？  and disable = 0";
        String newSql = "select goods_id  from es_goods where sn = ? and store_id =？and disable = 0 ";
        int result = this.daoSupport.queryForInt(sql,sn,storeId);
        int goodsId = 0;
        if(result == 0){
            goodsId = 0;
        }else if (result > 1){
            goodsId = -1;
        }else{
            goodsId = this.daoSupport.queryForInt(newSql, sn,storeId);

        }
        return goodsId;
    }
    
    //根据商品id查询上架商品总数       add by Tension
    public int getCountByStore(int storeId) {
        String sql = "select count(*) from es_goods where store_id="+ storeId +" and market_enable=1 and disabled=0";
        return this.baseDaoSupport.queryForInt(sql);
    }
    
    //获取众筹商品
    public List<Map> getCFGoods() {
        String sql = "select goods_id, name from es_goods where goods_type='cf'";
        return this.baseDaoSupport.queryForList(sql);
    }
    
    public IStoreLogManager getStoreLogManager() {
        return storeLogManager;
    }
    
    public void setStoreLogManager(IStoreLogManager storeLogManager) {
        this.storeLogManager = storeLogManager;
    }
    
    public IProductManager getProductManager() {
        return productManager;
    }
    
    public void setProductManager(IProductManager productManager) {
        this.productManager = productManager;
    }

    public void setWareOpenApiManager(IWareOpenApiManager wareOpenApiManager) {
        this.wareOpenApiManager = wareOpenApiManager;
    }
    
    public void setGoodsGalleryManager(IGoodsGalleryManager goodsGalleryManager) {
        this.goodsGalleryManager = goodsGalleryManager;
    }

    public void setGoodsTypeManager(IGoodsTypeManager goodsTypeManager) {
        this.goodsTypeManager = goodsTypeManager;
    }
    
    public void setGfsManager(IGFSManager gfsManager) {
        this.gfsManager = gfsManager;
    }

    public List<Map<String, Object>> searchGoodsForActivity(Map<String, Object> returnMap) {
       Integer storeId = (Integer) returnMap.get("storeId");
       String goods_name= (String) returnMap.get("goods_name");
       String sn = (String) returnMap.get("sn");
       Integer activityId = (Integer) returnMap.get("activityId");
       String isAdd = (String)returnMap.get("isAdd");
       String sql = "select g.* from es_goods g left join es_activity_goods ag on ag.goods_id=g.goods_id where 1=1 ";
       if (storeId != null && !storeId.equals("")) {
          
           sql += " and g.store_id=" + storeId;
       }
       
       if (goods_name != null && !goods_name.isEmpty()){
           
           sql += " and g.name like '%"+goods_name+"%' ";
       }
        if(sn != null && !sn.isEmpty()){
            sql += " and g.sn like '%"+sn+"%' ";
        }
        if (activityId != null) {
         
            sql+= " and ag.activity_id=" + activityId;
        }
        if(isAdd != null && !isAdd.equals("")){
            sql += " and g.enable_store <> 0  ";
        }
        sql +="  and g.market_enable=1  order by g.goods_id desc";
        List<Map<String, Object>> list = this.baseDaoSupport.queryForList(sql);
        return list;
    }


    public List<Map<String, Object>> searchGoodsForGift(Map<String, Object> returnMap) {
        Integer storeId = (Integer) returnMap.get("storeId");
        String goods_name= (String) returnMap.get("goods_name");
        String sn = (String) returnMap.get("sn");
        Integer activityId = (Integer) returnMap.get("activityId");
        String cond = "";
        
        if (storeId != null) {
            cond += "g.store_id=" + storeId;
        }
        
        if (activityId != null) {
            if (cond.length() > 0) {
                cond += " and ";
            }
            cond += "ag.activity_id=" + activityId;
        }
        
        if (cond.isEmpty()) {
            cond = "1=1";
        }
        if(goods_name != null && !goods_name.isEmpty()){
            cond += " g.name like '%"+goods_name+"%' ";
        }
        if(sn != null && !sn.isEmpty()){
            cond += " g.sn like '%"+sn+"%' " ;
        }
        String sql = "select g.* from es_goods g left join es_activity_gift ag on ag.goods_id=g.goods_id where "+ cond +" order by g.goods_id desc";
        List<Map<String, Object>> list = this.baseDaoSupport.queryForList(sql);
        
        return list;
    }

    @Override
    public void updateGoodsField(int goodsId, String fieldValue, String fieldName) {
        StringBuilder build = new StringBuilder("update es_goods set ");
        build.append(fieldName).append("=? where goods_id=?");
        this.baseDaoSupport.execute(build.toString(), fieldValue, goodsId);
    }

    @Override
    public void updateGoodsField(Map<Integer, String> fieldValueMap, String fieldName) {
        if (fieldValueMap.isEmpty()) return;
        StringBuilder build = new StringBuilder("update es_goods set ");
        build.append(fieldName).append("=? where goods_id=?");
        List<Object[]> batchArgs = new ArrayList<Object[]>(fieldValueMap.size());
        for (Map.Entry<Integer, String> entry : fieldValueMap.entrySet()) {
            batchArgs.add(new Object[] { entry.getValue(), entry.getKey() });
        }
        this.baseDaoSupport.batchExecute(build.toString(), batchArgs);
    }
}