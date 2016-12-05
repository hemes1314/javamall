package com.enation.app.shop.core.service;

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.shop.core.model.DepotStore;
import com.enation.app.shop.core.model.WarnNum;
import com.enation.framework.database.Page;


/**
 * 商品库存管理
 * @author kingapex
 *
 */
public interface IGoodsStoreManager {
	
	

	/**
	 * 获取某个货品各个库房的库存
	 */
	public List<Map> listProductStore(Integer productid);
	
	
	/**
	 * 读取某货品某个库房的列表
	 * @param productid
	 * @return
	 */
	public List<Map> ListProductDepotStore(Integer productid,Integer depotid);
	
	
	
	/**
	 * 攻取某个货品的仓库配货情况
	 * @param itemid 订单货物项
	 * @return
	 */
	public List<Map> listProductAllo(Integer orderid,Integer itemid);
	
	
	/**
	 * 获取某个商品库存维护的html
	 * @param goodsid
	 * @return
	 */
	public String getStoreHtml(Integer goodsid);
	
	/**
	 * 获取某个商品报警设置维护的html
	 * @param goodsid
	 * @return
	 */
	public String getWarnHtml(Integer goodsid);
	
	/**
	 * 保存报警
	 * @param goodsid
	 */
	public void saveWarn(int goodsid) ;
	/**
	 * 获取某个商品进货的html
	 * @param goodsid
	 * @return
	 */
	@Transactional(propagation = Propagation.REQUIRED)  
	public String getStockHtml(Integer goodsid);
	 
	
	/**
	 * 获取某个商品库出货的html
	 * @param goodsid
	 * @return
	 */
	public String getShipHtml(Integer goodsid);
	
	
	
	/**
	 * 保存商品库存
	 * @param goods
	 * @return
	 */
	public void saveStore(int goodsid);
	
	
	
	/**
	 * 进货
	 * @param goodsid
	 */
	public void saveStock(int goodsid);
	
	
	
	
	/**
	 * 出货
	 * @param goodsid 商品Id
	 */
	public void saveShip(int goodsid);
	
	/**
	 * 获取某个货品某个库房的库存
	 * @param productid 货品Id
	 * @param depotId 仓库Id
	 * @return Integer
	 */
	public Integer getbStoreByProId(Integer productid,Integer depotId);
	
	/**
	 * 商品某个库房的库存列表
	 * @param goodsid 商品Id
	 * @param depotid 库房Id
	 * @return List<Map>
	 */
	public List<Map> getDegreeDepotStore(int goodsid, int depotid);
	

	public List<WarnNum> listWarns(Integer goods_id) ;
	
	
	public Page listGoodsStore(Map map,int page,int pageSize,String other,String sort,String order);

	/**
	 * 退货入库
	 * @param goodsid
	 * @param num
	 * @param depotid
	 * @param isInner
	 */
	public void increaseStroe(int goodsid, int productid, int depotid,int num);
	
	
	public List getStoreList();
}
