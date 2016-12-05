package com.enation.app.shop.component.payment.plugin.bill;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import com.enation.app.shop.component.payment.plugin.bill.encrypt.MD5Util;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.context.webcontext.ThreadContextHolder;

/**
 * 快钱人民币支付
 * @author xiaokx
 *
 */
@Component
public class BillPlugin extends AbstractPaymentPlugin implements IPaymentEvent {
	
	private IPaymentManager paymentManager;



	@Override
	public String getId() {
		
		return "billPlugin";
	}

	@Override
	public String getName() {
		
		return "快钱人民币支付";
	}

	
	/**
	 * 功能函数。将变量值不为空的参数组成字符串
	 * @param returnStr
	 * @param paramId
	 * @param paramValue
	 * @return
	 */
	public String appendParam(String returnStr,String paramId,String paramValue)
	{
			if(!returnStr.equals(""))
			{
				if(!paramValue.equals(""))
				{
					returnStr=returnStr+"&"+paramId+"="+paramValue;
				}
			}
			else
			{
				if(!paramValue.equals(""))
				{
				returnStr=paramId+"="+paramValue;
				}
			}	
			return returnStr;
	}

	@Override
	public String onPay(PayCfg payCfg, PayEnable order) {
		
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
		String partner =params.get("partner");
		
		//人民币网关密钥
		///区分大小写.请与快钱联系索取
		String key =  params.get("key");

		String show_url = this.getShowUrl(order);
		String notify_url = this.getCallBackUrl(payCfg,order);
		String return_url =this.getReturnUrl(payCfg,order);
		
		//人民币网关账户号
		///请登录快钱系统获取用户编号，用户编号后加01即为人民币网关账户号。
		String merchantAcctId=partner;

		
		//字符集.固定选择值。可为空。
		///只能选择1、2、3.
		///1代表UTF-8; 2代表GBK; 3代表gb2312
		///默认值为1
		String inputCharset="1";


		//服务器接受支付结果的后台地址.与[pageUrl]不能同时为空。必须是绝对地址。
		///快钱通过服务器连接的方式将交易结果发送到[bgUrl]对应的页面地址，在商户处理完成后输出的<result>如果为1，页面会转向到<redirecturl>对应的地址。
		///如果快钱未接收到<redirecturl>对应的地址，快钱将把支付结果GET到[pageUrl]对应的页面。
		String bgUrl=return_url;
			
		//网关版本.固定值
		///快钱会根据版本号来调用对应的接口处理程序。
		///本代码版本号固定为v2.0
		String version="v2.0";

		//语言种类.固定选择值。
		///只能选择1、2、3
		///1代表中文；2代表英文
		///默认值为1
		String language="1";

		//签名类型.固定值
		///1代表MD5签名
		///当前版本固定为1
		String signType="1";
		   
		//支付人姓名
		///可为中文或英文字符
		String payerName="payerName";

		//支付人联系方式类型.固定选择值
		///只能选择1
		///1代表Email
		String payerContactType="1";

		//支付人联系方式
		///只能选择Email或手机号
		String payerContact="";

		//商户订单号
		///由字母、数字、或[-][_]组成
		String orderId=order.getSn();

		//订单金额
		///以分为单位，必须是整型数字
		///比方2，代表0.02元
		double oa = order.getNeedPayMoney()*100;
		String orderAmount=String.valueOf((int)oa);
			
		//订单提交时间
		///14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		///如；20080101010101
		String orderTime=new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date());

		//商品名称
		///可为中文或英文字符
		String productName= "订单:" + order.getSn(); 

		//商品数量
		///可为空，非空时必须为数字
		String productNum="1";

		//商品代码
		///可为字符或者数字
		String productId="";

		//商品描述
		String productDesc="";
			
		//扩展字段1
		///在支付结束后原样返回给商户
		String ext1="";

		//扩展字段2
		///在支付结束后原样返回给商户
		String ext2="";
			
		//支付方式.固定选择值
		///只能选择00、10、11、12、13、14
		///00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）
		String payType="00";


		//同一订单禁止重复提交标志
		///固定选择值： 1、0
		///1代表同一订单号只允许提交1次；0表示同一订单号在没有支付成功的前提下可重复提交多次。默认为0建议实物购物车结算类商户采用0；虚拟产品类商户采用1
		String redoFlag="0";

		//快钱的合作伙伴的账户号
		///如未和快钱签订代理合作协议，不需要填写本参数
		String pid="";


		//生成加密签名串
		///请务必按照如下顺序和规则组成加密串！
		String signMsgVal="";
		signMsgVal=appendParam(signMsgVal,"inputCharset",inputCharset);
		signMsgVal=appendParam(signMsgVal,"bgUrl",bgUrl);
		signMsgVal=appendParam(signMsgVal,"version",version);
		signMsgVal=appendParam(signMsgVal,"language",language);
		signMsgVal=appendParam(signMsgVal,"signType",signType);
		signMsgVal=appendParam(signMsgVal,"merchantAcctId",merchantAcctId);
		signMsgVal=appendParam(signMsgVal,"payerName",payerName);
		signMsgVal=appendParam(signMsgVal,"payerContactType",payerContactType);
		signMsgVal=appendParam(signMsgVal,"payerContact",payerContact);
		signMsgVal=appendParam(signMsgVal,"orderId",orderId);
		signMsgVal=appendParam(signMsgVal,"orderAmount",orderAmount);
		signMsgVal=appendParam(signMsgVal,"orderTime",orderTime);
		signMsgVal=appendParam(signMsgVal,"productName",productName);
		signMsgVal=appendParam(signMsgVal,"productNum",productNum);
		signMsgVal=appendParam(signMsgVal,"productId",productId);
		signMsgVal=appendParam(signMsgVal,"productDesc",productDesc);
		signMsgVal=appendParam(signMsgVal,"ext1",ext1);
		signMsgVal=appendParam(signMsgVal,"ext2",ext2);
		signMsgVal=appendParam(signMsgVal,"payType",payType);
		signMsgVal=appendParam(signMsgVal,"redoFlag",redoFlag);
		signMsgVal=appendParam(signMsgVal,"pid",pid);
		signMsgVal=appendParam(signMsgVal,"key",key);

		try {
			String signMsg=MD5Util.md5Hex(signMsgVal.getBytes("utf-8")).toUpperCase();
			String strHtml = "";
			strHtml+="<form name=\"kqPay\" id=\"kqPay\" action=\"https://www.99bill.com/gateway/recvMerchantInfoAction.htm\" method=\"post\">";
			strHtml+="<input type=\"hidden\" name=\"inputCharset\" value=\""+inputCharset+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"bgUrl\" value=\""+bgUrl+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"version\" value=\""+version+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"language\" value=\""+language+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"signType\" value=\""+signType+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"signMsg\" value=\""+signMsg+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"merchantAcctId\" value=\""+merchantAcctId+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"payerName\" value=\""+payerName+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"payerContactType\" value=\""+payerContactType+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"payerContact\" value=\""+payerContact+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"orderId\" value=\""+orderId+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"orderAmount\" value=\""+orderAmount+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"orderTime\" value=\""+orderTime+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"productName\" value=\""+productName+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"productNum\" value=\""+productNum+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"productId\" value=\""+productId+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"productDesc\" value=\""+productDesc+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"ext1\" value=\""+ext1+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"ext2\" value=\""+ext2+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"payType\" value=\""+payType+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"redoFlag\" value=\""+redoFlag+"\"/>";
			strHtml+="<input type=\"hidden\" name=\"pid\" value=\""+pid+"\"/>";
//			strHtml+="<input type=\"submit\" name=\"submit\" value=\"提交到快钱\">";
			strHtml+="</form>";
			strHtml+="<script type=\"text/javascript\">document.forms['kqPay'].submit();</script>";
			return strHtml; 
			
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "验证串失败";
		}
	}

	@Override
	public String onCallBack(String ordertype) {
		return null;
	}

	@Override
	public String onRefund(BigDecimal refundPrice, PayCfg payCfg, PayEnable order) {
		return "";
	}

	@Override
	public String onReturn(String ordertype) {
		HttpServletRequest request  =  ThreadContextHolder.getHttpRequest();
		//获取人民币网关账户号
		String merchantAcctId=(String)request.getParameter("merchantAcctId").trim();
		
		Map<String,String> params = paymentManager.getConfigParams(this.getId());
		//设置人民币网关密钥
		///区分大小写
		String key= params.get("key");

		//获取网关版本.固定值
		///快钱会根据版本号来调用对应的接口处理程序。
		///本代码版本号固定为v2.0
		String version=(String)request.getParameter("version").trim();

		//获取语言种类.固定选择值。
		///只能选择1、2、3
		///1代表中文；2代表英文
		///默认值为1
		String language=(String)request.getParameter("language").trim();

		//签名类型.固定值
		///1代表MD5签名
		///当前版本固定为1
		String signType=(String)request.getParameter("signType").trim();

		//获取支付方式
		///值为：10、11、12、13、14
		///00：组合支付（网关支付页面显示快钱支持的各种支付方式，推荐使用）10：银行卡支付（网关支付页面只显示银行卡支付）.11：电话银行支付（网关支付页面只显示电话支付）.12：快钱账户支付（网关支付页面只显示快钱账户支付）.13：线下支付（网关支付页面只显示线下支付方式）.14：B2B支付（网关支付页面只显示B2B支付，但需要向快钱申请开通才能使用）
		String payType=(String)request.getParameter("payType").trim();

		//获取银行代码
		///参见银行代码列表
		String bankId=(String)request.getParameter("bankId").trim();

		//获取商户订单号
		String orderId=(String)request.getParameter("orderId").trim();
		logger.debug("快钱 return-----------orderId----------"+orderId);
		//获取订单提交时间
		///获取商户提交订单时的时间.14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		///如：20080101010101
		String orderTime=(String)request.getParameter("orderTime").trim();

		//获取原始订单金额
		///订单提交到快钱时的金额，单位为分。
		///比方2 ，代表0.02元
		String orderAmount=(String)request.getParameter("orderAmount").trim();

		//获取快钱交易号
		///获取该交易在快钱的交易号
		String dealId=(String)request.getParameter("dealId").trim();

		//获取银行交易号
		///如果使用银行卡支付时，在银行的交易号。如不是通过银行支付，则为空
		String bankDealId=(String)request.getParameter("bankDealId").trim();

		//获取在快钱交易时间
		///14位数字。年[4位]月[2位]日[2位]时[2位]分[2位]秒[2位]
		///如；20080101010101
		String dealTime=(String)request.getParameter("dealTime").trim();

		//获取实际支付金额
		///单位为分
		///比方 2 ，代表0.02元
		String payAmount=(String)request.getParameter("payAmount").trim();

		//获取交易手续费
		///单位为分
		///比方 2 ，代表0.02元
		String fee=(String)request.getParameter("fee").trim();

		//获取扩展字段1
		String ext1=(String)request.getParameter("ext1").trim();

		//获取扩展字段2
		String ext2=(String)request.getParameter("ext2").trim();

		//获取处理结果
		///10代表 成功11代表 失败
		String payResult=(String)request.getParameter("payResult").trim();
		logger.debug("快钱 return---------payResult------------"+payResult);
		//获取错误代码
		///详细见文档错误代码列表
		String errCode=(String)request.getParameter("errCode").trim();

		//获取加密签名串
		String signMsg=(String)request.getParameter("signMsg").trim();
		logger.debug("快钱 return----------signMsg-----------"+signMsg);


		//生成加密串。必须保持如下顺序。
			String merchantSignMsgVal="";
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"merchantAcctId",merchantAcctId);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"version",version);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"language",language);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"signType",signType);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"payType",payType);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"bankId",bankId);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderId",orderId);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderTime",orderTime);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"orderAmount",orderAmount);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"dealId",dealId);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"bankDealId",bankDealId);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"dealTime",dealTime);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"payAmount",payAmount);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"fee",fee);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"ext1",ext1);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"ext2",ext2);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"payResult",payResult);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"errCode",errCode);
			merchantSignMsgVal=appendParam(merchantSignMsgVal,"key",key);

		String merchantSignMsg="";
		try {
			merchantSignMsg = MD5Util.md5Hex(merchantSignMsgVal.getBytes("utf-8")).toUpperCase();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			logger.error("快钱支付验证串失败");
			e.printStackTrace();
		}


		//初始化结果及地址
		int rtnOk=0;
		String rtnUrl="";
		
		//商家进行数据处理，并跳转会商家显示支付结果的页面
		///首先进行签名字符串验证
		if(signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())){
			
			///接着进行支付结果判断
			switch (NumberUtils.toInt(payResult)) {
			
				  case 10:
					
					//*  
					// 商户网站逻辑处理，比方更新订单支付状态为成功
					// 特别注意：只有signMsg.toUpperCase().equals(merchantSignMsg.toUpperCase())，且payResult=10，才表示支付成功！同时将订单金额与提交订单前的订单金额进行对比校验。
					//*
					
					//报告给快钱处理结果，并提供将要重定向的地址。
					rtnOk=1;
					rtnUrl="member_orderdetail_"+orderId+".html";
					this.paySuccess(orderId, dealId,ordertype, BigDecimal.ZERO);
					return orderId;
				  
				 default:

					rtnOk=1;
					rtnUrl="member_orderdetail_"+orderId+".html";
					this.paySuccess(orderId, dealId,ordertype,BigDecimal.ZERO);
					return orderId;

			}

		}else{

			rtnOk=1;
			rtnUrl="";
			logger.debug("onReturn in............. fail");
			throw new RuntimeException("验证失败");  

		}
	}



	public IPaymentManager getPaymentManager() {
		return paymentManager;
	}

	public void setPaymentManager(IPaymentManager paymentManager) {
		this.paymentManager = paymentManager;
	}

}
