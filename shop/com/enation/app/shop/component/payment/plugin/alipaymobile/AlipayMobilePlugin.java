package com.enation.app.shop.component.payment.plugin.alipaymobile;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipaySubmit;
import com.enation.app.shop.component.payment.plugin.alipaymobile.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipaymobile.util.AlipayNotify;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.plugin.payment.PaymentPluginBundle;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

@Component("alipayMobilePlugin")
public class AlipayMobilePlugin extends AbstractPaymentPlugin implements IPaymentEvent{
	
	//支付插件桩
	private PaymentPluginBundle paymentPluginBundle;
		
	@SuppressWarnings("rawtypes")
	@Override
	public String onCallBack(String ordertype) {
		Map<String,String> cfgparams = paymentManager.getConfigParams(this.getId());
		
		AlipayConfig.partner = cfgparams.get("partner");
		AlipayConfig.ali_public_key = cfgparams.get("rsa_public");
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		 
		//获取支付宝POST过来反馈信息
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1) ? valueStr + values[i]
						: valueStr + values[i] + ",";
			}
			//乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
			//valueStr = StringUtil.toUTF8(valueStr);
			params.put(name, valueStr);
		}
		
		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
		//商户订单号
		 String out_trade_no = StringUtil.toUTF8(request.getParameter("out_trade_no") );
		//支付宝交易号

		String trade_no =StringUtil.toUTF8(request.getParameter("trade_no") );// new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

		//交易状态
		String trade_status = StringUtil.toUTF8(request.getParameter("trade_status") );//new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

		BigDecimal totalFee = new BigDecimal(StringUtil.toUTF8(request.getParameter("total_fee")));//交易金额

		//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//

		if(AlipayNotify.verify(params)){//验证成功
			//////////////////////////////////////////////////////////////////////////////////////////
			//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
			if(trade_status.equals("TRADE_FINISHED")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
			    //请在这里加上商户的业务逻辑程序代码
				//注意：
				//退款日期超过可退款期限后（如三个月可退款），支付宝系统发送该交易状态通知
			} else if (trade_status.equals("TRADE_SUCCESS")){
				//判断该笔订单是否在商户网站中已经做过处理
					//如果没有做过处理，根据订单号（out_trade_no）在商户网站的订单系统中查到该笔订单的详细，并执行商户的业务程序
					//如果有做过处理，不执行商户的业务程序
				try {
			        this.paySuccess(out_trade_no,trade_no, ordertype,totalFee);    
				} catch (Exception e) {
					logger.error("确认支付失败" + out_trade_no + ", tradeno:" + trade_no
							+ ", totalFee" + totalFee, e);
					throw e;
				}
				//注意：
				//付款完成后，支付宝系统发送该交易状态通知
			}
			//——请根据您的业务逻辑来编写程序（以上代码仅作参考）——
			return ("success");	//请不要修改或删除
			//////////////////////////////////////////////////////////////////////////////////////////
		}else{//验证失败
			return ("fail");
		}
	}
	
	@Override
	public String onRefund(BigDecimal refundPrice, PayCfg payCfg, PayEnable order) {
		return null;
	}
	
	@Override
	public String onPay(PayCfg arg0, PayEnable arg1) {
		return null;
	}
	
	@Override
	public String onReturn(String arg0) {
		return null;
	}
	
	@Override
	public String getId() {
		return "alipayMobilePlugin";
	}
	
	@Override
	public String getName() {
		return "支付宝移动支付接口";
	}

	public PaymentPluginBundle getPaymentPluginBundle() {
		return paymentPluginBundle;
	}

	public void setPaymentPluginBundle(PaymentPluginBundle paymentPluginBundle) {
		this.paymentPluginBundle = paymentPluginBundle;
	}
	

}
