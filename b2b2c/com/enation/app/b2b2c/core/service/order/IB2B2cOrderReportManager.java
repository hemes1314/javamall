package com.enation.app.b2b2c.core.service.order;

import java.util.Map;

import com.enation.framework.database.Page;

/**
 * 订单单据管理接口
 * @author LiFenLong
 */
public interface IB2B2cOrderReportManager {

	/**
	 * 分页读取收款单<br>
	 * <li>对应表payment_logs</li>
	 * <li>读取字段type值为<b>1</b>的记录</li>
	 * <li>其中字段order_id和order表关联</li>
	 * <li>其中字段member_id和member表关联</li>
	 * 
	 * @param pageNo 当前页码 
	 * @param pageSize 每页分页数量
	 * 
	 * @param order 排序项<br>
	 * 接受如下格式参数：
	 * order_id desc 或 pay_user asc
	 * 默认为 payment_id desc
	 * @return  收款单分页列表，列表中的实体为： PaymentLog实体,<br> 
	 * 其member_name为非数据库字段，用于显示会员用户名<br>
	 * 如果非会员购买此属性为空
	 */
	public Page listPayment(Map map,int pageNo,int pageSize,String order);
	/**
	 * 分页读取退款单
    * @param pageNo 分页编号
    * @param pageSize 分页页数
    * @param order 排序
    * @param map 搜索条件
    */
   public Page listRefund(int pageNo, int pageSize, String order,Map map);
}
