package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.model.store.Store;
import com.enation.app.shop.core.model.Goods;
import com.enation.app.shop.core.model.GoodsStores;
import com.enation.app.shop.core.model.support.GoodsEditDTO;
import com.enation.framework.database.Page;

/**
 * 商品管理接口
 * @author kingapex
 *
 */
public interface IGoodsManager {

	public static final String plugin_type_berforeadd= "goods_add_berforeadd" ;
	public static final String plugin_type_afteradd= "goods_add_afteradd" ;
 
	/**
	 * 读取一个商品的详细
	 * @param Goods_id
	 * @return Map
	 */
	public Map get(Integer goods_id);
	public Map getByCache(Integer goods_id);
	/**
	 * 根据商品ID获取商品
	 * @param goods_id 商品Id
	 * @return Goods
	 */
	public Goods getGoods(Integer goods_id);
	
	/**
	 * 修改时获取数据
	 * @param goods_id
	 * @return
	 */
	public GoodsEditDTO getGoodsEditData(Integer goods_id);
	
	/**
	 * 添加商品
	 * @param goods
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void add(Goods goods);
	
	/**
	 * 修改商品
	 * @param goods
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void edit(Goods goods);

	/**
	 * 商品搜索
	 * @param goodsMap 搜索参数
	 * @param page 分页
	 * @param pageSize 分页每页数量
	 * @param other 其他
	 * @return Page
	 */
	public Page searchGoods(Map goodsMap,int page,int pageSize,String other,String sort,String order);
	/**
	 * 获取商品列表
	 * @param goodsMap 搜索参数
	 * @return List
	 */
	public List searchGoods(Map goodsMap);
	/**
	 * 后台搜索商品
	 * @param name 商品名称
	 * @param sn 商品编号
	 * @param order 排序
	 * @param page 分页
	 * @param pageSize 每页数量 
	 * @return Page
	 */
	public Page searchBindGoods(String name,String sn,String order,int page,int pageSize);
	
	/**
	 * 读取商品回收站列表
	 * @param name 商品名称
	 * @param sn 商品编号
	 * @param order 排序
	 * @param order 
	 * @param page 分页
	 * @param pageSize 每页数量
	 * @return Page
	 */
	public Page pageTrash(String name,String sn,String sort,String order, int page,int pageSize);
	
	/**
	 * 库存余量提醒分页列表
	 * @param warnTotal
	 * @param page 分页
	 * @param pageSize 每页数量
	 * @return List<GoodsStores>
	 */
	public List<GoodsStores> storeWarnGoods(int warnTotal,int page,int pageSize);//库存余量提醒分页列表
	
	/**
	 * 将商品加入回收站
	 * @param ids 商品Id数组
	 */
	public void delete(Integer[] ids);
	/**
	 * 商品还原
	 * @param ids 商品Id数组
	 */
	public void  revert(Integer[] ids);
	/**
	 * 清除商品
	 * @param ids 商品Id数组
	 */
	public void clean(Integer[] ids);
	
	/**
	 * 根据商品id数组读取商品列表
	 * @param ids
	 * @return
	 */
	public List list(Integer[] ids);
	
	/**
	 * 按分类id列表商品
	 * @param catid
	 * @return
	 */
	public List listByCat(Integer catid);
	
	/**
	 * 按标签id列表商品
	 * 如果tagid为空则列表全部
	 * @param tagid
	 * @return
	 */
	public List listByTag(Integer[] tagid);
	
	/**
	 * 不分页、不分类别读取所有有效商品，包含捆绑商品
	 * @return
	 */
	public List<Map> list();
	
	/**
	 * 批量编辑商品
	 */
	public void batchEdit();
	
	/**
	 * 商品信息统计
	 * @return
	 */
	public Map census();

	/**
	 * 获取某个商品的位置信息
	 */
	public void getNavdata(Map goods);
	
	/**
	 * 更新某个商品的字段值
	 * @param filedname 字段名称
	 * @param value 字段值
	 * @param goodsid 商品id
	 */
	public void updateField(String filedname,Object value,Integer goodsid);
	
	/**
	 * 获取某个商品的推荐组合
	 * @param goods_id
	 * @param cat_id
	 * @param brand_id
	 * @param num
	 * @return
	 */
	public List getRecommentList(int goods_id, int cat_id, int brand_id, int num);
	
	/**
	 * 根据货物编号得到某个商品
	 * @param goodSn 货物编号
	 * @return
	 */
	public Goods getGoodBySn(String goodSn);
	
	/**
	 * 根据货物编号得到某个商品
	 * @param goodSn 货物编号
	 * @param goodsId 商品编号
	 * @return
	 */
	public Goods getGoodBySn(String goodSn, Integer goodsId);
	
	/**
	 * 修改商品访问次数
	 * @param goods_id
	 */
	public void incViewCount(Integer goods_id);

	/**
	 * 商品列表
	 * @param catid 分类Id
	 * @param tagid 标签Id
	 * @param goodsnum 数量
	 * @return List
	 */
	public List listGoods(String catid,String tagid,String goodsnum);
	
	/**
	 * 购买过商品的会员
	 * @param goods_id 商品Id
	 * @param pageSize 显示数量
	 * @return List
	 */
	public List goodsBuyer(int goods_id, int pageSize);
	
	/**
	 * 根据分类查询所有商品信息  
	 * @author Sylow
	 * @param catid
	 * @param goodsnum
	 * @return
	 */
	public List listByCat(String tagid, String catid, String goodsnum);
	
	/**
	 *  搜索商品
	 * @param goodsMap	查询条件
	 * @param sort	排序字段
	 * @param order	正倒叙关键字
	 * @return
	 */
	public List searchGoods(Map goodsMap, String sort, String order);
	/**
	 *  搜索商品forApp
	 * @param goodsMap	查询条件
	 * @param sort	排序字段
	 * @param order	正倒叙关键字
	 * @return
	 */
	public Page searchGoodsForApp(Map goodsMap, String sort, String order,int page ,int pageSize);

	/**
	 * 商品变化
	 * 当商品变化时触发，如果商品生成静态页面，开启优惠对其进行更改。
	 * @author Kanon
	 * @param goods 商品
	 */
	public void startChange(Map goods);
	/**
	 * 
	 * 记录历史记录
	 */
	public void insertHistory(Integer member_id,String session_id);
	/**
	 * 将erp 的数据插入到程序
	 * @param goods
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addGoods(Goods goods);
	/**
	 * 从erp 更新库存
	 * @param sn
	 * @param store
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updateStore(Integer goodsid, Integer num ,Integer storeId);
	/**
	 * 更新价格
	 * @param sn
	 * @param store
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void updatePrice(String sn, Double price);
	/**
	 * 和商品添加一起 加载店铺信息
	 * @param parameter
	 * @param store
	 */
	@Transactional(propagation = Propagation.REQUIRED)
	public void addStore_id(String sn, Store store);
	/**
	 *查询购买过的记录
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
    public Page searchHistory(int pageNo, int pageSize, Integer member_id);
    
    public void setPPosition(Goods goods, int typePosition, String valueOf);
    /**
     * 根据 商品编号 和店铺名查询商品ID
     * @return
     */
    public int getId(String sn , Integer storeId);
    
    //根据商品id查询上架商品总数       add by Tension
    public int getCountByStore(int store_id);
    
    //返回添加商品的ID       add by Tension
    public int erpAddGoods(Goods goods);
    
    /**
     * 更新商品指定字段值.
     * 
     * @param goodsId 商品ID
     * @param fieldValue 字段值
     * @param fieldName 更新字段名
     * @author baoxiufeng
     */
    public void updateGoodsField(int goodsId, String fieldValue, String fieldName);
    
    /**
     * 批量更新商品指定字段值.
     * 
     * @param fieldValueMap 待更新的商品字段值集合
     * @param fieldName 更新字段名
     * @author baoxiufeng
     */
    public void updateGoodsField(Map<Integer, String> fieldValueMap, String fieldName);
}