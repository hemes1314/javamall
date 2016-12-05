package com.enation.app.shop.core.action.backend;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.service.order.IStoreOrderManager;
import com.enation.app.base.core.service.IMemberManager;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipaySubmit;
import com.enation.app.shop.core.model.Depot;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.SellBackChild;
import com.enation.app.shop.core.model.SellBackGoodsList;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.model.SellBackStatus;
import com.enation.app.shop.core.plugin.order.OrderPluginBundle;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IDepotManager;
import com.enation.app.shop.core.service.IGoodsStoreManager;
import com.enation.app.shop.core.service.ILogiManager;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.app.shop.core.service.ISellBackManager;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.app.shop.mobile.service.impl.ErpManager;
import com.enation.app.shop.mobile.util.OrderUtils;
import com.enation.eop.sdk.context.UserConext;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.database.IDaoSupport;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.JsonUtil;
import com.enation.framework.util.StringUtil;

/**
 * 退货管理Action
 *
 * @author fenlongli
 */
@SuppressWarnings({ "rawtypes", "serial", "unchecked", "unused" })
@Component
@Scope("prototype")
@ParentPackage("shop_default")
@Namespace("/shop/admin")
@Action("sellBack")
@Results({
        @Result(name = "add", type = "freemarker", location = "/shop/admin/orderReport/add_sellback.html"),
        @Result(name = "auth", type = "freemarker", location = "/shop/admin/orderReport/auth_sellback.html"),
        @Result(name = "payment", type = "freemarker", location = "/shop/admin/orderReport/payment_sellback.html"),
        @Result(name = "returned", type = "freemarker", location = "/shop/admin/orderReport/returned_sellback.html"),
        @Result(name = "list", type = "freemarker", location = "/shop/admin/orderReport/sellback_list.html"),
        @Result(name = "packDetail", type = "freemarker", location = "/shop/admin/orderReport/packDetail.html")//整箱详细
})
public class SellBackAction extends WWAction {
    private ISellBackManager sellBackManager;
    private IPaymentManager paymentManager;
    private IOrderManager orderManager;
    private IStoreOrderManager storeOrderManager;
    private ILogiManager logiManager;
    private Integer orderId;
    private Order orderinfo;
	private List orderItem;
    private String tradesn;
    private SellBackList sellBackList;
    private Integer goodsId[];
    private String goodsName[];//退货商品名
    private Integer goodsNum[];//申请退货数量
    private String goodsRemark[];//退货商品备注
    private Double goodsPrice[];
    private Integer payNum[];//购买数量
    private Integer[] returnNum;//申请退货数
    private Integer[] oldStorageNum;//已入库数量
    private Integer storageNum[];//入库商品数量
    private Integer status;
    private Integer id;
    private List goodsList;
    private List logList;
    private String cancelRemark;//取消退货备注
    private String keyword;
    private Integer gid[];
    private IDepotManager depotManager;
    private Integer depotid;
    private Integer ctype;
    private Integer productId[];
    private String seller_remark;
    private String depot_name;
    private List<Depot> depotlist;
    private List paymentList;

    private List orderItemDetail;    //整箱详情
    private Integer itemId;
    private String packDetailJson;
    private List return_child_list;    //子项详情
    private OrderPluginBundle orderPluginBundle;
    private IDaoSupport daoSupport;
    private IGoodsStoreManager goodsStoreManager;
    private IMemberManager memberManager;
    private String return_price;//退款金额
    private ErpManager erpManager;

    @Value("#{configProperties['pay.callback.endpoint']}")
    private String payCallbackEndpoint;
    
    private String onLinePaymentName; //在线支付方式 chenzhognwei add
    
    /**
     * 退货申请列表
     */
    public String list() {
        return "list";
    }

    public String listJson() {
        this.webpage = sellBackManager.list(this.getPage(), this.getPageSize(), status);
        this.showGridJson(this.webpage);
        return JSON_MESSAGE;
    }

    //订单号查询
    public String searchSn() {
        orderinfo = orderManager.get(orderId);//订单详细
        int num = this.sellBackManager.searchSn(orderinfo.getSn());
        //System.out.println(num);
        //提交过返回success 因为只有success能传递id
        if (num > 0) {
            this.showSuccessJson("订单已提交过退货申请", num);
        } else {
            this.showErrorJson("");
        }
        return JSON_MESSAGE;
    }

    /**
     * 退货搜索
     *
     * @return
     */
    public String search() {
        this.webpage = sellBackManager.search(keyword, this.getPage(), this.getPageSize());
        return "list";
    }

    /**
     * 新建退货申请
     */
    public String add() {
        orderinfo = orderManager.get(orderId);//订单详细
        orderItem = orderManager.getOrderItem(orderId);
        depotlist = depotManager.list();//仓库列表
        paymentList = paymentManager.list(); //支付方式列表
        return "add";
    }

    /**
     * 审核退货申请
     *
     * @return
     * @author fenlongli
     */
    public String auth() {
        try {
            sellBackList = this.sellBackManager.get(id);//退货详细
            orderinfo = orderManager.get(sellBackList.getOrdersn());//订单详细
            goodsList = this.sellBackManager.getGoodsList(id, sellBackList.getOrdersn());//退货商品列表
            depot_name = depotManager.get(sellBackList.getDepotid()).getName();

            return_child_list = new ArrayList();
            for (int i = 0; i < goodsList.size(); i++) {
                Map map = (Map) goodsList.get(i);
                if (NumberUtils.toInt(map.get("return_type").toString()) == 1) {
                    List list = this.sellBackManager.getSellbackChilds(orderinfo.getOrder_id(), NumberUtils.toInt(map.get("goodsId").toString()));
                    if (list != null) {
                        return_child_list.addAll(list);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "auth";
    }

    /**
     * 审核退货申请
     *
     * @return
     */
    public String saveAuth() {
        try {
            this.sellBackManager.editStatus(status, id, seller_remark);
            this.showSuccessJson("操作成功");
        } catch (Exception e) {
            this.showErrorJson("操作失败");
        }
        return JSON_MESSAGE;
    }

    /**
     * 退货入库
     *
     * @return
     */
    public String returned() {

        sellBackList = this.sellBackManager.get(id);// 退货详细
        orderinfo = orderManager.get(sellBackList.getOrdersn());// 订单详细
        goodsList = this.sellBackManager.getGoodsList(id, sellBackList.getOrdersn());// 退货商品列表
        logList = this.sellBackManager.sellBackLogList(id);// 退货操作日志
        depot_name = depotManager.get(sellBackList.getDepotid()).getName();

        return_child_list = new ArrayList();
        for (int i = 0; i < goodsList.size(); i++) {
            Map map = (Map) goodsList.get(i);
            if (NumberUtils.toInt(map.get("return_type").toString()) == 1) {
                List list = this.sellBackManager.getSellbackChilds(orderinfo.getOrder_id(), NumberUtils.toInt(map.get("goodsId").toString()));
                if (list != null) {
                    return_child_list.addAll(list);
                }
            }

        }

        return "returned";
    }

    /**
     * 财务结算
     *
     * @return
     */
    public String payment() {
        sellBackList = this.sellBackManager.get(id);//退货详细
        orderinfo = orderManager.get(sellBackList.getOrdersn());//订单详细
        //防止页面报错  chenzhongwei add
        if(null==orderinfo.getRefund_status()){
            orderinfo.setRefund_status(0);
        }
        // 赋值主订单对象  chenzhongwei add
        if(this.orderinfo.getParent_id() != null) {
            Order parentOrder = orderManager.get(this.orderinfo.getParent_id());
            this.orderinfo.setParentOrder(parentOrder);
        }
        //获取在线支付方式 chenzhongwei add
        Double need_pay_money = orderinfo.getNeed_pay_money();
		if (need_pay_money != null && need_pay_money > 0) {
			onLinePaymentName = orderinfo.getPayment_name();
		}
		// 退货商品列表
        goodsList = this.sellBackManager.getGoodsList(id, sellBackList.getOrdersn());
        // 退货操作日志
        logList = this.sellBackManager.sellBackLogList(id);
        depot_name = depotManager.get(sellBackList.getDepotid()).getName();

        return_child_list = new ArrayList();
        for (int i = 0; i < goodsList.size(); i++) {
            Map map = (Map) goodsList.get(i);
            if (NumberUtils.toInt(map.get("return_type").toString()) == 1) {
                List list = this.sellBackManager.getSellbackChilds(orderinfo.getOrder_id(), NumberUtils.toInt(map.get("goods_id").toString()));
                if (list != null) {
                    return_child_list.addAll(list);
                }
            }
        }
        return "payment";
    }
    
    /**
     * 统一退款请求处理.
     * <br/>支付宝、微信等.
     */
    public void refund() {
		try {
			// 校验退款金额
			BigDecimal return_price = null;
			// 获取退款退货详情
	    	sellBackList = this.sellBackManager.get(id);
	    	// 获取订单详情
	        orderinfo = storeOrderManager.get(sellBackList.getOrdersn());
	        if (orderinfo.getPayment_id() == null) {
	        	renderHtml("退款订单支付方式不存在，请检查");
	        	return;
	        }
	        
	        // 客户申请的退款金额
	        BigDecimal return_amount = new BigDecimal(sellBackList.getReturn_price());
 			// 客户订单应付金额
 			BigDecimal need_pay_money = new BigDecimal(orderinfo.getNeedPayMoney());
 			// 客户余额支付金额
 			BigDecimal advance_pay = new BigDecimal(orderinfo.getAdvance_pay());
 			// 如果在线支付的金额大于0
 			if (need_pay_money.compareTo(BigDecimal.ZERO) > 0) {
 				if (return_amount.compareTo(BigDecimal.ZERO) == 0) {
 					renderHtml("退款金额为0，无需退款，直接结算即可");
 					return;
 				}
 				if (return_amount.compareTo(need_pay_money) > 0) {
 					BigDecimal alltotal_advance_pay = return_amount.subtract(need_pay_money);
					if (alltotal_advance_pay.compareTo(advance_pay) > 0) {
						logger.info("return_amount: " + return_amount + ", need_pay_money: " + need_pay_money + ", alltotal_advance_pay: " + alltotal_advance_pay + ", advance_pay: " + advance_pay);
						renderHtml("退款金额大于实际支付金额，请检查");
						return;
					}
					return_price = need_pay_money;
 				} else {
 					return_price = return_amount;
 				}
 				return_price = return_price.setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
 				renderHtml(sellBackManager.refund(orderinfo, return_price));
 			} else {
 				renderHtml("无在线支付金额，无需退款，直接结算即可");
 			}
		} catch (Exception e) {
			e.printStackTrace();
			renderHtml(e.getMessage());
			return;
		}
    }
    
    /**
     * 支付宝退款
     */
    @Deprecated
    public void alipayRefund() {
        sellBackList = this.sellBackManager.get(id);//退货详细
        orderinfo = orderManager.get(sellBackList.getOrdersn());//订单详细

        PayCfg payCfg = this.paymentManager.get(orderinfo.getPayment_id());
        Map<String, String> params = paymentManager.getConfigParams("alipayDirectPlugin");
        String partner = params.get("partner");  // 支付宝合作伙伴id (账户内提取)
        String key = params.get("key");  // 支付宝安全校验码(账户内提取)
        String seller_email = params.get("seller_email"); // 卖家支付宝帐户
        String content_encoding = params.get("content_encoding"); // 卖家支付宝帐户

        AlipayConfig.key = key;
        AlipayConfig.partner = partner;
        AlipayConfig.seller_email = seller_email;

        try {
        	HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            
            StringBuilder url = new StringBuilder("http://");
            if(StringUtils.isBlank(payCallbackEndpoint)) {
                url.append(request.getServerName());
                if(request.getLocalPort() != 80) {
                    url.append(":").append(request.getLocalPort());
                }
            } else {
                url.append(payCallbackEndpoint);
            }

            //服务器异步通知页面路径
            String notify_url = url.append("/api/shop/b_alipayDirectPlugin_refund-callback.do").toString();
            //需http://格式的完整路径，不允许加?id=123这类自定义参数
            //String notify_url = "http://zues.5166.info:20589/api/shop/b_alipayDirectPlugin_refund-callback.do";

            //退款当天日期
            String refund_date = DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss");
            //必填，格式：年[4位]-月[2位]-日[2位] 小时[2位 24小时制]:分[2位]:秒[2位]，如：2007-10-01 13:13:13

            //批次号
            String batch_no = DateUtil.toString(new Date(), "yyyyMMddHHmmss");
            //必填，格式：当天日期[8位]+序列号[3至24位]，如：201008010000001

            //退款笔数
            String batch_num = "1";
            //必填，参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的数量999个）

            //退款详细数据格式:  '支付宝交易订单号^退款金额^退款描述#第二笔支付宝交易订单号^退款金额^退款描述'
            //退款金额为 支付宝 使用金额
//			if(orderinfo.getPayment_name().equals("支付宝")){
//				orderinfo.getNeedPayMoney();
//			}

            BigDecimal return_price;
            try {
                BigDecimal return_amount = BigDecimal.valueOf(sellBackList.getReturn_price());//客户申请的退款金额
                BigDecimal advance_pay = BigDecimal.valueOf(orderinfo.getAdvance_pay()); //客户余额支付金额
                //如果 申请退款金额 大于等于 余额支付金额  支付宝退款金额为 申请的退款金额 - 余额支付金额  否则为  为错误
                if (return_amount.compareTo(advance_pay) > 0) {
                    return_price = return_amount.subtract(advance_pay).setScale(2, java.math.BigDecimal.ROUND_HALF_UP);
                    //如果实际支付金额 小于  要退款金额 则为错误
                    if (BigDecimal.valueOf(orderinfo.getNeedPayMoney()).compareTo(return_price) < 0) {
                        renderHtml("退款金额 大于 支付金额 请检查");
                        return;
                    }
                } else {
                    renderHtml("申请退款金额 小于 余额支付金额 不可进行支付宝退款 请检查");
                    return;
                }
            } catch (Exception e) {
                return_price = BigDecimal.ZERO;
            }


            String data = orderinfo.getTradeno() + "^" + return_price + "^" + "协商退款";
            String detail_data = data;//new String(data.getBytes("ISO-8859-1"),"UTF-8");
            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("service", "refund_fastpay_by_platform_pwd");
            sParaTemp.put("partner", AlipayConfig.partner);
            sParaTemp.put("_input_charset", AlipayConfig.input_charset);
            sParaTemp.put("notify_url", notify_url);
            sParaTemp.put("seller_email", seller_email);
            sParaTemp.put("refund_date", refund_date);
            sParaTemp.put("batch_no", batch_no);
            sParaTemp.put("batch_num", batch_num);
            sParaTemp.put("detail_data", detail_data);

            //建立请求
            String sHtmlText = AlipaySubmit.buildRequest(sParaTemp, "post", "确认");
            renderHtml(sHtmlText);

            this.daoSupport.execute("update es_order set refund_batchno=? where order_id=?", batch_no, orderinfo.getOrder_id());
        } catch (Exception e) {
            e.printStackTrace();
            renderHtml(e.getMessage());
        }
    }

    /**
     * 保存退货申请
     *
     * @return
     */
    public String save() {
        String goodslist = "";
        //查找用户选中的goodsid对应的数据
        if (goodsId != null) {
            for (int i = 0; i < goodsId.length; i++) {
                for (int j = 0; j < gid.length; j++) {
                    if (goodsId[i].intValue() == gid[j].intValue()) {
                        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
                        String isPack = request.getParameter("isPack_" + goodsId[i].intValue());
                        //如果该商品是一个整箱  则把箱内的商品 提取出来
                        if ("1".equals(isPack)) {
                            goodslist = goodslist + goodsName[j];
                        } else {
                            goodslist = goodslist + goodsName[j] + "(" + goodsNum[j] + ") ";
                        }
                    }
                }
            }
        }
        try {
            //Logi logi = logiManager.getLogiById(sellBackList.getLogi_id());
            sellBackList.setGoodslist(goodslist);
            SellBackList sellback = this.sellBackManager.get(sellBackList.getTradeno());
            Order order = orderManager.get(orderId);
            if (order.getShipping_area() != null || !StringUtil.isEmpty(order.getShipping_area()) || order.getShipping_area().trim().equals("暂空")) {
                sellBackList.setAdr(order.getShip_addr());
            } else {
                String adr[] = order.getShipping_area().split("-");
                sellBackList.setAdr(adr[0] + adr[1] + adr[2] + order.getShip_addr());
            }
            sellBackList.setRegtime(DateUtil.getDateline());
            sellBackList.setRegoperator(UserConext.getCurrentAdminUser().getUsername());
            sellBackList.setTel(order.getShip_tel());
            sellBackList.setZip(order.getShip_zip());
            sellBackList.setTradestatus(status);
            sellBackList.setDepotid(depotid);
            //保存退货单
            Integer sid = this.sellBackManager.save(sellBackList, true);

            if (sellback != null) {
                SellBackList sellbacklist = this.sellBackManager.get(sellBackList.getTradeno());
                this.sellBackManager.saveLog(sellbacklist.getId(), SellBackStatus.valueOf(sellbacklist.getTradestatus()), "");
            }

            /**
             * 关于整箱退货代码修改   Start
             * 冯兴隆 2015-07-14
             */
            List<Map<String, Object>> list = JsonUtil.toList(packDetailJson);
            if (goodsId != null) {
                for (int i = 0; i < goodsId.length; i++) {
                    int nowGoodsId = goodsId[i];
                    for (int j = 0; j < gid.length; j++) {
                        //判断商品是否被选中
                        if (goodsId[i].intValue() == gid[j].intValue()) {
                            //获取退货详情
                            SellBackGoodsList sellBackGoods = this.sellBackManager.getSellBackGoods(sid, nowGoodsId);
                            if (sellBackGoods != null) {
                                this.editGoodsList(goodsNum[j], sid, nowGoodsId, sellBackList.getSeller_remark(), null);
                            } else {
                                this.saveGoodsList(nowGoodsId, goodsNum[j],
                                        goodsPrice[j], payNum[j], sid,
                                        sellBackList.getSeller_remark(), null,
                                        productId[i]);
                            }
                            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
                            String isPack = request.getParameter("isPack_" + nowGoodsId);
                            //如果该商品是一个整箱  则把箱内的商品 提取出来
                            if ("1".equals(isPack)) {
                                String isAll = request.getParameter("is_all_" + nowGoodsId);        //是否是全部退货

                                //如果是全部退货 则自动算出数量
                                if ("0".equals(isAll)) {
                                    List<Map> listGoods = sellBackManager.list(nowGoodsId);
                                    for (Map map : listGoods) {
                                        int tempGoodsId = NumberUtils.toInt(map.get("rel_goods_id").toString());
                                        Map childGoodsInfo = this.sellBackManager.getPackInfo(nowGoodsId, tempGoodsId);
                                        int pkgNum = NumberUtils.toInt(childGoodsInfo.get("pkgnum").toString());
                                        int tempNum = goodsNum[j] * pkgNum;
                                        //int tempNum =  goodsNum[j];
                                        this.saveOrUpdateSellbackChild(orderId, tempGoodsId, nowGoodsId, tempNum);
                                    }
                                } else {
                                    for (Map<String, Object> map : list) {
                                        int packGoodsId = NumberUtils.toInt(map.get("packGoodsId").toString());
                                        //如果该条数据是该整箱中的商品
                                        if (nowGoodsId == packGoodsId) {
                                            int tempGoodsId = NumberUtils.toInt(map.get("goodsId").toString());
                                            int tempNum = NumberUtils.toInt(map.get("num").toString());
                                            this.saveOrUpdateSellbackChild(orderId, tempGoodsId, nowGoodsId, tempNum);
                                        }
                                    }
                                }

                            }
                        }
                    }
                }
            }
            /**
             * 关于整箱退货代码修改   End
             * 冯兴隆 2015-07-14
             */
            this.showSuccessJson("操作成功！", sid);
        } catch (Exception e) {
            //e.printStackTrace();
            this.showErrorJson("操作失败:" + e.getMessage());
        }
        return JSON_MESSAGE;
    }

    /**
     * 判断json数组转换的list中 是否含有该商品Id  相当于判断该商品是不是一个整箱
     * @param goodsId
     * @param list
     * @return boolean
     */
    /*private boolean isHaveGoods(int goodsId,List<Map<String,Object>> list){
        boolean result = false;
		for(Map map : list){
			int packGoodsId = NumberUtils.toInt(map.get("packGoodsId").toString());
			if(goodsId == packGoodsId){
				result = true;
				break;
			}
		}
		return result;
	}*/

    /**
     * 新增或修改 整箱内的退货子项详情
     *
     * @param orderId
     * @param goodsId
     * @param parentId
     * @param returnNum
     */
    private void saveOrUpdateSellbackChild(int orderId, int goodsId, int parentId, int returnNum) {
        SellBackChild sellBackChild = this.sellBackManager.getSellbackChild(orderId, goodsId);
        if (sellBackChild != null) {
            this.sellBackManager.updateSellbackChild(orderId, goodsId, returnNum, 0);
        } else {
            this.sellBackManager.saveSellbackChild(orderId, goodsId, parentId, returnNum);
        }
    }


    /**
     * 退货申请入库
     *
     * @return
     */
    public String update() {

        SellBackList sellback = this.sellBackManager.get(id);
        orderinfo = orderManager.get(sellback.getOrdersn());// 订单详细

        String goodslist = "";
        status = 2;// 先假设全部入库

        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String json = request.getParameter("storageDetail");
        List<Map<String, Object>> list = JsonUtil.toList(json);

        if (goodsId != null) {
            for (int i = 0; i < goodsId.length; i++) {
                for (int j = 0; j < gid.length; j++) {

                    int rnum = this.returnNum[i]; // 申请退货的数量
                    int osnum = this.oldStorageNum[i];// 已入库的数量

                    if (goodsId[i].intValue() == gid[j].intValue()) {
                        /**
                         * 关于整箱退货入库代码修改   Start
                         * 冯兴隆 2015-07-16
                         */
                        int nowGoodsId = goodsId[i];
                        SellBackGoodsList sellBackGoods = this.sellBackManager.getSellBackGoods(id, goodsId[i]);
                        if (sellBackGoods.getReturn_type() != 1) {
                            int snum = this.storageNum[j];// 本次入库的数量

                            if (snum + osnum > rnum) {
                                this.showErrorJson("入库数量不能大于退货数量");
                                return JSON_MESSAGE;
                            }
                            // 还有部分入库的情况
                            if (snum + osnum < rnum) {
                                status = 5;
                            }

                            goodslist = goodslist + goodsName[j] + "(" + rnum + ") ";

                            if (sellBackGoods != null) {
                                this.editGoodsList(null, id, nowGoodsId, sellBackGoods.getGoods_remark(), snum + osnum);
                            }
                        } else {
                            goodslist = goodslist + goodsName[j];
                            List<Map> listChilds = this.sellBackManager.getSellbackChilds(orderinfo.getOrder_id(), nowGoodsId);
                            for (Map child : listChilds) {
                                int tempONum = NumberUtils.toInt(child.get("storage_num").toString());        //已入库数量
                                int tempRNum = NumberUtils.toInt(child.get("return_num").toString());        // 申请退货的数量
                                int parentId = NumberUtils.toInt(child.get("parent_id").toString());            //所属整箱商品id
                                int childGoodsId = NumberUtils.toInt(child.get("goods_id").toString());
                                for (Map<String, Object> map : list) {

                                    int tempGoodsId = NumberUtils.toInt(map.get("goodsId").toString());
                                    int tempParentId = NumberUtils.toInt(map.get("parentId").toString());
                                    if (childGoodsId == tempGoodsId && parentId == tempParentId) {
                                        int tempSNum = NumberUtils.toInt(map.get("num").toString());        //本次入库数量

                                        if (tempSNum + tempONum > tempRNum) {
                                            this.showErrorJson("入库数量不能大于退货数量");
                                            return JSON_MESSAGE;
                                        }
                                        // 还有部分入库的情况
                                        if (tempSNum + tempONum < tempRNum) {
                                            status = 5;
                                        }
                                        this.sellBackManager.updateSellbackChild(orderinfo.getOrder_id(), childGoodsId, tempRNum, tempSNum + tempONum);
                                    }
                                }

                            }
                        }
                        /**
                         * 关于整箱退货入库代码修改   End
                         * 冯兴隆 2015-07-16
                         */
                    }
                }
            }
        }

        try {
            sellback.setGoodslist(goodslist);
            sellback.setWarehouse_remark(sellBackList.getWarehouse_remark());
            sellback.setTradestatus(status);
            this.sellBackManager.save(sellback, true);
            if (status == 2 || status == 5) {
                this.sellBackManager.saveLog(sellback.getId(),
                        SellBackStatus.valueOf(sellback.getTradestatus()), "");
            }
            this.showSuccessJson("操作成功！");
        } catch (Exception e) {
            e.printStackTrace();
            this.showErrorJson("操作失败 ！");
        }
        return JSON_MESSAGE;
    }

    /**
     * 财务结算
     *
     * @return 操作结果
     */
	public String savePayment() {
		try {
			// 退货详细
			sellBackList = this.sellBackManager.get(id);
			// 订单详细
			orderinfo = orderManager.get(sellBackList.getOrdersn());
			// 验证是否已经退过款 chenzhongwei add
			if (OrderStatus.ORDER_CANCEL_PAY == orderinfo.getStatus() || OrderStatus.ORDER_CANCEL_SHIP == orderinfo.getStatus()) {
				this.showErrorJson("请不要重复退款");
				return JSON_MESSAGE;
			}
			
			// 客户申请的退款金额
            BigDecimal return_amount = new BigDecimal(NumberUtils.toDouble(return_price));
            // 实际退款金额
            BigDecimal alltotal_pay = BigDecimal.ZERO;
            // 客户订单应付金额
			BigDecimal need_pay_money = new BigDecimal(orderinfo.getNeedPayMoney());
			// 客户余额支付金额
			BigDecimal advance_pay = new BigDecimal(orderinfo.getAdvance_pay());
			// 如果在线支付的金额大于0 chenzhongwei add
			if (need_pay_money.compareTo(BigDecimal.ZERO) > 0) {
				if (return_amount.compareTo(need_pay_money) > 0) {
					BigDecimal alltotal_advance_pay = return_amount.subtract(need_pay_money);
					if (alltotal_advance_pay.compareTo(advance_pay) < 0) {
						advance_pay = alltotal_advance_pay;
					}
					alltotal_pay = need_pay_money.add(advance_pay);
				} else {
					alltotal_pay = return_amount;
					advance_pay = BigDecimal.ZERO;
				}
			} else {
				// 如果 申请退款金额 大于 余额支付金额 退款金额为 余额支付金额 否则为 申请退款金额
				if (return_amount.compareTo(advance_pay) > 0) {
					alltotal_pay = advance_pay;
				} else {
					alltotal_pay = return_amount;
					advance_pay = return_amount;
				}
			}
			this.sellBackManager.closePayable(id,
					sellBackList.getFinance_remark(),
					"财务退款：" + alltotal_pay.toString() + "元",
					alltotal_pay.doubleValue(), advance_pay.doubleValue());

			// 通知即可, 这里是退货把?
			// erpManager.notifyOmsForDefault(sellBackList.getOrdersn(), "" + OrderStatus.PAY_CANCEL);
			// erpManager.notifyOmsForRefund(order);

			this.showSuccessJson("操作成功！");
		} catch (Exception e) {
			this.showErrorJson("操作失败！");
		}
		return JSON_MESSAGE;
	}


    public void update(Integer id) {
        SellBackList sellBackList = sellBackManager.get(id);
        List<Map> goodsList = this.sellBackManager.getGoodsList(id);
        for (Map map : goodsList) {
            Integer goods_id = NumberUtils.toInt(map.get("goods_id").toString());
            Integer product_id = NumberUtils.toInt(map.get("product_id").toString());

            if (this.sellBackManager.isPack(product_id) == 1) {

                Integer orderid = this.orderManager.get(sellBackList.getOrdersn()).getOrder_id();
                List<Map> list = this.sellBackManager.getSellbackChilds(orderid, goods_id);
                if (list != null) {
                    for (Map mapTemp : list) {
                        Integer childGoodsId = NumberUtils.toInt(mapTemp.get("goods_id").toString());
                        Integer childProductId = NumberUtils.toInt(mapTemp.get("product_id").toString());
                        Integer childNum = NumberUtils.toInt(mapTemp.get("return_num").toString());
                        this.sellBackManager.editChildStorageNum(orderid, goods_id, childGoodsId, childNum);
                        goodsStoreManager.increaseStroe(childGoodsId, childProductId, 1, childNum);

                    }
                }
            } else {

                Integer num = NumberUtils.toInt(map.get("return_num").toString());
                this.sellBackManager.editStorageNum(id, goods_id, num);//修改入库数量
                goodsStoreManager.increaseStroe(goods_id, product_id, 1, num);
            }


        }
    }

    /**
     * 保存退货商品
     */
    public Integer saveGoodsList(Integer goodsid, Integer goodsnum, Double price, Integer paynum, Integer id, String remark, Integer storageNum, Integer productid) {
        SellBackGoodsList sellBackGoods = new SellBackGoodsList();
        if (storageNum != null) {
            sellBackGoods.setStorage_num(storageNum);
            sellBackGoods.setReturn_num(storageNum);
        }
        if (goodsnum == null) {
            sellBackGoods.setReturn_num(0);
        } else {
            sellBackGoods.setReturn_num(goodsnum);
        }
        sellBackGoods.setGoods_id(goodsid);
        sellBackGoods.setPrice(price);
        sellBackGoods.setRecid(id);
        sellBackGoods.setShip_num(paynum);
        sellBackGoods.setGoods_remark(remark);
        sellBackGoods.setProduct_id(productid);
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        String isPack = request.getParameter("isPack_" + goodsid);
        try {
            //如果商品为全部退货或者为单只产品
            if (isPack.equals("1")) {
                String return_type = request.getParameter("is_all_" + goodsid);        //是否是全部退货
                if ("0".equals(return_type)) {
                    sellBackGoods.setReturn_type(0);
                } else {
                    sellBackGoods.setReturn_type(1);

                }
            } else {
                sellBackGoods.setReturn_type(0);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        return this.sellBackManager.saveGoodsList(sellBackGoods);
    }


    /**
     * 编辑退货商品
     */
    public void editGoodsList(Integer goodsNum, Integer recid, Integer goodsid, String remark, Integer storageNum) {
        if (goodsNum != null) {
            if (goodsNum > 0) {
                Map map = new HashMap();
                map.put("recid", recid);
                map.put("goods_id", goodsid);
                map.put("return_num", goodsNum);
                map.put("goods_remark", remark);
                this.sellBackManager.editGoodsNum(map);
            } else {
                this.sellBackManager.delGoods(recid, goodsid);
            }
        }
        if (storageNum != null)
            this.sellBackManager.editStorageNum(recid, goodsid, storageNum);//修改入库数量
    }

    /**
     * 取消退货
     *
     * @return
     */
    public String cancel() {
        try {
            SellBackList sellbacklist = null;
            status = ctype;
            if (id != null) {
                if (status == 0 || status == 1) {
                    sellbacklist = this.sellBackManager.get(id);
                } else {
                    this.showErrorJson("该退货单的商品已入库，不能取消退货！");
                    return JSON_MESSAGE;
                }
            } else {
                if (sellBackList.getTradeno() != null) {
                    if (status == 0 || status == 1) {
                        sellbacklist = this.sellBackManager.get(sellBackList.getTradeno());
                    } else {
                        this.showErrorJson("该退货单的商品已入库，不能取消退货！");
                        return JSON_MESSAGE;
                    }
                }
            }
            if (sellbacklist != null) {
                sellbacklist.setTradestatus(4);// 取消
                this.sellBackManager.save(sellbacklist, true);
                this.sellBackManager.saveLog(sellbacklist.getId(),
                        SellBackStatus.valueOf(sellbacklist.getTradestatus()),
                        "取消退货，原因：" + cancelRemark);
                orderinfo = orderManager.get(sellbacklist.getOrdersn());// 订单详细
                this.sellBackManager.delSellerBackChilds(orderinfo.getOrder_id());
                this.showSuccessJson("取消退货成功！");
            } else {
                this.showSuccessJson("操作成功！");
            }
        } catch (Exception e) {
            this.showErrorJson("取消退货失败！");
        }
        return JSON_MESSAGE;
    }

    /**
     * 获取整箱详细
     *
     * @return
     */
    public String packDetail() {
        orderItemDetail = orderManager.getOrderItemDetail(itemId);
        return "packDetail";
    }

    /**
     * 创建退货单号
     */
    public String createSn() {
        Date now = new Date();
        String sn = com.enation.framework.util.DateUtil.toString(now, "yyMMddhhmmss");
        return sn;
    }
    
    /**
     * 是否可以跳转到退款页面
     * 
     * @author chenzhongwei
     * @return 支付宝提示语
     */
    public String checkAliPayButton() {
		if (null != id) {
			// 退货详细
			sellBackList = this.sellBackManager.get(id);
			if (null != sellBackList) {
                String ordersn = sellBackList.getOrdersn();
				if (StringUtils.isNotEmpty(ordersn)) {
					// 订单详细
                    orderinfo = orderManager.get(ordersn);
                    Integer refundStatus = orderinfo.getRefund_status();
                    if (refundStatus == null) refundStatus = 0;
                    String payType = orderinfo.getPayment_type();
        			if (StringUtils.isBlank(payType)) {
        				this.showErrorJson("支付方式不确定，不可进行退款，请检查");
        				return JSON_MESSAGE;
        			}
        			if (payType.startsWith("alipay")) {
        				switch(refundStatus) {
	                        case 0:
	                            this.showSuccessJson("跳转到支付宝退款页面？", "alipay");
	                            break;
	                        case 1:
	                            this.showErrorJson("支付宝退款成功");
	                            break;
	                        case 2:
	                            this.showErrorJson("支付宝退款失败，请联系管理员");
	                            break;
		             	}
        			} else if (payType.startsWith("wechat")) {
        				switch(refundStatus) {
	                        case 0:
	                            this.showSuccessJson("请求微信支付退款申请？", "wxpay");
	                            break;
	                        case 1:
	                            this.showErrorJson("微信支付退款成功");
	                            break;
	                        case 2:
	                            this.showErrorJson("微信支付退款失败，请联系管理员");
	                            break;
        				}
        			}
                }
            }
        }
        return JSON_MESSAGE;
    }
    
    /**
     * 验证是否可以点击确认结算按钮
     * 
     * @author chenzhongwei
     * @return 订单信息
     */
    public String checkReturnPriceButton() {
		if (null != orderId) {
			orderinfo = orderManager.get(orderId);
			if (null != orderinfo) {
				Integer refundStatus = orderinfo.getRefund_status();
                if (refundStatus == null) refundStatus = 0;
                String payType = orderinfo.getPayment_type();
                if (StringUtils.isBlank(payType)) {
    				this.showErrorJson("支付方式不确定，不可进行结算，请检查");
    				return JSON_MESSAGE;
    			}
    			if (payType.startsWith("alipay")) {
    				switch(refundStatus) {
                        case 0:
                        	this.showErrorJson("请先进行支付宝退款操作");
                            break;
                        case 1:
                        	this.showSuccessJson("可以进行确认结算操作");
                            break;
                        case 2:
                        	this.showErrorJson("请先进行支付宝退款操作");
                            break;
	             	}
    			} else if (payType.startsWith("wechat")) {
    				switch(refundStatus) {
                        case 0:
                            this.showErrorJson("请先进行微信支付退款操作");
                            break;
                        case 1:
                        	this.showSuccessJson("可以进行确认结算操作");
                            break;
                        case 2:
                        	this.showErrorJson("请先进行微信支付退款操作");
                            break;
    				}
    			}
			}
		}
        return JSON_MESSAGE;
    }

    public ISellBackManager getSellBackManager() {
        return sellBackManager;
    }

    public void setSellBackManager(ISellBackManager sellBackManager) {
        this.sellBackManager = sellBackManager;
    }

    public IOrderManager getOrderManager() {
        return orderManager;
    }

    public void setOrderManager(IOrderManager orderManager) {
        this.orderManager = orderManager;
    }

    public IStoreOrderManager getStoreOrderManager() {
		return storeOrderManager;
	}

	public void setStoreOrderManager(IStoreOrderManager storeOrderManager) {
		this.storeOrderManager = storeOrderManager;
	}

	public ILogiManager getLogiManager() {
        return logiManager;
    }

    public void setLogiManager(ILogiManager logiManager) {
        this.logiManager = logiManager;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Order getOrderinfo() {
        return orderinfo;
    }

    public void setOrderinfo(Order orderinfo) {
        this.orderinfo = orderinfo;
    }

    public List getOrderItem() {
        return orderItem;
    }

    public void setOrderItem(List orderItem) {
        this.orderItem = orderItem;
    }

    public String getTradesn() {
        return tradesn;
    }

    public void setTradesn(String tradesn) {
        this.tradesn = tradesn;
    }

    public SellBackList getSellBackList() {
        return sellBackList;
    }

    public void setSellBackList(SellBackList sellBackList) {
        this.sellBackList = sellBackList;
    }

    public Integer[] getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Integer[] goodsId) {
        this.goodsId = goodsId;
    }

    public String[] getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String[] goodsName) {
        this.goodsName = goodsName;
    }

    public Integer[] getGoodsNum() {
        return goodsNum;
    }

    public void setGoodsNum(Integer[] goodsNum) {
        this.goodsNum = goodsNum;
    }

    public String[] getGoodsRemark() {
        return goodsRemark;
    }

    public void setGoodsRemark(String[] goodsRemark) {
        this.goodsRemark = goodsRemark;
    }

    public Double[] getGoodsPrice() {
        return goodsPrice;
    }

    public void setGoodsPrice(Double[] goodsPrice) {
        this.goodsPrice = goodsPrice;
    }

    public Integer[] getPayNum() {
        return payNum;
    }

    public void setPayNum(Integer[] payNum) {
        this.payNum = payNum;
    }

    public Integer[] getReturnNum() {
        return returnNum;
    }

    public void setReturnNum(Integer[] returnNum) {
        this.returnNum = returnNum;
    }

    public Integer[] getOldStorageNum() {
        return oldStorageNum;
    }

    public void setOldStorageNum(Integer[] oldStorageNum) {
        this.oldStorageNum = oldStorageNum;
    }

    public Integer[] getStorageNum() {
        return storageNum;
    }

    public void setStorageNum(Integer[] storageNum) {
        this.storageNum = storageNum;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List goodsList) {
        this.goodsList = goodsList;
    }

    public List getLogList() {
        return logList;
    }

    public void setLogList(List logList) {
        this.logList = logList;
    }

    public String getCancelRemark() {
        return cancelRemark;
    }

    public void setCancelRemark(String cancelRemark) {
        this.cancelRemark = cancelRemark;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer[] getGid() {
        return gid;
    }

    public void setGid(Integer[] gid) {
        this.gid = gid;
    }

    public IDepotManager getDepotManager() {
        return depotManager;
    }

    public void setDepotManager(IDepotManager depotManager) {
        this.depotManager = depotManager;
    }

    public Integer getDepotid() {
        return depotid;
    }

    public void setDepotid(Integer depotid) {
        this.depotid = depotid;
    }

    public Integer getCtype() {
        return ctype;
    }

    public void setCtype(Integer ctype) {
        this.ctype = ctype;
    }

    public Integer[] getProductId() {
        return productId;
    }

    public void setProductId(Integer[] productId) {
        this.productId = productId;
    }

    public String getSeller_remark() {
        return seller_remark;
    }

    public void setSeller_remark(String seller_remark) {
        this.seller_remark = seller_remark;
    }

    public String getDepot_name() {
        return depot_name;
    }

    public void setDepot_name(String depot_name) {
        this.depot_name = depot_name;
    }

    public List<Depot> getDepotlist() {
        return depotlist;
    }

    public void setDepotlist(List<Depot> depotlist) {
        this.depotlist = depotlist;
    }

    public IPaymentManager getPaymentManager() {
        return paymentManager;
    }

    public void setPaymentManager(IPaymentManager paymentManager) {
        this.paymentManager = paymentManager;
    }

    public List getPaymentList() {
        return paymentList;
    }

    public void setPaymentList(List paymentList) {
        this.paymentList = paymentList;
    }

    public List getOrderItemDetail() {
        return orderItemDetail;
    }

    public void setOrderItemDetail(List orderItemDetail) {
        this.orderItemDetail = orderItemDetail;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getPackDetailJson() {
        return packDetailJson;
    }

    public void setPackDetailJson(String packDetailJson) {
        this.packDetailJson = packDetailJson;
    }

    public List getReturn_child_list() {
        return return_child_list;
    }

    public void setReturn_child_list(List return_child_list) {
        this.return_child_list = return_child_list;
    }

    public OrderPluginBundle getOrderPluginBundle() {
        return orderPluginBundle;
    }

    public void setOrderPluginBundle(OrderPluginBundle orderPluginBundle) {
        this.orderPluginBundle = orderPluginBundle;
    }

    public IDaoSupport getDaoSupport() {
        return daoSupport;
    }

    public void setDaoSupport(IDaoSupport daoSupport) {
        this.daoSupport = daoSupport;
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

    public ErpManager getErpManager() {
        return erpManager;
    }

    public void setErpManager(ErpManager erpManager) {
        this.erpManager = erpManager;
    }

    public String getReturn_price() {
        return return_price;
    }

    public void setReturn_price(String return_price) {
        this.return_price = return_price;
    }

    
    public String getOnLinePaymentName() {
        return onLinePaymentName;
    }

    
    public void setOnLinePaymentName(String onLinePaymentName) {
        this.onLinePaymentName = onLinePaymentName;
    }

}