package com.enation.app.shop.core.action.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;

import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.RequestUtil;

/**
 * 支付异步通知 action
 */
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("*payment-callback")
public class PaymentCallbackApiAction extends WWAction {
	private static final long serialVersionUID = 1L;

	/**
     * 支付回调
     *
     * @return 对应的页面
     */
    public String execute() {
        try {
            String url = RequestUtil.getRequestUrl(ThreadContextHolder.getHttpRequest());
            logger.info("支付回调:" + url);
            String[] params = this.getPluginId(url);
            if (params == null) {
                json = "参数不正确";
                logger.error("根据:" + url + "解析后参数不正确");
                return JSON_MESSAGE;
            }
            
            String orderType = params[0];	// 订单类型
            String pluginId = params[1];	// 组件ID
            if (StringUtils.isAnyBlank(params)) {
                json = "参数不正确";
                logger.error("根据:" + url + "解析后参数不正确" + "orderType:" + orderType + "	pluginId:" + pluginId);
                return JSON_MESSAGE;
            }
            logger.info("手机端支付订单类型:" + orderType);
            logger.info("手机端支付支付方式id:" + pluginId);

            // 根据不同的pluginId获得不同的组件 例如 id:AlipayDirect 返回支付宝PC端及时到账
            IPaymentEvent paymentPlugin = SpringContextHolder.getBean(pluginId);
            json = paymentPlugin.onCallBack(orderType);
            //这里日志有问题
            logger.debug("支付回调结果" + json);

        } catch (Exception e) {
            json = "error";
            logger.error("支付回调发生错误", e);
        }
        return JSON_MESSAGE;
    }

    /**
     * 根据规则处理回调地址.
     *
     * @param url 回调地址
     * @return 数组[] 返回 订单类型 组件ID
     */
    private String[] getPluginId(String url) {
        String pattern = ".*/(\\w+)_(\\w+)_(payment-callback).do(.*)";
        Pattern p = Pattern.compile(pattern, 2 | Pattern.DOTALL);
        Matcher matcher = p.matcher(url);
        if (matcher.find()) {
            try {
                return new String[] { 
                		matcher.replaceAll("$1"), 
                		matcher.replaceAll("$2")
                	};
            } catch (Exception e) {}
        }
        return null;
    }
}
