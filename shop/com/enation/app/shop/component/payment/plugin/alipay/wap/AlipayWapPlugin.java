package com.enation.app.shop.component.payment.plugin.alipay.wap;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.alipay.JavashopAlipayUtil;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipaySubmit;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.eop.resource.model.EopSite;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;

@Component("alipayWapPlugin")
public class AlipayWapPlugin extends AbstractPaymentPlugin  implements IPaymentEvent{

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		try {
			Map<String,String> params = paymentManager.getConfigParams(this.getId());
			// 卖家支付宝帐户
			AlipayConfig.key = params.get("key");
			AlipayConfig.partner = params.get("partner");
			String out_trade_no = order.getSn(); // 商户网站订单
			String content_encoding = params.get("content_encoding"); 

			String payment_type = "1";
			//必填，不能修改
			//服务器异步通知页面路径
			String notify_url =this.getCallBackUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数

			//页面跳转同步通知页面路径
			String return_url = this.getReturnWapUrl(payCfg, order);
			//需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/
			
			String show_url= this.getShowUrl(order);
			
			//商户订单号
			out_trade_no = new String( out_trade_no.getBytes("ISO-8859-1"),"UTF-8")  ;
			//商户网站订单系统中唯一订单号，必填

			String sitename = EopSite.getInstance().getSitename();
			//订单名称
			String subject =sitename+"订单";
			if(!StringUtil.isEmpty(content_encoding)){
				 subject = new String(subject.getBytes("ISO-8859-1"),content_encoding);
			}
			//必填

			String body =  ("订单："+out_trade_no);
			if(!StringUtil.isEmpty(content_encoding)){
				body=new String( body.getBytes("ISO-8859-1"),content_encoding);
			}
			
			//付款金额
			String price = new String(String.valueOf( order.getNeedPayMoney()).getBytes("ISO-8859-1"),"UTF-8");
			
			//把请求参数打包成数组
			Map<String, String> sParaTemp = new HashMap<String, String>();
			sParaTemp.put("service", "alipay.wap.create.direct.pay.by.user");
	        sParaTemp.put("partner", AlipayConfig.partner);
	        sParaTemp.put("seller_id", AlipayConfig.partner);
	        sParaTemp.put("sign_type", AlipayConfig.sign_type = "RSA");
	        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
			sParaTemp.put("payment_type", "1");
			sParaTemp.put("notify_url", notify_url);
			sParaTemp.put("return_url", return_url);
			sParaTemp.put("out_trade_no", out_trade_no);
			sParaTemp.put("subject", subject);
			sParaTemp.put("total_fee", price);
			sParaTemp.put("show_url", show_url);
			sParaTemp.put("body", body);
			sParaTemp.put("it_b_pay", "");
			sParaTemp.put("extern_token", "");
			String sHtmlText = AlipaySubmit.buildRequest(sParaTemp,"get","确认");
			return sHtmlText;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String onCallBack(String ordertype) {
		String out_trade_no = "";
    	String trade_no = "";
    	BigDecimal totalFee = BigDecimal.ZERO;
		try {
			Map<String,String> paramscfg = paymentManager.getConfigParams(this.getId());
			AlipayConfig.key = paramscfg.get("key");
			AlipayConfig.partner = paramscfg.get("partner");
			String param_encoding = paramscfg.get("param_encoding");
			
			HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		 
			//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
			//商户订单号
			out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//支付宝交易号
			trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

			//交易状态
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"),"UTF-8");

			totalFee = new BigDecimal(new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8"));//交易金额
			
			if(JavashopAlipayUtil.verify(param_encoding)){//验证成功
				//////////////////////////////////////////////////////////////////////////////////////////
//				System.out.println("校验成功");
				//请在这里加上商户的业务逻辑程序代码
				this.paySuccess(out_trade_no,trade_no, ordertype,totalFee);
				//——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
				
				if(trade_status.equals("TRADE_FINISHED")||trade_status.equals("TRADE_SUCCESS")){
					this.logger.info("异步校验订单["+out_trade_no+"]成功");
					return ("success");	//请不要修改或删除
					
				}else {
					this.logger.info("异步校验订单["+out_trade_no+"]成功");
					return ("success");	//请不要修改或删除
				}
			}else{//验证失败
				this.logger.info("异步校验订单["+out_trade_no+"]失败");
				return ("fail");
			}
		 
		} catch (Exception e) {
			logger.error("确认支付失败" + out_trade_no + ", tradeno:" + trade_no
					+ ", totalFee" + totalFee, e);
			e.printStackTrace();
			return ("fail");
		}
	}

	@Override
	public String onReturn(String ordertype) {
			try {
				Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());
				AlipayConfig.key = cfgparams.get("key");
				AlipayConfig.partner = cfgparams.get("partner");
				String param_encoding = cfgparams.get("param_encoding");

				//获取支付宝GET过来反馈信息
				HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();

				//获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
				//商户订单号
				String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"),"UTF-8");

				//支付宝交易号
				String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"),"UTF-8");

				BigDecimal totalFee = new BigDecimal(new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8"));//交易金额
				
				//计算得出通知验证结果
				boolean verify_result = JavashopAlipayUtil.verify(param_encoding);
				if(verify_result){
					this.paySuccess(out_trade_no,trade_no,ordertype,totalFee);
					this.logger.info("同步校验订单["+out_trade_no+"]成功");
					return out_trade_no;	
				}else{
					this.logger.info("同步校验订单失败");
					throw new RuntimeException("验证失败");  
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				this.logger.info("异步校验订单失败");
				throw new RuntimeException("验证失败",e);  
			}
	}

	@Override
	public String onRefund(BigDecimal refundPrice, PayCfg payCfg, PayEnable order) {
		return null;
	}

	@Override
	public String getId() {
		return "alipayWapPlugin";
	}

	@Override
	public String getName() {
		return "支付宝Wap支付接口";
	}

}
