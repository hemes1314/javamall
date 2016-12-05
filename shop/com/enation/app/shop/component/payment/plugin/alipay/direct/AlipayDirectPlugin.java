package com.enation.app.shop.component.payment.plugin.alipay.direct;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.enation.app.shop.component.payment.plugin.alipay.JavashopAlipayUtil;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.config.AlipayConfig;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipayNotify;
import com.enation.app.shop.component.payment.plugin.alipay.sdk33.util.AlipaySubmit;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.model.SellBackList;
import com.enation.app.shop.core.plugin.payment.AbstractPaymentPlugin;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 * 支付宝即时到账插件
 *
 * @author kingapex
 */
@Component
public class AlipayDirectPlugin extends AbstractPaymentPlugin implements IPaymentEvent {

    private Log log = LogFactory.getLog(AlipayDirectPlugin.class);

    /**
     * 支付宝 回调
     *
     * @param ordertype 订单类型，standard 标准订单，credit:信用账户充值
     * @return
     */
    @Override
    public String onCallBack(String ordertype) {
    	String out_trade_no = "";
    	String trade_no = "";
    	BigDecimal totalFee = BigDecimal.ZERO;
        try {
            Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());
            String key = cfgparams.get("key");
            String partner = cfgparams.get("partner");
            AlipayConfig.key = key;
            AlipayConfig.partner = partner;
            String param_encoding = cfgparams.get("param_encoding");
            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            // 商户订单号
            out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 支付宝交易号
            trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            totalFee = new BigDecimal(new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8"));//交易金额
            if (JavashopAlipayUtil.verify(param_encoding)) {// 验证成功
                this.paySuccess(out_trade_no, trade_no, ordertype,totalFee);
                if (trade_status.equals("TRADE_FINISHED")) {

                } else if (trade_status.equals("TRADE_SUCCESS")) {
                }
                return ("success"); // 请不要修改或删除
            } else {// 验证失败
                return ("fail");
            }
        } catch (Exception e) {
        	logger.error("确认支付失败" + out_trade_no + ", tradeno:" + trade_no
					+ ", totalFee" + totalFee, e);
            return ("fail");
        }
    }

    @SuppressWarnings("rawtypes")
    @Transactional(propagation = Propagation.REQUIRED)
	public String onRefundNotify(String ordertype) {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        //获取支付宝POST过来反馈信息
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = request.getParameterMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext(); ) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            //valueStr = new String(valueStr.getBytes("ISO-8859-1"), "gbk");
            params.put(name, valueStr);
        }

        //获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
        //批次号
        try {
            // 交易流水号
            String batch_no = new String(request.getParameter("batch_no").getBytes("ISO-8859-1"), "UTF-8");
            // 批量退款数据中转账成功的笔数
            String success_num = new String(request.getParameter("success_num").getBytes("ISO-8859-1"), "UTF-8");
            // 批量退款数据中的详细信息
            String result_details = new String(request.getParameter("result_details").getBytes("ISO-8859-1"), "UTF-8");
            // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)
            System.out.println("onRefundNotify: batch_no=" + batch_no + ", success_num=" + success_num + ", details=" + result_details);

            if (AlipayNotify.verify(params)) {//验证成功
                //////////////////////////////////////////////////////////////////////////////////////////
                //请在这里加上商户的业务逻辑程序代码

                //——请根据您的业务逻辑来编写程序（以下代码仅作参考）——
                //判断是否在商户网站中已经做过了这次通知返回的处理
                //如果没有做过处理，那么执行商户的业务程序
                //如果有做过处理，那么不执行商户的业务程序
            	Integer successNum = NumberUtils.toInt(success_num, 0);
            	Integer refundStatus = 2;
            	// 如果交易成功笔数为0
            	if (successNum == 0) {
            		// 判定是否交易已关闭，交易已关闭时也认为退款已成功（可能支付宝退款成功后第一次回调没成功导致），退款状态设为1
            		String[] ds = result_details.split("\\^");
            		if (ds.length == 3 && ds[2].equalsIgnoreCase("TRADE_HAS_CLOSED")) {
            			refundStatus = 1;
            		}
            	} else {
            		refundStatus = 1;
            	}
            	this.daoSupport.execute("update es_order set refund_status=? where refund_batchno=? and (refund_status IS NULL OR refund_status!=1)", refundStatus, batch_no);
            	if (refundStatus == 1) {
            		Order order = (Order) this.daoSupport.queryForObject("select * from es_order where refund_batchno=?", Order.class, batch_no);
            		SellBackList sellBackList = sellBackManager.getLast(order.getSn());
            		if (sellBackList != null && sellBackList.getRefund_time()==null) {
    					this.daoSupport.execute("update es_sellback_list set refund_time=? where id=?", DateUtil.getDateline(), sellBackList.getId());
    					this.sellBackManager.saveRefundLog(sellBackList.getId());
    				}
            	}
            	
                System.out.println("onRefundNotify verify successful.");
                return ("success"); //请不要修改或删除

                //——请根据您的业务逻辑来编写程序（以上代码仅作参考）——

                //////////////////////////////////////////////////////////////////////////////////////////
            } else {//验证失败
                System.out.println("onRefundNotify verify failed.");
                return ("fail");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return ("fail");
        }
    }


    /**
     * 支付宝  支付
     *
     * @param payCfg 支付方式相应配置
     * @param order  可支付的对象 订单
     * @return
     */
    @Override
    public String onPay(PayCfg payCfg, PayEnable order) {
        try {
            Map<String, String> params = paymentManager.getConfigParams(this.getId());
            String out_trade_no = order.getSn(); // 商户网站订单
            String partner = params.get("partner");  // 支付宝合作伙伴id (账户内提取)
            String key = params.get("key");  // 支付宝安全校验码(账户内提取)
            String seller_email = params.get("seller_email"); // 卖家支付宝帐户
            String content_encoding = params.get("content_encoding"); // 卖家支付宝帐户

            AlipayConfig.key = key;
            AlipayConfig.partner = partner;
            AlipayConfig.seller_email = seller_email;

            String payment_type = "1";
            //必填，不能修改
            //服务器异步通知页面路径
            String notify_url = this.getCallBackUrl(payCfg, order);
            //需http://格式的完整路径，不能加?id=123这类自定义参数
            //String notify_url = "http://zues.5166.info:20589/api/shop/b_alipayDirectPlugin_payment-callback.do";

            //页面跳转同步通知页面路径
            String return_url = this.getReturnUrl(payCfg, order);
            //String return_url = "http://zues.5166.info:20589/b_alipayDirectPlugin_payment-result.html";
            //需http://格式的完整路径，不能加?id=123这类自定义参数，不能写成http://localhost/

            String show_url = this.getShowUrl(order);
            //String show_url = "http://zues.5166.info:20589/b_145439696317.html";

            //商户订单号
            out_trade_no = new String(out_trade_no.getBytes("ISO-8859-1"), "UTF-8");
            //商户网站订单系统中唯一订单号，必填

            
            //订单名称
            //String sitename = EopSite.getInstance().getSitename();
            //String ordertype = order.getOrderType();
            //String subject =sitename+(ordertype.equals("topup") ? "账户充值" : (ordertype.equals("giftcard") ? "礼品卡" : "订单"));
            String subject = "GomeCellar";
            if (!StringUtil.isEmpty(content_encoding)) {
                subject = new String(subject.getBytes("ISO-8859-1"), content_encoding);
            }


            String body = ("sn:" + out_trade_no);
            if (!StringUtil.isEmpty(content_encoding)) {
                body = new String(body.getBytes("ISO-8859-1"), content_encoding);
            }
            //付款金额
            String price = new String(String.valueOf(order.getNeedPayMoney()).getBytes("ISO-8859-1"), "UTF-8");


            //把请求参数打包成数组
            Map<String, String> sParaTemp = new HashMap<String, String>();
            sParaTemp.put("service", "create_direct_pay_by_user");
            sParaTemp.put("partner", AlipayConfig.partner);
            sParaTemp.put("seller_email", AlipayConfig.seller_email);
            sParaTemp.put("sign_type", AlipayConfig.sign_type = "MD5");
            sParaTemp.put("_input_charset", AlipayConfig.input_charset);
            sParaTemp.put("payment_type", payment_type);
            sParaTemp.put("notify_url", notify_url);
            sParaTemp.put("return_url", return_url);
            sParaTemp.put("out_trade_no", out_trade_no);
            sParaTemp.put("subject", subject);
            sParaTemp.put("total_fee", price);
            sParaTemp.put("body", body);
            sParaTemp.put("show_url", show_url);
            sParaTemp.put("anti_phishing_key", "");
            sParaTemp.put("exter_invoke_ip", "");

            return AlipaySubmit.buildRequest(sParaTemp, "POST", "确认");
        } catch (Exception e) {
            return "转码失败";
        }

    }

    @Override
    public String onReturn(String ordertype) {
        try {
            log.info("支付宝回调开始:");
            if(paymentManager != null) {
                log.info("paymentManager注入成功" + paymentManager);
            }
            Map<String, String> cfgparams = paymentManager.getConfigParams(this.getId());
            String key = cfgparams.get("key");
            String partner = cfgparams.get("partner");

            log.info("key:" + key);
            log.info("partner:" + partner);

            AlipayConfig.key = key;
            AlipayConfig.partner = partner;
            String param_encoding = cfgparams.get("param_encoding");
            log.info("param_encoding:" + param_encoding);
            HttpServletRequest request = ThreadContextHolder.getHttpRequest();
            // 商户订单号
            String out_trade_no = new String(request.getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("out_trade_no:" + out_trade_no);

            // 支付宝交易号
            String trade_no = new String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            log.info("trade_no:" + trade_no);
            // 交易状态
            String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

            log.info("trade_status:" + trade_status);
            BigDecimal totalFee = new BigDecimal(new String(request.getParameter("total_fee").getBytes("ISO-8859-1"), "UTF-8"));//交易金额
            log.info("totalFee:" + totalFee);

            if (JavashopAlipayUtil.verify(param_encoding)) {// 验证成功
                if (trade_status.equals("TRADE_FINISHED") || trade_status.equals("TRADE_SUCCESS")) {
                    // 该页面可做页面美工编辑
                	try {
                		this.paySuccess(out_trade_no, trade_no, ordertype,totalFee);
                	} catch (RuntimeException e) {}
                }
                // 这个地方不用      return ("success");  么
                return out_trade_no;
            } else {
                throw new RuntimeException("支付宝二次验证失败");
            }
        } catch (Exception e) {
            log.error("支付宝即时到帐接口回调失败:"+e);
            throw new RuntimeException("支付宝即时到帐接口回调失败");
        }
    }
    

    @SuppressWarnings("unchecked")
	@Override
	public String onRefund(BigDecimal refundPrice, PayCfg payCfg, PayEnable order) {
    	Map<String, String> params = JSONObject.parseObject(payCfg.getConfig(), Map.class);
        AlipayConfig.key = params.get("key");							// 支付宝安全校验码(账户内提取)
        AlipayConfig.partner = params.get("partner");					// 支付宝合作伙伴id (账户内提取)
        AlipayConfig.seller_email = params.get("seller_email");			// 卖家支付宝帐户
        AlipayConfig.input_charset = params.get("content_encoding");	// 参数内容编码
    	
    	// 服务器异步通知页面路径
    	String notify_url = this.getRefundCallbackUrl(payCfg, order);
        // 退款当天日期（必填，格式：年[4位]-月[2位]-日[2位] 小时[2位 24小时制]:分[2位]:秒[2位]，如：2007-10-01 13:13:13）
        String refund_date = DateUtil.toString(new Date(), "yyyy-MM-dd HH:mm:ss");
        // 批次号（必填，格式：当天日期[8位]+序列号[3至24位]，如：201008010000001）
        String batch_no = order.getRefund_batchno();
        // 退款笔数（必填，参数detail_data的值中，“#”字符出现的数量加1，最大支持1000笔（即“#”字符出现的数量999个））
        String batch_num = "1";

        // 构建请求参数集合
        Map<String, String> sParaTemp = new HashMap<String, String>();
        sParaTemp.put("service", "refund_fastpay_by_platform_pwd");
        sParaTemp.put("partner", AlipayConfig.partner);
        sParaTemp.put("_input_charset", AlipayConfig.input_charset);
        sParaTemp.put("notify_url", notify_url);
        sParaTemp.put("seller_email", AlipayConfig.seller_email);
        sParaTemp.put("refund_date", refund_date);
        sParaTemp.put("batch_no", batch_no);
        sParaTemp.put("batch_num", batch_num);
        sParaTemp.put("detail_data", order.getTradeno() + "^" + refundPrice + "^" + "协商退款");

        // 构建请求表单
        return AlipaySubmit.buildRequest(sParaTemp, "POST", "确认");
	}

	@Override
    public String getId() {
        return "alipayDirectPlugin";
    }

    @Override
    public String getName() {
        return "支付宝即时到帐接口";
    }
}
