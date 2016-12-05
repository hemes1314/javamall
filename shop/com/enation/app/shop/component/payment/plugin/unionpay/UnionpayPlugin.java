package com.enation.app.shop.component.payment.plugin.unionpay;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.unionpay.acp.sdk.SDKConfig;

/**
 * 中国银联在线支付
 * @author xulipeng
 *	2015年09月03日13:29:34
 */
@Component("unPay")
public class UnionpayPlugin extends AbstractPaymentPlugin implements IPaymentEvent {
	
	public static String encoding = "UTF-8";

	/**
	 * 5.0.0
	 */
	public static String version = "5.0.0";
	
	public static int is_load=0;

	
	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
	 
		String html = getPayHtml(payCfg,order);
		return  html;
	} 

	@Override
	public String onCallBack(String ordertype) {
		
		try {
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String respCode = request.getParameter("respCode");	//应答码			参考：https://open.unionpay.com/ajweb/help/respCode/respCodeList
			String respMsg = request.getParameter("respMsg");	//应答信息
			String ordersn = request.getParameter("orderId");	//商户订单号
			String queryId = request.getParameter("queryId");	//流水号
			String tradeno = request.getParameter("traceNo");	//系统追踪号
			
			if(respCode.equals("00")){	//交易成功
				this.paySuccess(ordersn, tradeno, ordertype, BigDecimal.ZERO);
				return ordersn;
				
			}else{
				throw new RuntimeException("验证失败，错误信息:"+respMsg);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("验证失败");
		}
	}

	@Override
	public String onReturn(String ordertype) {
		try {
			
			HttpServletRequest request = ThreadContextHolder.getHttpRequest();
			String respCode = request.getParameter("respCode");	//应答码			参考：https://open.unionpay.com/ajweb/help/respCode/respCodeList
			String respMsg = request.getParameter("respMsg");	//应答信息
			String ordersn = request.getParameter("orderId");	//商户订单号
			String queryId = request.getParameter("queryId");	//流水号
			String tradeno = request.getParameter("traceNo");	//系统追踪号
			
			if(respCode.equals("00")){	//交易成功
				this.paySuccess(ordersn, tradeno, ordertype,BigDecimal.ZERO);
				return ordersn;
				
			}else{
				throw new RuntimeException("验证失败，错误信息:"+respMsg);
			}
			
		} catch (Exception e) {
			throw new RuntimeException("验证失败");
		}
		
	}

	@Override
	public String getId() {
		return "unPay";
	}

	@Override
	public String getName() {
		return "中国银联支付";
	}
	
	/**
	 * 获取付款的html
	 * @param payCfg
	 * @param order
	 * @return
	 */
	public String getPayHtml(PayCfg payCfg, PayEnable order){
		if(is_load==0){
			SDKConfig.getConfig().loadPropertiesFromPath(Thread.currentThread().getContextClassLoader().getResource("").getPath()+"com/enation/app/shop/component/payment/plugin/unionpay/");
			is_load=1;
		}
		
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
		String merId = params.get("merId");
		/**
		 * 组装请求报文
		 */
		Map<String, String> data = new HashMap<String, String>();
		// 版本号
		data.put("version", "5.0.0");
		// 字符集编码 默认"UTF-8"
		data.put("encoding", "UTF-8");
		// 签名方法 01 RSA
		data.put("signMethod", "01");
		// 交易类型 01-消费
		data.put("txnType", "01");
		// 交易子类型 01:自助消费 02:订购 03:分期付款
		data.put("txnSubType", "01");
		// 业务类型
		data.put("bizType", "000201");
		// 渠道类型，07-PC，08-手机
		data.put("channelType", "07");
		// 前台通知地址 ，控件接入方式无作用
		data.put("frontUrl", this.getReturnUrl(payCfg, order));
		// 后台通知地址
		data.put("backUrl", this.getCallBackUrl(payCfg, order));
		// 接入类型，商户接入填0 0- 商户 ， 1： 收单， 2：平台商户
		data.put("accessType", "0");
		// 商户号码，请改成自己的商户号
		data.put("merId", merId);
		// 商户订单号，8-40位数字字母
		data.put("orderId", order.getSn());
		// 订单发送时间，取系统时间
		data.put("txnTime", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		// 交易金额，单位分
		data.put("txnAmt", "1");
		// 交易币种
		data.put("currencyCode", "156");
		// 请求方保留域，透传字段，查询、通知、对账文件中均会原样出现
		// data.put("reqReserved", "透传信息");
		// 订单描述，可不上送，上送时控件中会显示该信息
		// data.put("orderDesc", "订单描述");

		Map<String, String> submitFromData = DemoBase.signData(data);
		
		// 交易请求url 从配置文件读取
		String requestFrontUrl = SDKConfig.getConfig().getFrontRequestUrl();
		
		/**
		 * 创建表单
		 */
		String html = DemoBase.createHtml(requestFrontUrl, submitFromData);
		return html;
	}
	
	@Override
	public String onRefund(BigDecimal refundPrice, PayCfg payCfg, PayEnable order) {
		return "";
	}
}
