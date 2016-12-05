package com.enation.app.shop.core.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 订单状态
 *
 * @author apexking
 */
public abstract class OrderStatus {

    /**
     * -------------------------------------------------------------
     * 订单状态
     * -------------------------------------------------------------
     */
    public static final int ORDER_RETURNED = -8;//已退货
    public static final int ORDER_CHANGED = -7;//已换货
    public static final int ORDER_CHANGE_APPLY = -4;//申请换货
    public static final int ORDER_RETURN_APPLY = -3; // 申请退货
    public static final int ORDER_CANCEL_SHIP = -2; // 退货
    public static final int ORDER_CANCEL_PAY = -1; // 退款
    public static final int ORDER_NOT_PAY = 0; // 未付款/新订单       修改为已确认
    public static final int ORDER_PAY = 1; // 已支付待确认
    public static final int ORDER_PAY_CONFIRM = 2; // 已确认支付
    public static final int ORDER_SHIP = 5; // 已发货
    public static final int ORDER_ROG = 6; // 已收货
    public static final int ORDER_COMPLETE = 7; // 已完成
    public static final int ORDER_CANCELLATION = 8; // 作废
    //
    public static final int ORDER_CHANGE_REFUSE = -6;//换货被拒绝
    public static final int ORDER_RETURN_REFUSE = -5;//退货被拒绝

    //以下为暂停使用的订单状态
    public static final int ORDER_ALLOCATION_YES = 4; //配货完成，待发货
    public static final int ORDER_NOT_CONFIRM = 9; //订单已生效
    public static final int ORDER_ALLOCATION = 3; //配货中

    public static final int ORDER_THOUSAND = 1000; //erp取走的状态


    /**
     * -------------------------------------------------------------
     * 付款状态
     * -------------------------------------------------------------
     */
    public static final int PAY_NO = 0;   //未付款
    public static final int PAY_YES = 1; //已付款待确认
    public static final int PAY_CONFIRM = 2; //已确认付款
    public static final int PAY_CANCEL = 3; //已退款
    public static final int PAY_PARTIAL_REFUND = 4; //部分退款
    public static final int PAY_PARTIAL_PAYED = 5;//部分付款


    /**
     * -------------------------------------------------------------
     * 货运状态
     * -------------------------------------------------------------
     */
    public static final int SHIP_ALLOCATION_NO = 0;  //	0未配货
    public static final int SHIP_ALLOCATION_YES = 1;  //	1配货中
    public static final int SHIP_NO = 2;  //	0未发货 （配货已完成）
    public static final int SHIP_YES = 3;//	1已发货
    public static final int SHIP_CANCEL = 4;//	2.已退货
    public static final int SHIP_PARTIAL_SHIPED = 5; //	4 部分发货
    public static final int SHIP_PARTIAL_CANCEL = 6;// 3 部分退货
    public static final int SHIP_PARTIAL_CHANGE = 7;// 5部分换货
    public static final int SHIP_CHANED = 8;// 6已换货
    public static final int SHIP_ROG = 9;// 9已收货

    /**
     * -------------------------------------------------------------
     * 申请退货退款状态
     * -------------------------------------------------------------
     */
    public static final int SQTK_STATUS = 13;  //申请退款
    public static final int JS_STATUS = 12;        // 用户拒收
    public static final int YJS_STATUS = 16;      // 已拒收
    public static final int YTK_STATUS = 17;     // 已退款
    public static final int TKBJ_STATUS = -15;  // 退款被拒绝
    public static final int JSBJ_STATUS= -12;    // 拒收被拒绝
    public static final int SQTHZ_STATUS= 18;    // 申请退货中
   
    /**
     * 获取订单状态值 map
     * key为状态变量名字串
     * value为状态值
     *
     * @return
     */
    public static Map<String, Object> getOrderStatusMap() {
        Map<String, Object> map = new HashMap<String, Object>(17);

        //订单状态
        map.put("ORDER_CHANGED", OrderStatus.ORDER_CHANGED);
        map.put("ORDER_CHANGE_REFUSE", OrderStatus.ORDER_CHANGE_REFUSE);
        map.put("ORDER_RETURN_REFUSE", OrderStatus.ORDER_RETURN_REFUSE);
        map.put("ORDER_CHANGE_APPLY", OrderStatus.ORDER_CHANGE_APPLY);
        map.put("ORDER_RETURN_APPLY", OrderStatus.ORDER_RETURN_APPLY);
        map.put("ORDER_CANCEL_SHIP", OrderStatus.ORDER_CANCEL_SHIP);
        map.put("ORDER_CANCEL_PAY", OrderStatus.ORDER_CANCEL_PAY);
        map.put("ORDER_NOT_PAY", OrderStatus.ORDER_NOT_PAY);
        map.put("ORDER_PAY", OrderStatus.ORDER_PAY);
        map.put("ORDER_PAY_CONFIRM", OrderStatus.ORDER_PAY_CONFIRM);
        map.put("ORDER_ALLOCATION", OrderStatus.ORDER_ALLOCATION);
        map.put("ORDER_ALLOCATION_YES", OrderStatus.ORDER_ALLOCATION_YES);
        map.put("ORDER_SHIP", OrderStatus.ORDER_SHIP);
        map.put("ORDER_ROG", OrderStatus.ORDER_ROG);
        map.put("ORDER_COMPLETE", OrderStatus.ORDER_COMPLETE);
        map.put("ORDER_CANCELLATION", OrderStatus.ORDER_CANCELLATION);
        map.put("ORDER_NOT_CONFIRM", OrderStatus.ORDER_NOT_CONFIRM);


        //付款状态
        map.put("PAY_NO", PAY_NO);
        map.put("PAY_YES", PAY_YES);
        map.put("PAY_CONFIRM", PAY_CONFIRM);
        map.put("PAY_CANCEL", PAY_CANCEL);
        map.put("PAY_PARTIAL_REFUND", PAY_PARTIAL_REFUND);
        map.put("PAY_PARTIAL_PAYED", PAY_PARTIAL_PAYED);


        //货运状态
        map.put("SHIP_ALLOCATION_NO", SHIP_ALLOCATION_NO);
        map.put("SHIP_ALLOCATION_YES", SHIP_ALLOCATION_YES);
        map.put("SHIP_NO", SHIP_NO);
        map.put("SHIP_YES", SHIP_YES);
        map.put("SHIP_CANCEL", SHIP_CANCEL);
        map.put("SHIP_PARTIAL_SHIPED", SHIP_PARTIAL_SHIPED);
        map.put("SHIP_PARTIAL_CANCEL", SHIP_YES);
        map.put("SHIP_PARTIAL_CHANGE", SHIP_CANCEL);
        map.put("SHIP_CHANED", SHIP_CHANED);
        map.put("SHIP_ROG", SHIP_ROG);


        return map;
    }

    /**
     * 获取订单状态
     *
     * @param status
     * @return
     */
    public static String getOrderStatusText(int status) {
        String text = "";

        switch (status) {
            case ORDER_RETURNED:
                text = "退货中";
                break;
            case ORDER_CHANGED:
                text = "已换货";
                break;
            case ORDER_CHANGE_REFUSE:
                text = "换货被拒绝";
                break;
            case ORDER_RETURN_REFUSE:
                text = "退货被拒绝";
                break;
            case ORDER_CHANGE_APPLY:
                text = "申请换货";
                break;
            case ORDER_RETURN_APPLY:
                text = "申请退货";
                break;
            case ORDER_CANCEL_SHIP:
                text = "已退货";
                break;
            case ORDER_CANCEL_PAY:
                text = "已退款";
                break;
            case ORDER_NOT_PAY:
                text = "未付款";
                break;
            case ORDER_NOT_CONFIRM:
                text = "订单已生效";
                break;
            case ORDER_PAY:
                text = "已付款待确认";
                break;
            case ORDER_PAY_CONFIRM:
                text = "已付款";
                break;
            case ORDER_ALLOCATION:
                text = "配货中";
                break;
            case ORDER_ALLOCATION_YES:
                text = "未发货";
                break;
            case ORDER_SHIP:
                text = "已发货";
                break;
            case ORDER_COMPLETE:
                text = "已完成";
                break;
            case ORDER_ROG:
                text = "已收货";
                break;
            case ORDER_CANCELLATION:
                text = "已取消";
                break;
            default:
                text = "错误状态";
                break;
        }
        return text;

    }


    /**
     * 获取付款状态
     *
     * @param status
     * @return
     */
    public static String getPayStatusText(int status) {
        String text = "";

        switch (status) {
            case PAY_NO:
                text = "未付款";
                break;
            case PAY_YES:
                text = "已付款待确认";
                break;
            case PAY_CONFIRM:
                text = "已确认付款";
                break;

            case PAY_CANCEL:
                text = "已经退款";
                break;
            case PAY_PARTIAL_REFUND:
                text = "部分退款";
                break;
            case PAY_PARTIAL_PAYED:
                text = "部分付款";
                break;
            default:
                text = "错误状态";
                break;
        }
        return text;
    }


    /**
     * 获取货运状态
     *
     * @param status
     * @return
     */
    public static String getShipStatusText(int status) {
        String text = "";

        switch (status) {
            case SHIP_ALLOCATION_NO:
                text = "未配货";
                break;
            case SHIP_ALLOCATION_YES:
                text = "配货中 ";
                break;

            case SHIP_NO:
                text = "未发货";
                break;

            case SHIP_YES:
                text = "已发货";
                break;

            case SHIP_CANCEL:
                text = "已退货";
                break;
            case SHIP_PARTIAL_SHIPED:
                text = "部分发货";
                break;
            case SHIP_PARTIAL_CANCEL:
                text = "部分退货";
                break;

            case SHIP_PARTIAL_CHANGE:
                text = "部分换货";
                break;

            case SHIP_CHANED:
                text = " 已换货";
                break;

            case SHIP_ROG:
                text = " 已收货";
                break;
            default:
                text = "错误状态";
                break;
        }

        return text;

    }


}
