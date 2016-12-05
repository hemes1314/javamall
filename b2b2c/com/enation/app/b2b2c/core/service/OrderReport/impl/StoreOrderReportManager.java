package com.enation.app.b2b2c.core.service.OrderReport.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.enation.app.b2b2c.core.service.OrderReport.IStoreOrderReportManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;

@SuppressWarnings({ "unchecked", "rawtypes" })
@Component
public class StoreOrderReportManager implements IStoreOrderReportManager{
	private IDaoSupport daoSupport;
	private ISellBackManager sellBackManager;
	private IOrderManager orderManager;
	private IGoodsStoreManager goodsStoreManager;
	private IMemberManager memberManager;
	
	@Override
	public void saveAuth(Integer status, Integer id, String seller_remark,String return_address) {
	    String sql = "";
	    if(status == 4){
	        sql = "update es_sellback_list set tradestatus =4,seller_remark=? ,return_address = ? where id=?";
	    }else{
	        // sql = "update es_sellback_list set tythstatus =1,seller_remark=? ,return_address = ? where id=?";
	        sql = "update es_sellback_list set tradestatus =2,seller_remark=? ,return_address = ? where id=?";
	    }
		this.daoSupport.execute(sql,seller_remark,return_address,id);
		SellBackList sellBackList=sellBackManager.get(id);
		Order order = this.orderManager.get(sellBackList.getOrdersn());
		Integer orderid = order.getOrder_id();
		if(status==2){
			daoSupport.execute("update es_order set status=? where order_id=?",OrderStatus.ORDER_RETURNED, orderid);
			this.update(id);
			
		}else{
			daoSupport.execute("update es_order set status=? where order_id=?",OrderStatus.ORDER_RETURN_REFUSE, orderid);
		}
		//
		
	}
	
	@Override
    public void saveAuthtg(Integer status, Integer id, String seller_remark,String return_address) {
        String sql = "update es_sellback_list set tradestatus=?,seller_remark=? ,return_address = ? ,return_time=?  where id=?";
        this.daoSupport.execute(sql,status,seller_remark,return_address,DateUtil.getDateline(),id);
        SellBackList sellBackList=sellBackManager.get(id);
			Order order = this.orderManager.get(sellBackList.getOrdersn());
			Integer orderid = order.getOrder_id();
        if(status==2){
            daoSupport.execute("update es_order set status=? where order_id=?",OrderStatus.ORDER_RETURNED,orderid);
            this.update(id);
            
        }else{
            daoSupport.execute("update es_order set status=? where order_id=?",OrderStatus.ORDER_RETURN_REFUSE,orderid);
        }
    }
	
	@Override
    public void savezydAuthtg(Integer status, Integer id, String seller_remark,String return_address) {
        String sql = "update es_sellback_list set tradestatus=?,seller_remark=? ,return_address = ? ,return_time=?  where id=?";
        this.daoSupport.execute(sql,status,seller_remark,return_address,DateUtil.getDateline(),id);
    }
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void tksaveAuthtg(Integer status, Integer id, String seller_remark, String return_address) {
		String sql = "update es_sellback_list set tradestatus=?, seller_remark=?, return_address=?,return_time=? where id=?";
		this.daoSupport.execute(sql, status, seller_remark, return_address, DateUtil.getDateline(),id);
		SellBackList sellBackList = sellBackManager.get(id);
		String logDetail = null;
		switch (status) {
		case 2:
			daoSupport.execute("update es_order set status=? where order_id=?", OrderStatus.YTK_STATUS, sellBackList.getOrderid());
			this.update(id);
			logDetail = "您的退款申请已通过审核，待财务确认中";
			break;
		case 4:
			daoSupport.execute("update es_order set status=? where order_id=?", OrderStatus.TKBJ_STATUS, sellBackList.getOrderid());
			logDetail = "很抱歉，您的退款申请未通过审核";
			if (StringUtils.isNotBlank(seller_remark)) {
				logDetail += "（拒绝理由：" + seller_remark + "）";
			}
			break;
		}
		// 记录退款审核日志
		String operName = null;
		Member m = UserConext.getCurrentMember();
		if (m != null) {
			operName = m.getUname();
		} else if (UserConext.getCurrentAdminUser() != null) {
			operName = UserConext.getCurrentAdminUser().getUsername();
		} else {
			operName = "admin";
		}
		sellBackManager.saveLog(id, null, logDetail, operName);
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@Override
	public void jssaveAuthtg(Integer status, Integer id, String seller_remark, String return_address) {
		String sql = "update es_sellback_list set tradestatus=?,seller_remark=?,return_address=?,return_time=? where id=?";
		this.daoSupport.execute(sql, status, seller_remark, return_address,DateUtil.getDateline(), id);
		SellBackList sellBackList = sellBackManager.get(id);
		String logDetail = null;
		switch (status) {
        case 2:
        	daoSupport.execute("update es_order set status=? where order_id=?", OrderStatus.YJS_STATUS, sellBackList.getOrderid());
			this.jsupdate(id);
			logDetail = "您的退款申请已通过审核，待财务确认中";
        	break;
        case 4:
        	daoSupport.execute("update es_order set status=? where order_id=?", OrderStatus.JSBJ_STATUS, sellBackList.getOrderid());
        	logDetail = "很抱歉，您的退款申请未通过审核";
        	if (StringUtils.isNotBlank(seller_remark)) {
				logDetail += "（拒绝理由：" + seller_remark + "）";
			}
        	break;
        }
		// 记录用户拒收日志
		String operName = null;
		Member m = UserConext.getCurrentMember();
		if (m != null) {
			operName = m.getUname();
		} else if (UserConext.getCurrentAdminUser() != null) {
			operName = UserConext.getCurrentAdminUser().getUsername();
		} else {
			operName = "admin";
		}
		sellBackManager.saveLog(id, null, logDetail, operName);
	}
   
	@Override
    public void saveKdgs(Integer id,String kddh, String wlgs) {
        String sql = "update es_sellback_list set kddh=? ,wlgs = ? where id=?";
        this.daoSupport.execute(sql,kddh,wlgs,id);
    }
	
	public void update(Integer id) {
		SellBackList sellBackList = sellBackManager.get(id);
		Integer orderId = sellBackList.getOrderid();
		List<Map> goodsList = this.sellBackManager.getGoodsList(id);
		for (Map map : goodsList) {
			Integer goods_id = (Integer) map.get("goods_id");
			Integer product_id = (Integer) map.get("product_id");
			// 如果是整箱商品（2016-10-13-baoxiufeng备注：此处isPack不能为1，否则进入后会查询不存在的数据库表）
			if (this.sellBackManager.isPack(product_id) == 1) {
				List<Map> list = this.sellBackManager.getSellbackChilds(orderId, goods_id);
				if (list != null) {
					for (Map mapTemp : list) {
						Integer childGoodsId= (Integer) mapTemp.get("goods_id");
						Integer childProductId= (Integer) mapTemp.get("product_id");
						Integer childNum = (Integer) mapTemp.get("return_num");
						this.sellBackManager.editChildStorageNum(orderId, goods_id, childGoodsId, childNum);
						goodsStoreManager.increaseStroe(childGoodsId, childProductId, 1, childNum);
						
					}
				}
			} else {
				//TODO MOSOON 4-13修改不一定对 需要测试一下 保留此todo好查询到
				//Integer num=NumberUtils.toInt(map.get("return_num").toString());
//				this.sellBackManager.editStorageNum(id,goods_id,num);//修改入库数量
//				goodsStoreManager.increaseStroe(goods_id,product_id,1,num);
			}
		}
	}


	public void jsupdate(Integer id){
		SellBackList sellBackList= sellBackManager.get(id);
		List<Map> goodsList = this.sellBackManager.getGoodsList(id);
		for (Map map : goodsList) {
			Integer goods_id = NumberUtils.toInt(map.get("goods_id").toString());
			Integer product_id = NumberUtils.toInt(map.get("product_id").toString());

			if(this.sellBackManager.isPack(product_id) == 1){

				Integer orderid = this.orderManager.get(sellBackList.getOrdersn()).getOrder_id();
				List<Map> list = this.sellBackManager.getSellbackChilds(orderid,goods_id);
				if (list != null) {
					for(Map mapTemp : list){
						Integer childGoodsId = NumberUtils.toInt(mapTemp.get("goods_id").toString());
						Integer childProductId = NumberUtils.toInt(mapTemp.get("product_id").toString());
						Integer childNum = NumberUtils.toInt(mapTemp.get("return_num").toString());
						this.sellBackManager.editChildStorageNum(orderid, goods_id, childGoodsId, childNum);
						goodsStoreManager.increaseStroe(childGoodsId,childProductId,1,childNum);

					}
				}
			}else{
				Integer num = NumberUtils.toInt(map.get("return_num").toString());


				this.sellBackManager.editStorageNum(id,goods_id,num);//修改入库数量
				goodsStoreManager.increaseStroe(goods_id,product_id,1,num);
			}


		}
	}



	public ISellBackManager getSellBackManager() {
		return sellBackManager;
	}

	public void setSellBackManager(ISellBackManager sellBackManager) {
		this.sellBackManager = sellBackManager;
	}

	public IDaoSupport getDaoSupport() {
		return daoSupport;
	}

	public void setDaoSupport(IDaoSupport daoSupport) {
		this.daoSupport = daoSupport;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IGoodsStoreManager getGoodsStoreManager() {
		return goodsStoreManager;
	}

	public void setGoodsStoreManager(IGoodsStoreManager goodsStoreManager) {
		this.goodsStoreManager = goodsStoreManager;
	}

    public IMemberManager getMemberManager() {
        return memberManager;
    }

    public void setMemberManager(IMemberManager memberManager) {
        this.memberManager = memberManager;
    }

}
