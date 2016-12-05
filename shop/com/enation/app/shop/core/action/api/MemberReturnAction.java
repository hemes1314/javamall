package com.enation.app.shop.core.action.api;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.enation.app.base.core.model.Member;
import com.enation.app.shop.component.orderreturns.service.IReturnsOrderManager;
import com.enation.app.shop.core.model.Order;
import com.enation.app.shop.core.model.OrderLog;
import com.enation.app.shop.core.model.ReturnsOrder;
import com.enation.app.shop.core.service.IGoodsManager;
import com.enation.app.shop.core.service.IMemberPointManger;
import com.enation.app.shop.core.service.IOrderManager;
import com.enation.app.shop.core.service.OrderItemStatus;
import com.enation.app.shop.core.service.OrderStatus;
import com.enation.eop.sdk.context.UserConext;
import com.enation.eop.sdk.utils.UploadUtil;
import com.enation.framework.action.WWAction;
import com.enation.framework.context.webcontext.ThreadContextHolder;
import com.enation.framework.util.StringUtil;


/**
 * 会员退换货申请，whj
 * (这个版本图片和文本信息不能同时提交，需要再实施)
 *2014-06-09 15:24分
 */
@SuppressWarnings({ "serial", "unused", "rawtypes" })
@Component
@Scope("prototype")
@ParentPackage("eop_default")
@Namespace("/api/shop")
@Action("returnorder")
public class MemberReturnAction extends WWAction{

	private IReturnsOrderManager returnsOrderManager;
	private IMemberPointManger memberPointManger;
	private IOrderManager orderManager;
	private IGoodsManager goodsManager;
	private String type;
	private String ordersn;
	private String applyreason;
	private String goodsns;
	private Long member_id;
	private File pic;
	private String picFileName;
	private File file ;
	private String fileFileName;
	private int rerurntype;
	private int orderid;
	
	/**
	*(这个版本暂时不支持上传图片，需要再实施)  whj
	* 需要传递type、ordersn、goodsns、applyreason参数
	* @type type：Integer型，退、换货类型，“1”表示退货，“2”表示换货    ,必须
	* @ordersn ordersn String型，订单号 ，必须
	* @goodsns goodsns String型，订单中商品货号，如果多个，使用“,”号隔开 ，必须
	* @applyreason applyreason ，String型， 申请理由， 选填。
	* @author 返回json串 “1”表示成功，“0”表示失败
	*2014-02-20 15:24分
	 */
	public String returnAdd() {
		ReturnsOrder returnOrder=new ReturnsOrder();
		//先上传图片
		String subFolder = "order";
		if(file!=null){
			//判断文件类型
			String allowTYpe = "gif,jpg,bmp,png";
			if (!fileFileName.trim().equals("") && fileFileName.length() > 0) {
				String ex = fileFileName.substring(fileFileName.lastIndexOf(".") + 1, fileFileName.length());
				if(allowTYpe.toString().indexOf(ex.toLowerCase()) < 0){
					this.showErrorJson("对不起,只能上传gif,jpg,bmp,png格式的图片！");
					return JSON_MESSAGE;
				}
			}
			//判断文件大小
			if(file.length() > 200 * 1024){
				this.showErrorJson("'对不起,图片不能大于200K！");
				return JSON_MESSAGE;
			}
			String imgPath=	UploadUtil.upload(file, fileFileName, subFolder);
			returnOrder.setPhoto(imgPath);
		}
		HttpServletRequest request = ThreadContextHolder.getHttpRequest();
		int type = rerurntype; //获得退货还是换货类型，type类型.
                //获得订单号;
		Order order = this.orderManager.get(ordersn);                     //
		Member member = UserConext.getCurrentMember();
		Long member_id;
		if(orderManager.get(ordersn)==null){
			this.showErrorJson("此订单不存在！");
			return JSON_MESSAGE;
		}else{
			if(order.getStatus().intValue()!=OrderStatus.ORDER_SHIP&&order.getStatus().intValue()!=OrderStatus.ORDER_ROG){
				this.showErrorJson("您的订单还没有发货！");
				return JSON_MESSAGE;
			}
			
			member_id = order.getMember_id();
			if (!member_id.equals(member.getMember_id())){
				this.showErrorJson("此订单号不是您的订单号！");
				return JSON_MESSAGE;
			}
		}
		ReturnsOrder tempReturnsOrder = this.returnsOrderManager.getByOrderSn(ordersn);
		if(tempReturnsOrder != null){
			this.showErrorJson("此订单已经申请过退货或换货，不能再申请！");
			return JSON_MESSAGE;
		}
         //获得goodsns; String goods = request.getParameter("goodsns");是正常获得form提交的值，但是这里提交了图片，就不能这么用了，不清楚为啥。
		String goods = goodsns;
			//goodsns是个数组		
		String[] goodsns;
		if (goods != null&&!goods.equals("")){
			goodsns = StringUtils.split(goods,",");
		}else{
			this.showErrorJson("您填写的货号为空！");
			return JSON_MESSAGE;
		}
		List<Map> items = orderManager.getItemsByOrderid(order.getOrder_id());
		if(items==null){
			this.showErrorJson("您的订单下没有货物！");
			return JSON_MESSAGE;
		}
		List<String> goodSnUnderOrder=new ArrayList<String>();
		for (int i = 0; i < items.size(); i++) {
			goodSnUnderOrder.add(goodsManager.getGoods((Integer)items.get(i).get("goods_id")).getSn());
		}
		for (int j = 0; j < goodsns.length; j++) {
			if(goodsns[j].indexOf("-") != -1){
				goodsns[j]=goodsns[j].substring(0, goodsns[j].indexOf("-"));
			}
		}
		for (int j = 0; j < goodsns.length; j++) {
			if(!goodSnUnderOrder.contains(goodsns[j])){
				this.showErrorJson("您所填写的所有货物号必须属于一个订单中！");
				return JSON_MESSAGE;
			}else{
				continue;
			}
		}
		int[] goodids=new int[goodsns.length];;
		if(goodsns!=null){
			for (int i = 0; i < goodsns.length; i++) {
				goodids[i]=this.goodsManager.getGoodBySn(goodsns[i]).getGoods_id();
			}
		}

        if(order.getStatus()==OrderStatus.ORDER_COMPLETE){
            this.showErrorJson("订单已解冻积分，不能再申请退款。");
            return JSON_MESSAGE;
        }
		returnOrder.setGoodsns(goods);
		returnOrder.setMemberid(member_id);
		returnOrder.setOrdersn(ordersn);
		returnOrder.setApply_reason(applyreason);
		returnOrder.setType(type);
		int orderid=orderManager.get(ordersn).getOrder_id();
		
		//写订单退换货日志
		OrderLog log = new OrderLog();
		if(type==1){
			log.setMessage("用户"+member.getUname()+"申请退货");
			log.setOp_name(member.getUname());
			log.setOp_id(member.getMember_id());
			log.setOrder_id(order.getOrder_id());
			this.returnsOrderManager.add(returnOrder,orderid,OrderItemStatus.APPLY_RETURN,goodids);	
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("退货，我们会在2个工作日内处理您的请求！");			
		}
		if(type==2){
			log.setMessage("用户"+member.getUname()+"申请退货");
			log.setOp_name(member.getUname());
			log.setOp_id(member.getMember_id());
			log.setOrder_id(order.getOrder_id());
			this.returnsOrderManager.add(returnOrder,orderid,OrderItemStatus.APPLY_CHANGE,goodids);			
			this.returnsOrderManager.addLog(log);
			this.showSuccessJson("换货申请已提交，我们会在2个工作日内处理您的请求！");
		}
		
		return JSON_MESSAGE;
	}

	
	/**
	 * 手动解冻积分 
	 */
	public String thaw() {
		try{
			HttpServletRequest request  = ThreadContextHolder.getHttpRequest();
			int orderid = StringUtil.toInt(request.getParameter("orderid"),true);
			this.memberPointManger.thaw(orderid);
			this.showSuccessJson("成功解冻");
		}catch(RuntimeException e){
			this.logger.error("手动解冻积分"+ e.getMessage(),e);
			this.showErrorJson(e.getMessage());
		}
		return WWAction.JSON_MESSAGE;
	}
	
	
	

	public Long getMember_id() {
		return member_id;
	}

	public void setMember_id(Long member_id) {
		this.member_id = member_id;
	}

	public String getOrdersn() {
		return ordersn;
	}

	public void setOrdersn(String ordersn) {
		this.ordersn = ordersn;
	}

	public String getApplyreason() {
		return applyreason;
	}

	public void setApplyreason(String applyreason) {
		this.applyreason = applyreason;
	}

	public String getGoodsns() {
		return goodsns;
	}

	public void setGoodsns(String goodsns) {
		this.goodsns = goodsns;
	}

	public IReturnsOrderManager getReturnsOrderManager() {
		return returnsOrderManager;
	}

	public void setReturnsOrderManager(IReturnsOrderManager returnsOrderManager) {
		this.returnsOrderManager = returnsOrderManager;
	}

	public IOrderManager getOrderManager() {
		return orderManager;
	}

	public void setOrderManager(IOrderManager orderManager) {
		this.orderManager = orderManager;
	}

	public IGoodsManager getGoodsManager() {
		return goodsManager;
	}

	public void setGoodsManager(IGoodsManager goodsManager) {
		this.goodsManager = goodsManager;
	}


	public File getPic() {
		return pic;
	}

	public void setPic(File pic) {
		this.pic = pic;
	}

	public String getPicFileName() {
		return picFileName;
	}

	public void setPicFileName(String picFileName) {
		this.picFileName = picFileName;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFileFileName() {
		return fileFileName;
	}

	public void setFileFileName(String fileFileName) {
		this.fileFileName = fileFileName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getRerurntype() {
		return rerurntype;
	}

	public void setRerurntype(int rerurntype) {
		this.rerurntype = rerurntype;
	}



	public IMemberPointManger getMemberPointManger() {
		return memberPointManger;
	}



	public void setMemberPointManger(IMemberPointManger memberPointManger) {
		this.memberPointManger = memberPointManger;
	}


}
