package com.enation.app.shop.core.service;

/**
 * 订单货物表状态:
 * 
 * @author dable
 * 
 */
public abstract class OrderItemStatus {

	/**
	 * ------------------------------------------------------------- 订单货物表状态:
	 * -------------------------------------------------------------
订单货物表状态:
0.正常
1.申请退货
2.申请换货  
3.退货已拒绝 
4.换货已拒绝 
5.退货已通过审核
6.换货已通过审核
7.退货已收到
8.换货已收到,换货已发出
9.退货完成
10.换货完成


	 */
	public static final int NORMAL = 0;
	public static final int APPLY_RETURN = 1;
	public static final int APPLY_CHANGE = 2;
	public static final int REFUSE_RETURN = 3;
	public static final int REFUSE_CHANGE = 4;
	public static final int RETURN_PASSED =5;
	public static final int CHANGE_PASSED =6;
	public static final int RETURN_REC =7;
	public static final int CHANGE_REC =8;
	public static final int RETURN_END =9;
	public static final int CHANGE_END =10;

	/**
	 * 获取退换货订单状态
	 * 
	 * @param status
	 * @return
	 */
	public static String getOrderStatusText(int status) {
		String text = "";
		switch (status) {
		case NORMAL:
			text = "正常";
			break;
		case APPLY_RETURN:
			text = "申请退货";
			break;
		case APPLY_CHANGE:
			text = "申请换货 ";
			break;
		case REFUSE_RETURN:
			text = "退货已拒绝";
			break;
		case REFUSE_CHANGE:
			text = "换货已拒绝";
			break;
		case RETURN_PASSED:
			text = "退货已通过审核";
			break;
		case CHANGE_PASSED:
			text = "换货已通过审核";
			break;
		case RETURN_REC:
			text = "退货已收货";
			break;			
		case CHANGE_REC:
			text = "已收货,换货已发出";
			break;
		case RETURN_END:
			text = "退货完成";
			break;
		case CHANGE_END:
			text = "换货完成";
			break;
		}
		return text;
	}
}
