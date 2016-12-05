package com.enation.app.shop.mobile.payment.plugin;

import java.math.BigDecimal;
import java.net.URLDecoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.b2b2c.core.model.order.StoreOrder;
import com.enation.app.base.core.plugin.job.IEveryMinutesExecuteEvent;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.plugin.payment.PaymentPluginBundle;
import com.enation.app.shop.mobile.payment.WeixinUtil;
import com.enation.app.shop.mobile.payment.WeixinUtil.WxPayTradeType;
import com.enation.app.shop.mobile.util.HttpUtils;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;

/**
 * 微信支付插件.
 * 
 * @author baoxiufeng
 */
@Component("wechatMobilePlugin")
public class WechatMobilePlugin extends AbstractPaymentPlugin implements IPaymentEvent, IEveryMinutesExecuteEvent {

	// 支付插件桩
	private PaymentPluginBundle paymentPluginBundle;
	
	@Override
	public String onCallBack(String ordertype) {
		// 获取支付配置参数相信
		Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());
		Map<String, String> map = new HashMap<>();
		String ordersn = "";
		try {
			Document document = new SAXReader().read(ThreadContextHolder.getHttpRequest().getInputStream());
			Map<String, String> params = WeixinUtil.xmlToMap(document);
			String return_code = params.get(WeixinUtil.RETURN_CODE);
			String result_code = params.get(WeixinUtil.RESULT_CODE);
			if (WeixinUtil.SUCCESS.equals(return_code) && WeixinUtil.SUCCESS.equals(result_code)) {
				ordersn = params.get("out_trade_no");
				String sign = WeixinUtil.createSign(params, cfgparams.get("key"));
				if (sign.equals(params.get("sign"))) {
					this.paySuccess(ordersn, params.get("transaction_id"), ordertype, BigDecimal.ZERO);
					map.put("return_code", WeixinUtil.SUCCESS);
					this.logger.debug("签名校验成功");
				} else {
					this.logger.debug("-----------签名校验失败---------");
					this.logger.debug("weixin sign:" + params.get("sign"));
					this.logger.debug("my sign:" + sign);
					map.put(WeixinUtil.RETURN_CODE, WeixinUtil.FAILURE);
					map.put(WeixinUtil.RETURN_MSG, WeixinUtil.FAILURE_MSG);
				}
			} else {
				map.put(WeixinUtil.RETURN_CODE, WeixinUtil.FAILURE);
				this.logger.debug("微信通知的结果为失败");
			}
		} catch (Exception e) {
			logger.error("确认支付失败" + ordersn + ", tradeno:" + ""
					+ ", totalFee" + BigDecimal.ZERO, e);
			map.put(WeixinUtil.RETURN_CODE, WeixinUtil.FAILURE);
			map.put(WeixinUtil.RETURN_MSG, "");
			e.printStackTrace();
		}
		ThreadContextHolder.getHttpResponse().setHeader("Content-Type", "text/xml");
		return WeixinUtil.mapToXml(map);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		String result = null;
		try {
			// 查询订单微信扫码二维码图片是否存在
			Order orderinfo = orderManager.get(order.getSn());
			if (StringUtils.isNoneBlank(orderinfo.getWx_pay_info())) {
				String qcodeImgUrl = orderinfo.getWx_pay_info();
				String qcodeImgName = qcodeImgUrl.substring(qcodeImgUrl.lastIndexOf("/") + 1, qcodeImgUrl.lastIndexOf("."));
				Date date = DateUtil.toDate(qcodeImgName, "yyyyMMddHHmmss");
				if (date.getTime() + 7000 < System.currentTimeMillis()) {
					StringBuilder sbHtml = new StringBuilder();
					sbHtml.append("<form id=\"wxpaysubmit\" name=\"wxpaysubmit\" action=\"/order_weixin_pay.html\" method=\"post\">");
					sbHtml.append("<input type=\"hidden\" name=\"wxpayqcode\" value=\"" + URLDecoder.decode(qcodeImgUrl, "UTF-8") + "\"/>");
					sbHtml.append("<input type=\"hidden\" name=\"order_id\" value=\"" + orderinfo.getOrder_id() + "\"/>");
					//submit按钮控件请不要含有name属性
			        sbHtml.append("<input type=\"submit\" value=\"确认\" style=\"display:none;\"></form>");
			        sbHtml.append("<script>document.forms['wxpaysubmit'].submit();</script>");
					result = sbHtml.toString();
					return result;
				}
			}
			Map<String, String> params = JSONObject.parseObject(payCfg.getConfig(), Map.class);
            Map<String, String> map = new HashMap<>();
    		map.put("appid", params.get("appid"));
    		map.put("mch_id", params.get("mchid"));
    		map.put("nonce_str", WeixinUtil.generateNonceStr());
    		map.put("notify_url", this.getCallBackUrl(payCfg, order));
    		map.put("out_trade_no", order.getSn());
    		map.put("spbill_create_ip", HttpUtils.getIpAddr(ThreadContextHolder.getHttpRequest()));
    		map.put("total_fee", String.valueOf(BigDecimal.valueOf(order.getNeedPayMoney()).multiply(BigDecimal.valueOf(100)).intValue()));
    		map.put("trade_type", WxPayTradeType.NATIVE.getType());
    		map.put("body", "国美国际酒业-订单支付");
    		map.put("product_id", order.getSn());
    		map.put("sign", WeixinUtil.createSign(map, params.get("key")));
			HttpResponse response = WeixinUtil.sendUnifiedOrderReq(map);
            HttpEntity entity = response.getEntity();
			if (entity != null) {
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(entity.getContent());
				Map<String, String> resultParams = WeixinUtil.xmlToMap(document);
				String return_code = resultParams.get(WeixinUtil.RETURN_CODE);
				String result_code = resultParams.get(WeixinUtil.RESULT_CODE);
				if (WeixinUtil.SUCCESS.equals(return_code) && WeixinUtil.SUCCESS.equals(result_code)) {
					String sign = WeixinUtil.createSign(resultParams, params.get("key"));
					if (sign.equals(resultParams.get("sign"))) {
						this.logger.debug("签名校验成功");
						this.logger.debug("微信支付成功");
						String prepayId = resultParams.get(WeixinUtil.PREPAY_ID);
						String qcodeUrl = resultParams.get(WeixinUtil.QCODE_URL);
						String qcodeImgUrl = WeixinUtil.buildQCodeReq(prepayId, qcodeUrl);
						if (qcodeImgUrl == null) return null;
						StringBuilder sbHtml = new StringBuilder();
						sbHtml.append("<form id=\"wxpaysubmit\" name=\"wxpaysubmit\" action=\"/order_weixin_pay.html\" method=\"post\">");
						sbHtml.append("<input type=\"hidden\" name=\"wxpayqcode\" value=\"" + URLDecoder.decode(qcodeImgUrl, "UTF-8") + "\"/>");
						sbHtml.append("<input type=\"hidden\" name=\"order_id\" value=\"" + orderinfo.getOrder_id() + "\"/>");
						//submit按钮控件请不要含有name属性
				        sbHtml.append("<input type=\"submit\" value=\"确认\" style=\"display:none;\"></form>");
				        sbHtml.append("<script>document.forms['wxpaysubmit'].submit();</script>");
						result = sbHtml.toString();
						
						// 更新订单的微信扫码支付图片信息
						this.daoSupport.execute("update es_order set wx_pay_info=? where sn=?", qcodeImgUrl, order.getSn());
						
					} else {
						this.logger.debug("-----------签名校验失败---------");
						this.logger.debug("weixin sign:" + resultParams.get("sign"));
						this.logger.debug("my sign:" + sign);
					}
				} else {
					String return_msg = resultParams.get("return_msg");
					if (StringUtils.isNotBlank(return_msg)) {
						this.logger.debug("return_msg: " + return_msg);
					}
					String err_code_des = resultParams.get("err_code_des");
					if (StringUtils.isNotBlank(err_code_des)) {
						this.logger.debug("err_code_des: " + err_code_des);
					}
				}
			}
        } catch (Exception e) {
        	e.printStackTrace();
			logger.error("微信支付请求失败" + order.getSn()
							+ ", tradeno:" + order.getTradeno() 
							+ ", totalFee:" + order.getNeedPayMoney());
        }
		return result;
	}
	
	@Override
	public String onReturn(String arg0) {
		// 微信支付无此处处理
		return null;
	}
	
	@Transactional(propagation = Propagation.REQUIRED)
	@SuppressWarnings("unchecked")
	@Override
	public String onRefund(BigDecimal refundPrice, PayCfg payCfg, PayEnable order) {
		Map<String, String> params = JSONObject.parseObject(payCfg.getConfig(), Map.class);
		
		BigDecimal needPayMoney = BigDecimal.valueOf(order.getNeedPayMoney());
		if (order.getParent_id() != null) {
			Order parentOrder = this.orderManager.get(order.getParent_id());
			if (parentOrder != null) {
				needPayMoney = BigDecimal.valueOf(parentOrder.getNeedPayMoney());
			}
		}
		Map<String, String> map = new HashMap<>();
		map.put("appid", params.get("appid"));
		map.put("mch_id", params.get("mchid"));
		map.put("nonce_str", WeixinUtil.generateNonceStr());
		if (StringUtils.isNotBlank(order.getTradeno())) {
			map.put("transaction_id", order.getTradeno());
		} else {
			map.put("out_trade_no", order.getSn());
		}
		map.put("out_refund_no", order.getRefund_batchno());
		map.put("total_fee", String.valueOf(needPayMoney.multiply(BigDecimal.valueOf(100)).intValue()));
		map.put("refund_fee", String.valueOf(refundPrice.multiply(BigDecimal.valueOf(100)).intValue()));
		map.put("op_user_id", params.get("mchid"));
		map.put("sign", WeixinUtil.createSign(map, params.get("key")));
		String message = "微信退款申请失败";
		boolean success = false;
		try {
			HttpResponse response = WeixinUtil.sendRefundReq(map);
            HttpEntity entity = response.getEntity();
			if (entity != null) {
				SAXReader saxReader = new SAXReader();
				Document document = saxReader.read(entity.getContent());
				logger.debug("------------------------退款申请结果（S）----------------------------");
				logger.info(entity.getContent());
				logger.debug("------------------------退款申请结果（E）----------------------------");
				Map<String, String> resultParams = WeixinUtil.xmlToMap(document);
				String return_code = resultParams.get(WeixinUtil.RETURN_CODE);
				String result_code = resultParams.get(WeixinUtil.RESULT_CODE);
				if (WeixinUtil.SUCCESS.equals(return_code) && WeixinUtil.SUCCESS.equals(result_code)) {
					String sign = WeixinUtil.createSign(resultParams, params.get("key"));
					if (sign.equals(resultParams.get("sign"))) {
						this.logger.debug("签名校验成功");
					} else {
						this.logger.debug("-----------签名校验失败---------");
						this.logger.debug("weixin sign:" + resultParams.get("sign"));
						this.logger.debug("my sign:" + sign);
						message = "签名校验失败";
					}
					this.logger.debug(message = "微信退款申请成功");
					success = true;
				} else {
					String return_msg = resultParams.get("return_msg");
					if (StringUtils.isNotBlank(return_msg)) {
						this.logger.debug("return_msg: " + return_msg);
					}
					String err_code_des = resultParams.get("err_code_des");
					if (StringUtils.isNotBlank(err_code_des)) {
						this.logger.debug("err_code_des: " + err_code_des);
					}
					this.logger.warn(message = "微信退款申请失败");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("微信退款申请失败" + order.getSn()
							+ ", tradeno:" + order.getTradeno() 
							+ ", totalFee:" + order.getNeedPayMoney()
							+ ", refundFee:" + refundPrice, e);
		}
		if (success) return message;
		throw new RuntimeException(message);
	}
	
    @Transactional(propagation = Propagation.REQUIRED)
	public void onRefundNotify(Order order, HttpResponse response, String key) throws Exception {
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			SAXReader saxReader = new SAXReader();
			Document document = saxReader.read(entity.getContent());
			Map<String, String> resultParams = WeixinUtil.xmlToMap(document);
			String return_code = resultParams.get(WeixinUtil.RETURN_CODE);
			String result_code = resultParams.get(WeixinUtil.RESULT_CODE);
			if (WeixinUtil.SUCCESS.equals(return_code) && WeixinUtil.SUCCESS.equals(result_code)) {
				String sign = WeixinUtil.createSign(resultParams, key);
				if (sign.equals(resultParams.get("sign"))) {
					this.logger.debug("签名校验成功");
					int refundCount = NumberUtils.toInt(resultParams.get("refund_count"), 0);
					String refund_batchno = null;
					for (int i = 0; i < refundCount; i++) {
						refund_batchno = resultParams.get("out_refund_no_" + i);
						Order dbOrder = (Order) this.daoSupport.queryForObject("select * from es_order where refund_batchno=?", Order.class, refund_batchno);
						if (dbOrder != null && !dbOrder.getSn().equals(order.getSn())) {
							continue;
						}
						this.daoSupport.execute("update es_order set refund_status=1 where sn=? AND (refund_status IS NULL OR refund_status!=1)", order.getSn());
						SellBackList sellBackList = sellBackManager.getLast(order.getSn());
						if (sellBackList != null && sellBackList.getRefund_time()==null) {
							this.daoSupport.execute("update es_sellback_list set refund_time=? where id=?", DateUtil.getDateline(), sellBackList.getId());
							this.sellBackManager.saveRefundLog(sellBackList.getId());
						}
					}
					this.logger.debug("微信退款成功");
				} else {
					this.logger.debug("-----------签名校验失败---------");
					this.logger.debug("weixin sign:" + resultParams.get("sign"));
					this.logger.debug("my sign:" + sign);
				}
			}
		}
    }
	
	@Override
	public String getId() {
		return "wechatMobilePlugin";
	}
	
	@Override
	public String getName() {
		return "微信移动支付接口";
	}
	
	public PaymentPluginBundle getPaymentPluginBundle() {
		return paymentPluginBundle;
	}
	
	public void setPaymentPluginBundle(PaymentPluginBundle paymentPluginBundle) {
		this.paymentPluginBundle = paymentPluginBundle;
	}

	/**
	 * 处理微信退款结果查询任务.
	 * <br/>每分钟执行一次.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void everyMinutes() {
		// 查询支付配置
		PayCfg payCfg = this.paymentManager.get(this.getId());
		Map<String, String> params = JSONObject.parseObject(payCfg.getConfig(), Map.class);
		// 查询已退款且未更新退款成功状态的微信支付订单
		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM es_order");
		sql.append(" WHERE payment_id=200 AND payment_type='wechatMobilePlugin'");
		sql.append(" AND status IN (16,17) AND (refund_status IS NULL OR refund_status!=1)");
		sql.append(" AND parent_id IS NOT NULL AND tradeno IS NOT NULL AND refund_batchno IS NOT NULL");
		try {
			List<StoreOrder> orders = this.daoSupport.queryForList(sql.toString(), StoreOrder.class);
			if (orders == null || orders.isEmpty()) return;
			Map<String, String> map = new HashMap<>();
			map.put("appid", params.get("appid"));
    		map.put("mch_id", params.get("mchid"));
			for (StoreOrder order : orders) {
				try {
					map.put("nonce_str", WeixinUtil.generateNonceStr());
					if (StringUtils.isNotBlank(order.getTradeno())) {
						map.put("transaction_id", order.getTradeno());
					} else if (StringUtils.isNotBlank(order.getRefund_batchno())) {
						map.put("out_refund_no", order.getRefund_batchno());
					} else {
						map.put("out_trade_no", order.getSn());
					}
					map.put("sign", WeixinUtil.createSign(map, params.get("key")));
					this.onRefundNotify(order, WeixinUtil.sendRefundQueryReq(map), params.get("key"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
