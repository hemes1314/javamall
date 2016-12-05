package com.enation.app.shop.core.action.api;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.auth.IAdminUserManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.SellBackChild;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 *  退换货Api
 * @author fenlongli
 */
@SuppressWarnings({ "rawtypes", "serial" })
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("sellBack")
@Component
public class SellBackApiAction extends WWAction {
	private IOrderManager orderManager;
	private ILogiManager logiManager;
	private IAdminUserManager adminUserManager;
	private ISellBackManager sellBackManager;
	private IDaoSupport daoSupport;
	
	private String remark;
	private Integer[] goodsId;
	private Integer[] gid;
	private Integer[] goodsNum;
	private Integer[] payNum;
	private Integer[] productId;
	private Double[] price;
	private Integer orderId;
	
	private String return_account; //退款账户
	private String refund_way;	//退款方式
	/**
	 * 保存退货申请
	 * @param orderId 订单ID
	 * @param goodsId 商品ID
	 * @param gid 选择退货的商品ID
	 * @param goodsNum 退货数量
	 * @param payNum 购买数量
	 * @param sid 退货单ID
	 * @param remark 退货原因
	 * @param productId 货品ID
	 * @param price 商品单价
	 * @return
	 */
	public String save(){
		try {
			//创建退货单
			SellBackList sellBackList=new SellBackList();
			//记录会员信息
			Member member =  UserConext.getCurrentMember();
			sellBackList.setMember_id(member.getMember_id());
			sellBackList.setSndto(member.getName());
			//订单信息
			Order order = orderManager.get(orderId);
			if(order.getShipping_area()!=null || !StringUtil.isEmpty(order.getShipping_area())  || order.getShipping_area().trim().equals("暂空")){
				sellBackList.setAdr(order.getShip_addr());
			}else{
				String adr[]=order.getShipping_area().split("-"); 
				sellBackList.setAdr(adr[0] + adr[1] + adr[2] + order.getShip_addr());
			}
			sellBackList.setTradeno(com.enation.framework.util.DateUtil.toString(DateUtil.getDateline(),"yyMMddhhmmss"));//退货单号
			sellBackList.setOrdersn(order.getSn());
			sellBackList.setRegoperator("会员");
			sellBackList.setTel(order.getShip_tel());
			sellBackList.setZip(order.getShip_zip());
			sellBackList.setTradestatus(0);
			sellBackList.setRegtime(DateUtil.getDateline());
			sellBackList.setDepotid(order.getDepotid());
			sellBackList.setRemark(remark);
			sellBackList.setRefund_way(refund_way);
			sellBackList.setReturn_account(return_account);
			Integer sid = this.sellBackManager.save(sellBackList, true);
			//创建退货单结束 创建退货单商品
			/*for(int i=0;i<goodsId.length;i++){
				for(int j=0;j<gid.length;j++){
					if(goodsId[i].intValue()==gid[j].intValue()){
						this.saveGoodsList(goodsId[i],goodsNum[j],payNum[j],sid,remark,productId[i],price[i]);
						this.daoSupport.execute("update es_order_items set state=1 where order_id=? and product_id=?", this.orderId,productId[i]); //将订单货品更新为申请退货
					}
				}
			}*/
			
			/**
			 * 关于整箱退货代码修改   Start
			 * 冯兴隆 2015-07-14
			 */
			HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
			String packDetailJson = request.getParameter("packDetailJson");
			List<Map<String,Object>> list = JsonUtil.toList(packDetailJson);
			//创建退货单结束 创建退货单商品
			for(int i=0;i<goodsId.length;i++){
				for(int j=0;j<gid.length;j++){
					if(goodsId[i].intValue()==gid[j].intValue()){
						this.saveGoodsList(goodsId[i],goodsNum[j],payNum[j],sid,remark,productId[i],price[i]);
						this.daoSupport.execute("update es_order_items set state=1 where order_id=? and product_id=?", this.orderId,productId[i]); //将订单货品更新为申请退货
						int nowGoodsId = goodsId[i];
						
						String isPack = request.getParameter("isPack_" + nowGoodsId);
						//如果该商品是一个整箱  则把箱内的商品 提取出来
						if("1".equals(isPack)){
							String return_type = request.getParameter("return_type_" + nowGoodsId);		//是否是全部退货
							if("1".equals(return_type)){
								//
								for(Map<String,Object> map : list){
									int packGoodsId = NumberUtils.toInt(map.get("packGoodsId").toString());
									//如果该条数据是该整箱中的商品
									if(nowGoodsId == packGoodsId){
										int tempGoodsId = NumberUtils.toInt(map.get("goodsId").toString());
										int tempNum = NumberUtils.toInt(map.get("num").toString());
										this.saveOrUpdateSellbackChild(orderId, tempGoodsId, nowGoodsId, tempNum);
									}
								}
								
							}else{
								List<Map> listGoods = sellBackManager.list(nowGoodsId);
								for(Map map : listGoods){
									int tempGoodsId = NumberUtils.toInt(map.get("rel_goods_id").toString());
									Map childGoodsInfo = this.sellBackManager.getPackInfo(nowGoodsId, tempGoodsId);
									int pkgNum = NumberUtils.toInt(childGoodsInfo.get("pkgnum").toString());
									int tempNum = goodsNum[j] * pkgNum;
									
									this.saveOrUpdateSellbackChild(orderId, tempGoodsId, nowGoodsId, tempNum);
								}
							}
							
						}
					}
				}
			}
			this.showSuccessJson("退货单申请成功，请等待审核");
		} catch (Exception e) {
			this.showErrorJson("退货单申请失败");
			this.logger.error("退货单申请失败：",e);
		}
		return JSON_MESSAGE;
	}
	
	/**
	 * 新增或修改 整箱内的退货子项详情
	 * @param orderId
	 * @param goodsId
	 * @param parentId
	 * @param returnNum
	 * @param storageNum
	 */
	private void saveOrUpdateSellbackChild(int orderId,int goodsId,int parentId,int returnNum){
		SellBackChild sellBackChild = this.sellBackManager.getSellbackChild(orderId, goodsId);
		if(sellBackChild != null){
			this.sellBackManager.updateSellbackChild(orderId, goodsId, returnNum,0);
		}else{
			this.sellBackManager.saveSellbackChild(orderId, goodsId, parentId, returnNum);
		}
	}
	/**
	 * 保存退货商品
	 * @param goodsid 商品Id
	 * @param goodsnum 退货的数量
	 * @param paynum 购买数量
	 * @param id 退货单Id
	 * @param remark 备注
	 * @param productid 货品Id
	 * @param price 价格
	 * @return 退货商品ID
	 */
	private Integer saveGoodsList(Integer goodsid,Integer goodsnum,Integer paynum,Integer id,String remark,Integer productid,Double price){
		SellBackGoodsList sellBackGoods = new SellBackGoodsList();
	 
		sellBackGoods.setRecid(id);
		sellBackGoods.setPrice(price);
		sellBackGoods.setReturn_num(goodsnum);
		sellBackGoods.setShip_num(paynum);
		sellBackGoods.setGoods_id(goodsid);	
		sellBackGoods.setGoods_remark(remark);
		sellBackGoods.setProduct_id(productid);
		HttpServletRequest  request = ThreadContextHolder.getHttpRequest();
		String isPack = request.getParameter("isPack_" + goodsid);
		try{
			//如果商品为全部退货或者为单只产品
			if(isPack.equals("1")){
				String return_type = request.getParameter("return_type_" + goodsid);		//是否是全部退货
				if("1".equals(return_type)){
					sellBackGoods.setReturn_type(1);
				}else{
					sellBackGoods.setReturn_type(0);
				}
			}else{
				sellBackGoods.setReturn_type(0);
			}
		}catch(RuntimeException e){
			e.printStackTrace();
		}
		Integer sid = this.sellBackManager.saveGoodsList(sellBackGoods);
		return sid;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public ILogiManager getLogiManager() {
		return logiManager;
	}

	public void setLogiManager(ILogiManager logiManager) {
		this.logiManager = logiManager;
	}

	public IAdminUserManager getAdminUserManager() {
		return adminUserManager;
	}

	public void setAdminUserManager(IAdminUserManager adminUserManager) {
		this.adminUserManager = adminUserManager;
	}

	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}

	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public Integer[] getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Integer[] goodsId) {
		this.goodsId = goodsId;
	}

	public Integer[] getGid() {
		return gid;
	}

	public void setGid(Integer[] gid) {
		this.gid = gid;
	}

	public Integer[] getGoodsNum() {
		return goodsNum;
	}

	public void setGoodsNum(Integer[] goodsNum) {
		this.goodsNum = goodsNum;
	}

	public Integer[] getPayNum() {
		return payNum;
	}

	public void setPayNum(Integer[] payNum) {
		this.payNum = payNum;
	}

	public Integer[] getProductId() {
		return productId;
	}

	public void setProductId(Integer[] productId) {
		this.productId = productId;
	}

	public Double[] getPrice() {
		return price;
	}

	public void setPrice(Double[] price) {
		this.price = price;
	}

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	public String getReturn_account() {
		return return_account;
	}

	public void setReturn_account(String return_account) {
		this.return_account = return_account;
	}

	public String getRefund_way() {
		return refund_way;
	}

	public void setRefund_way(String refund_way) {
		this.refund_way = refund_way;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}
	
	
}
