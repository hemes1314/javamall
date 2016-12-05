package com.enation.app.b2b2c.core.action.api.payment;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.enation.app.b2b2c.core.model.member.StoreMember;
import com.enation.app.shop.core.model.PayCfg;
import com.enation.app.shop.core.model.PayEnable;
import com.enation.app.shop.core.plugin.payment.IPaymentEvent;
import com.enation.app.shop.core.service.IMemberGiftcardManager;
import com.enation.app.shop.core.service.IPaymentManager;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.spring.SpringContextHolder;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.DateUtil;
import com.enation.framework.util.StringUtil;

/**
 *
 * 店铺支付API  个人账号充值
 *
 * @author LiFenLong
 */
@Component
@ParentPackage("eop_default")
@Namespace("/api/b2b2c")
@Action("advanceTopupApi")
public class AdvanceTopupApiAction extends WWAction {
    @Autowired
    private IPaymentManager paymentManager;

    @Autowired
    private IMemberGiftcardManager giftcardManager;

    /**
     * 跳转到第三方支付页面
     *
     * @return html和脚本
     */
    public String execute() {
        HttpServletRequest request = ThreadContextHolder.getHttpRequest();
        StoreMember member = (StoreMember) ThreadContextHolder.getSessionContext().getAttribute("curr_store_member");
        if (member == null) {
            this.showErrorJson("请重新登录");
            return this.JSON_MESSAGE;
        }

        //支付方式id参数
        Integer paymentId = StringUtil.toInt(request.getParameter("paymentId"), null);
        if (paymentId == null) {
            this.showErrorJson("支付方式不正确");
            return this.JSON_MESSAGE;
        }

        if (paymentId == 9999) {
            String card_sn = request.getParameter("card_sn");
            if (card_sn == null || card_sn.length() < 14) {
                this.showErrorJson("卡号不正确");
                return this.JSON_MESSAGE;
            }
            String card_pw = request.getParameter("card_pw");
            if (card_pw == null || card_pw.length() < 6) {
                this.showErrorJson("密码不正确");
                return this.JSON_MESSAGE;
            }
            String err = giftcardManager.topup(card_sn, card_pw, member.getMember_id());
            if (err.equals("ok")) this.showSuccessJson("ok");
            else this.showErrorJson(err);

        } else {
            Double money = StringUtil.toDouble(request.getParameter("money"), null);
            if (money == null) {
                this.json = ("必须传递money参数");
                return this.JSON_MESSAGE;
            }
            int cent = (int) (money * 100);
            String sn = member.getMember_id() + "M" + cent + "N" + DateUtil.getDateline();
            TopupOrder order = new TopupOrder(money, sn);

            PayCfg payCfg = this.paymentManager.get(paymentId);
            IPaymentEvent paymentPlugin = SpringContextHolder.getBean(payCfg.getType());
            String payhtml = paymentPlugin.onPay(payCfg, order);

            this.json = (payhtml);
        }
        return this.JSON_MESSAGE;
    }

    public IPaymentManager getPaymentManager() {
        return paymentManager;
    }

    public void setPaymentManager(IPaymentManager paymentManager) {
        this.paymentManager = paymentManager;
    }


    class TopupOrder implements PayEnable {

        private Double money;
        private String topupSN;

        public TopupOrder(Double money, String sn) {
            this.money = money;
            this.topupSN = sn;
        }

        @Override
        public Double getNeedPayMoney() {
            return money;
        }

        @Override
        public String getSn() {
            return topupSN;
        }

        @Override
        public String getOrderType() {
            return "topup";
        }
        
        @Override
    	public String getTradeno() {
    		return "";
    	}
    	@Override
    	public String getRefund_batchno() {
    		return "";
    	}

		@Override
		public Integer getOrder_id() {
			return null;
		}

		@Override
		public Integer getParent_id() {
			return null;
		}
    	
    }

    ;
}
