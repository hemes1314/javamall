package com.enation.app.b2b2c.core.action.api.order;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.b2b2c.core.model.order.StoreSellBackList;
import com.enation.app.b2b2c.core.service.OrderReport.IStoreOrderReportManager;
import com.enation.app.b2b2c.core.service.member.IStoreMemberManager;
import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.model.Member;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderItem;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.mobile.service.impl.ErpManager;
import com.enation.app.shop.mobile.util.NumberUtils;
import com.enation.app.shop.mobile.util.OrderUtils;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonMessageUtil;
import com.gomecellar.workflow.service.IAppealFlowManager;

/**
 * 店铺退货申请API
 * @author LiFenlong
 *
 */
@SuppressWarnings({ "serial" })
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/store")
@Action("storeSellBack")
public class StoreSellBackApiAction extends WWAction {

    private Integer id;
    private Integer status;
    private Integer orderId;
    private Integer workflow; // 2016/1/29 humaodong 

    private Integer[] goodsId;
    private Integer[] gid;
    private Integer[] goodsNum;
    private Integer[] payNum;
    private Integer[] productId;
    private Double[] price;


    private String remark;
    private String refund_way;
    private String return_account;
    private String seller_remark;
    private Double return_price;
    private String return_address;
    private String kddh;
    private String wlgs;
    private ISellBackManager sellBackManager;
    private IStoreOrderManager storeOrderManager;
    private IStoreMemberManager storeMemberManager;
    private IStoreOrderReportManager storeOrderReportManager;
    //工作流
    private File[] image;
    private String[] imageFileName;
    private IAppealFlowManager appealFlowManager;


    private File imagea;
    private String imageaFileName;
    private File imageb;
    private String imagebFileName;
    private File imagec;
    private String imagecFileName;
    private IOrderManager orderManager;
    private ErpManager erpManager;

    public IOrderManager getOrderManager() {
        return orderManager;
    }
    public void setOrderManager(IOrderManager orderManager) {
        this.orderManager = orderManager;
    }
    public ErpManager getErpManager() {
        return erpManager;
    }
    public void setErpManager(ErpManager erpManager) {
        this.erpManager = erpManager;
    }

    /**
     * 用户退款或后台拒收处理.
     * 退款操作状态：status = 13
     * 拒收操作状态: status = 12
     * 
     * @return 处理结果
     */
    public String save(){
    	JSONObject result = new JSONObject();
		result.put("success", false);
		result.put("errCode", -9);
        try {
        	status = NumberUtils.toInt(status);
        	if (status != OrderStatus.SQTK_STATUS && status != OrderStatus.JS_STATUS) {
        		this.showPlainErrorJson("非法请求！");
                this.logger.info("无效的退款或拒收请求，status:" + status);
                jsonp(result);
                return JSON_MESSAGE;
        	}
            String uploadImage = null;
            boolean hasWorkflow = (workflow != null && workflow == 1);
            if (hasWorkflow && image != null) {
            	List<String> images = new ArrayList<String>(image.length);
                for (int i = 0; i < image.length; i ++){
					images.add(UploadUtil.upload(image[i], imageFileName[i], "sellback"));
                }
                uploadImage = StringUtils.join(images, ",");
            }
            // 创建退货单
			StoreSellBackList sellBackList = new StoreSellBackList();
			StoreOrder order = null;
            // 记录会员信息
            Member member = storeMemberManager.getStoreMember();
            if (member == null) {
            	result.put("errCode", -1);
            	this.json = JsonMessageUtil.expireSession();
            	jsonp(result);
                return JSON_MESSAGE;
            }
        	sellBackList.setMember_id(member.getMember_id());
            sellBackList.setSndto(member.getName());
            // 订单信息
            order = storeOrderManager.get(orderId);
            //2016-10-09 添加订单状态校验
            switch (status) {
            // 退款申请时（可退款条件：已确认支付 OR 退款被拒绝）
            case OrderStatus.SQTK_STATUS:
            	switch (order.getStatus()) {
            	case OrderStatus.ORDER_PAY_CONFIRM:	// 已确认支付
            	case OrderStatus.TKBJ_STATUS:       // 退款被拒绝
            		if (order.getIs_erp_process() == 1) {
            			this.showPlainErrorJson("商家已发货，不能申请退款！");
                        this.logger.info("商家已发货，不能申请退款，订单号:" +order.getSn());
                        jsonp(result);
                        return JSON_MESSAGE;
            		}
            		break;
            	default:
            		if (OrderStatus.SQTK_STATUS == order.getStatus()) {
            			this.showPlainErrorJson("订单已提交过退款申请！");
            		} else {
            			this.showPlainErrorJson("对不起，此订单不能申请退款！");
            		}
            		this.logger.info("退款单申请失败，订单状态错误status:" +order.getStatus());
                    jsonp(result);
                    return JSON_MESSAGE;
            	}
            	break;
            // 用户拒收时（可拒收条件：已发货 OR 拒收被拒绝）
            case OrderStatus.JS_STATUS:
            	switch (order.getStatus()) {
            	case OrderStatus.ORDER_SHIP:	// 已发货
            	case OrderStatus.JSBJ_STATUS:	// 拒收被拒绝
            		break;
            	default:
            		if (OrderStatus.JS_STATUS == order.getStatus()) {
            			this.showPlainErrorJson("订单已提交过退货申请！");
            		} else {
            			this.showPlainErrorJson("对不起，此订单不能申请退货！");
            		}
            		this.logger.info("退货单申请失败,订单状态错误,status:" +order.getStatus());
            		jsonp(result);
                    return JSON_MESSAGE;
            	}
            	break;
            }
            
        	sellBackList.setMember_id(order.getMember_id());
        	sellBackList.setSndto(member.getUname());			// 退货人
            sellBackList.setAdr(order.getShip_addr());
            sellBackList.setTradeno(OrderUtils.getTradeNo());	// 退货单号
            sellBackList.setOrderid(order.getOrder_id());
            sellBackList.setOrdersn(order.getSn());
            sellBackList.setRegoperator("会员");
            sellBackList.setTel(order.getShip_tel());
            sellBackList.setZip(order.getShip_zip());
            sellBackList.setTradestatus(0);
            sellBackList.setRegtime(DateUtil.getDateline());
            sellBackList.setDepotid(order.getDepotid());
            sellBackList.setRemark(remark);
            sellBackList.setRefund_way(order.getPaymentMethod());
            sellBackList.setReturn_account(return_account);
            sellBackList.setStore_id(order.getStore_id());
            // 退款金额
            if (return_price == null) {
            	sellBackList.setReturn_price(order.getOrder_amount());
            } else {
            	sellBackList.setReturn_price(return_price); 
            }
            // 第三方店铺直接在商家后台审核
            List<SellBackGoodsList> sellBackGoodsList = null;
            boolean selfStore = false;
            if (goodsId != null && goodsId.length > 0) {
	            String store_type = this.sellBackManager.getStoreIdbyGoods_id(goodsId[0]);
	            if (StringUtils.isBlank(store_type)) selfStore = true;
	            // 构建退货商品列表
	            sellBackGoodsList = new ArrayList<>(goodsId.length);
	            SellBackGoodsList sellBackGoods = null;
				for (int i = 0; i < goodsId.length; i++) {
					for (int j = 0; j < gid.length; j++) {
						if (goodsId[i].intValue() != gid[j].intValue()) continue;
						sellBackGoods = new SellBackGoodsList();
		            	sellBackGoods.setGoods_id(goodsId[i]);
		            	sellBackGoods.setProduct_id(productId[i]);
		            	sellBackGoods.setPrice(price[i]);
		            	sellBackGoods.setReturn_num(goodsNum[j]);
		            	sellBackGoods.setShip_num(payNum[j]);
		            	sellBackGoods.setGoods_remark(remark);
		            	sellBackGoodsList.add(sellBackGoods);
					}
	            }
            } else {
            	List<OrderItem> orderItems = orderManager.listGoodsItems(orderId);
            	sellBackGoodsList = new ArrayList<SellBackGoodsList>(orderItems.size());
            	SellBackGoodsList sellBackGoods = null;
            	for (OrderItem orderItem : orderItems) {
            		sellBackGoods = new SellBackGoodsList();
	            	sellBackGoods.setGoods_id(orderItem.getGoods_id());
	            	sellBackGoods.setProduct_id(orderItem.getProduct_id());
	            	sellBackGoods.setPrice(orderItem.getPrice());
	            	sellBackGoods.setReturn_num(orderItem.getNum());
	            	sellBackGoods.setShip_num(orderItem.getShip_num());
	            	sellBackGoods.setGoods_remark(remark);
	            	sellBackGoodsList.add(sellBackGoods);
            	}
            }
			// 2016-10-11-baoxiufeng-重构后新的处理方式.
			this.sellBackManager.backsave(uploadImage, sellBackList, sellBackGoodsList, hasWorkflow, selfStore, status);
            this.showPlainSuccessJson("退货单申请成功，请等待审核");
        } catch (Exception e) {
            this.showPlainErrorJson("退货单申请失败");
            e.printStackTrace();
            this.logger.error("退货单申请失败：",e);
        }
        jsonp(result);
        return JSON_MESSAGE;
    }

    public IAppealFlowManager getAppealFlowManager() {
        return appealFlowManager;
    }


    public void setAppealFlowManager(IAppealFlowManager appealFlowManager) {
        this.appealFlowManager = appealFlowManager;
    }

    /**
     * 审核退货申请
     * @param status 审核状态
     * @param id 退货单ID
     * @param seller_remark 审核备注
     * @return
     */
    public String saveAuth(){
        try {
            String store_type = this.sellBackManager.getStoreIdbyReturnList(id);
			if (StringUtils.isNotBlank(store_type) && "0".equals(store_type)) {
                storeOrderReportManager.savezydAuthtg(status, id, seller_remark,return_address);
                /**
                 * 如果是自营店，调用接口通知OMS 商家同意进行退货
                 */
                new Thread(){
                    @Override
                    public void run(){
                        SellBackList sellback = sellBackManager.get(id);
                        Order order = orderManager.get(sellback.getOrdersn());
                        erpManager.notifyOmsForRefund(order,sellback.getTradeno());
                    }
                }.start();
            }else{
                storeOrderReportManager.saveAuth(status, id, seller_remark,return_address);
            }
            this.showSuccessJson("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorJson("操作失败");
        }
        return JSON_MESSAGE;
    }

    /**
     * 通过并审核
     * @param status 审核状态
     * @param id 退货单ID
     * @param seller_remark 审核备注
     * @return
     */
    public String saveAuthtg(){
        try {
            storeOrderReportManager.saveAuthtg(status, id, seller_remark,return_address);
            this.showSuccessJson("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorJson("操作失败");
        }
        return JSON_MESSAGE;
    }

    /**
     * 退款申请审核.
     * 
     * @param status 审核状态（2-通过、4-不通过）
     * @param id 退货单ID
     * @param seller_remark 审核备注
     * @param return_address
     * @return 操作结果
     */
    public String tksaveAuthtg() {
        try {
            storeOrderReportManager.tksaveAuthtg(status, id, seller_remark, return_address);
            this.showSuccessJson("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorJson("操作失败");
        }
        return JSON_MESSAGE;
    }

    /**
     * 用户拒收审核.
     * 
     * @param status 审核状态（2-通过、4-不通过）
     * @param id 退货单ID
     * @param seller_remark 审核备注
     * @param return_address
     * @return 操作结果
     */
    public String jssaveAuthtg() {
        try {
            storeOrderReportManager.jssaveAuthtg(status, id, seller_remark, return_address);
            this.showSuccessJson("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorJson("操作失败");
        }
        return JSON_MESSAGE;
    }

    /**
     * 保存物流公司和快递单号
     * @param status 审核状态
     * @param id 退货单ID
     * @param seller_remark 审核备注
     * @return
     */
    public String saveKdgs(){
        try {
            storeOrderReportManager.saveKdgs(id,kddh,wlgs);
            this.showSuccessJson("操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorJson("操作失败");
        }
        return JSON_MESSAGE;
    }

    //get set
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getStatus() {
        return status;
    }
    public void setStatus(Integer status) {
        this.status = status;
    }
    public Integer getOrderId() {
        return orderId;
    }
    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
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
    public String getRemark() {
        return remark;
    }
    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getRefund_way() {
        return refund_way;
    }
    public void setRefund_way(String refund_way) {
        this.refund_way = refund_way;
    }
    public String getReturn_account() {
        return return_account;
    }
    public void setReturn_account(String return_account) {
        this.return_account = return_account;
    }
    public String getSeller_remark() {
        return seller_remark;
    }
    public void setSeller_remark(String seller_remark) {
        this.seller_remark = seller_remark;
    }
    public ISellBackManager getSellBackManager() {
        return sellBackManager;
    }
    public void setSellBackManager(ISellBackManager sellBackManager) {
        this.sellBackManager = sellBackManager;
    }
    public IStoreOrderManager getStoreOrderManager() {
        return storeOrderManager;
    }
    public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
        this.storeOrderManager = storeOrderManager;
    }
    public IStoreMemberManager getStoreMemberManager() {
        return storeMemberManager;
    }
    public void setStoreMemberManager(IStoreMemberManager storeMemberManager) {
        this.storeMemberManager = storeMemberManager;
    }
    public IStoreOrderReportManager getStoreOrderReportManager() {
        return storeOrderReportManager;
    }
    public void setStoreOrderReportManager(
            IStoreOrderReportManager storeOrderReportManager) {
        this.storeOrderReportManager = storeOrderReportManager;
    }

    public File[] getImage() {
        return image;
    }

    public void setImage(File[] image) {
        this.image = image;
    }

    public String[] getImageFileName() {
        return imageFileName;
    }

    public void setImageFileName(String[] imageFileName) {
        this.imageFileName = imageFileName;
    }


    public File getImagea() {
        return imagea;
    }


    public void setImagea(File imagea) {
        this.imagea = imagea;
    }


    public String getImageaFileName() {
        return imageaFileName;
    }


    public void setImageaFileName(String imageaFileName) {
        this.imageaFileName = imageaFileName;
    }


    public File getImageb() {
        return imageb;
    }


    public void setImageb(File imageb) {
        this.imageb = imageb;
    }


    public String getImagebFileName() {
        return imagebFileName;
    }


    public void setImagebFileName(String imagebFileName) {
        this.imagebFileName = imagebFileName;
    }


    public File getImagec() {
        return imagec;
    }


    public void setImagec(File imagec) {
        this.imagec = imagec;
    }


    public String getImagecFileName() {
        return imagecFileName;
    }


    public void setImagecFileName(String imagecFileName) {
        this.imagecFileName = imagecFileName;
    }


    public Integer getWorkflow() {
        return workflow;
    }


    public void setWorkflow(Integer workflow) {
        this.workflow = workflow;
    }


    public Double getReturn_price() {
        return return_price;
    }

    public void setReturn_price(Double return_price) {
        this.return_price = return_price;
    }

    public String getReturn_address() {
        return return_address;
    }


    public void setReturn_address(String return_address) {
        this.return_address = return_address;
    }


    public String getKddh() {
        return kddh;
    }


    public void setKddh(String kddh) {
        this.kddh = kddh;
    }


    public String getWlgs() {
        return wlgs;
    }


    public void setWlgs(String wlgs) {
        this.wlgs = wlgs;
    }

}
